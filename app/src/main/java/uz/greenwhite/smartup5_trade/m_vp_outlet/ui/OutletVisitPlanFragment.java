package uz.greenwhite.smartup5_trade.m_vp_outlet.ui;// 12.12.2016

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_vp_outlet.VPOutletApi;
import uz.greenwhite.smartup5_trade.m_vp_outlet.arg.ArgVPOutlet;
import uz.greenwhite.smartup5_trade.m_vp_outlet.variable.VPlan;
import uz.greenwhite.smartup5_trade.m_vp_outlet.variable.VPlanDay;

public class OutletVisitPlanFragment extends MoldContentFragment {

    public static void open(ArgVPOutlet arg) {
        Mold.openContent(OutletVisitPlanFragment.class, Mold.parcelableArgument(arg, ArgVPOutlet.UZUM_ADAPTER));
    }

    public ArgVPOutlet getArgVPOutlet() {
        return Mold.parcelableArgument(this, ArgVPOutlet.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.outlet_visit_plan);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.outlet_visit_plan);


        ArgVPOutlet arg = getArgVPOutlet();
        boolean planEdit = OutletUtil.hasEditPlan(arg.getScope());

        if (planEdit) {
            addMenu(R.drawable.ic_save_black_24dp, R.string.save, new Command() {
                @Override
                public void apply() {
                    saveVisitPlan();
                }
            });
        }

        VPlanData data = Mold.getData(getActivity());
        if (data == null) {
            data = new VPlanData(arg.getOutletVisitPlan());
            Mold.setData(getActivity(), data);
        }

        vsRoot.bind(R.id.start_date, data.vPlan.startDate);
        if (planEdit) {
            vsRoot.makeDatePicker(R.id.start_date);
        } else {
            vsRoot.id(R.id.start_date).setEnabled(false);
        }
        initViews(data.vPlan.week1.days, 0, 5, planEdit);
        initViews(data.vPlan.week2.days, 1, 5, planEdit);
        initViews(data.vPlan.week3.days, 2, 5, planEdit);
        initViews(data.vPlan.week4.days, 3, 5, planEdit);
        initViews(data.vPlan.month.days, 4, 5, planEdit);

        final View hidenShowContent = vsRoot.id(R.id.ll_hiden_show_content);
        vsRoot.textView(R.id.tv_show_all).setText(
                hidenShowContent.getVisibility() == View.VISIBLE ? R.string.admin_hide : R.string.admin_detail);
        vsRoot.textView(R.id.tv_show_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidenShowContent.setVisibility(hidenShowContent.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE);
                vsRoot.textView(R.id.tv_show_all).setText(
                        hidenShowContent.getVisibility() == View.VISIBLE ? R.string.admin_hide : R.string.admin_detail);
            }
        });
    }

    private void saveVisitPlan() {
        FragmentActivity activity = getActivity();
        try {
            ArgVPOutlet arg = getArgVPOutlet();
            final VPlanData data = Mold.getData(activity);
            ErrorResult error = data.vPlan.getError();
            if (error.isError()) {
                String errorMessage = error.getErrorMessage();
                if (errorMessage.startsWith(VPlan.DATE_ERROR)) {
                    errorMessage = errorMessage.replace(VPlan.DATE_ERROR, "");
                    CharSequence errorMessageReplaceDate = UI.html().fRed().v(errorMessage).br()
                            .v(DS.getString(R.string.outlet_plan_set_tomorrow_date)).html();
                    UI.dialog()
                            .title(R.string.warning)
                            .message(errorMessageReplaceDate)
                            .positive(R.string.yes, new Command() {
                                @Override
                                public void apply() {
                                    int tomorrow = ((60 * 1000) * 60) * 24;
                                    Date date = new Date(System.currentTimeMillis() + tomorrow);
                                    data.vPlan.startDate.setValue(DateUtil.format(date, DateUtil.FORMAT_AS_DATE));
                                    saveVisitPlan();
                                }
                            })
                            .negative(R.string.no, Util.NOOP)
                            .show(getActivity());
                } else {
                    UI.alertError(activity, error.getErrorMessage());
                }
                return;
            }
            VPOutletApi.saveVisitPlan(arg.getScope(), data);

            Toast.makeText(activity, R.string.outlet_visit_plan_success_edited, Toast.LENGTH_SHORT).show();
            activity.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(activity, (String) ErrorUtil.getErrorMessage(e).message);
        }
    }

    private final int[][] IDS = {
            {R.id.week1_1, R.id.week1_2},
            {R.id.week2_1, R.id.week2_2},
            {R.id.week3_1, R.id.week3_2},
            {R.id.week4_1, R.id.week4_2},
            {R.id.month_1, R.id.month_2, R.id.month_3, R.id.month_4, R.id.month_5, R.id.month_6, R.id.month_7}};

    private void initViews(MyArray<VPlanDay> days, int idsIndex, int perLine, boolean hasPlanEdit) {
        int daysSize = days.size();
        int[] ids = IDS[idsIndex];
        perLine = adjustPerLine(daysSize, ids.length, perLine);

        Activity activity = getActivity();
        for (int i = 0; i < ids.length; i++) {
            LinearLayout ll = vsRoot.id(ids[i]);
            for (int j = 0, k = i * perLine; j < perLine && k < daysSize; j++, k++) {
                VPlanDay day = days.get(k);

                ViewSetup v = new ViewSetup(activity, R.layout.outlet_visit_plan_day);
                v.bind(R.id.checkbox, day.checked);
                v.textView(R.id.title).setText(day.title);
                ll.addView(v.view);
                v.id(R.id.checkbox).setEnabled(hasPlanEdit);
            }
        }
    }

    private int adjustPerLine(int daySize, int lineSize, int perLine) {
        int lineCount = daySize / perLine + (daySize % perLine != 0 ? 1 : 0);
        if (lineCount > lineSize) {
            return daySize / lineSize + (daySize % lineSize != 0 ? 1 : 0);
        }
        return perLine;
    }
}
