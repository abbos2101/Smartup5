package uz.greenwhite.smartup5_trade.m_session.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.View;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldDialogFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.widget.ChartLine;
import uz.greenwhite.smartup5_trade.common.widget.VerticalChart;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgProductKpi;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardProductTypePlan;
import uz.greenwhite.smartup5_trade.m_session.row.DashboardProductKpiRow;

public class ProductKpiDialog extends MoldDialogFragment {

    public static void open(FragmentActivity activity, ArgProductKpi arg) {
        ProductKpiDialog d = Mold.parcelableArgumentNewInstance(ProductKpiDialog.class,
                Mold.parcelableArgument(arg, ArgProductKpi.UZUM_ADAPTER));
        d.show(activity.getSupportFragmentManager(), "product-kpi");
    }

    public ArgProductKpi getArgProductKpi() {
        return Mold.parcelableArgument(this, ArgProductKpi.UZUM_ADAPTER);
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArgProductKpi arg = getArgProductKpi();

        DashboardProductKpiRow first = arg.kpiRows.findFirst(new MyPredicate<DashboardProductKpiRow>() {
            @Override
            public boolean apply(DashboardProductKpiRow val) {
                if (!TextUtils.isEmpty(val.room.id)) {
                    return val.room.id.equals(arg.roomId) && val.planType.equals(arg.planType);
                } else {
                    return val.planType.equals(arg.planType);
                }
            }
        });

        DashboardProductKpiRow.Detail productPlan = MyArray.from(first.details).findFirst(new MyPredicate<DashboardProductKpiRow.Detail>() {
            @Override
            public boolean apply(DashboardProductKpiRow.Detail detail) {
                return detail.id.equals(arg.id);
            }
        });

        ViewSetup vsRoot = new ViewSetup(getActivity(), R.layout.dashboard_kpi_dialog);

        MyArray<ChartLine> lines = productPlan.generateChartLineForDialog();

        VerticalChart vcPlan = vsRoot.id(R.id.vc_plan_chart_plan);
        VerticalChart vcFact = vsRoot.id(R.id.vc_plan_chart_fact);
        VerticalChart vcPredict = vsRoot.id(R.id.vc_plan_chart_predict);

        if (DashboardProductTypePlan.K_PLAN_AKB.equals(arg.planType)) {
            vcPredict.setVisibility(View.GONE);
        }

        vcFact.setLine(lines.get(0));
        vcPredict.setLine(lines.get(1));
        vcPlan.setLine(lines.get(2));

        vsRoot.textView(R.id.tv_kpi_fact).setText(lines.get(0).text);
        vsRoot.textView(R.id.tv_kpi_predict).setText(lines.get(1).text);
        vsRoot.textView(R.id.tv_kpi_plan).setText(lines.get(2).text);

        return new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), uz.greenwhite.smartup.anor.R.style.Dialog))
                .setTitle(first.getTitle() + "\n" + productPlan.name)
                .setView(vsRoot.view)
                .create();
    }
}
