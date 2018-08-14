package uz.greenwhite.smartup5_trade.m_duty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleMenu;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.ui.VisitPlanFragment;
import uz.greenwhite.smartup5_trade.send_my_location.SendMyLocationFragment;

public class DutyFragment extends MoldContentFragment {

    public static DutyFragment newInstance(ArgSession argSession) {
        return Mold.parcelableArgumentNewInstance(DutyFragment.class, argSession, ArgSession.UZUM_ADAPTER);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    public static final int SEND_MY_LOCATION = 1;
    public static final int VISIT_PLAN = 2;
    public static final int PRODUCT_PRICE = 3;
    public static final int FILAIL_ACTION = 4;

    public final MyArray<Duty> FORMS = MyArray.from(
            new Duty(SEND_MY_LOCATION, R.drawable.service_1, DS.getString(R.string.send_my_location)),
            new Duty(PRODUCT_PRICE, R.drawable.service_3, DS.getString(R.string.menu_prices)),
            new Duty(FILAIL_ACTION, R.drawable.service_3, DS.getString(R.string.menu_filial_action))
    );

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.z_card_view);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArgSession arg = getArgSession();
        Setting setting = arg.getSetting();
        MyArray<Duty> forms = FORMS;
//        boolean planEdit = OutletUtil.hasEditPlan(arg.getScope());
        if (setting.deal.visitAllow) {
            forms = forms.append(new Duty(VISIT_PLAN, R.drawable.service_2, DS.getString(R.string.session_visit_plan)));
        }

        MyArray<Duty> items = RoleMenu.sortForms(getArgSession(), RoleMenu.DUTY, forms, Duty.KEY_ADAPTER);

        ViewGroup vg = vsRoot.viewGroup(R.id.ll_card_view_row);
        vg.removeAllViews();
        for (final Duty item : items) {
            ViewSetup vs = new ViewSetup(getActivity(), R.layout.z_card_view_row);
            vs.imageView(R.id.iv_card_view_icon).setImageDrawable(item.icon);
            vs.textView(R.id.tv_card_view_title).setText(item.title);
            vs.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(item);
                }
            });
            vg.addView(vs.view);
        }
    }


    protected void onItemClick(Duty item) {
        switch (item.id) {
            case SEND_MY_LOCATION:
                SendMyLocationFragment.open(getArgSession());
                break;
            case VISIT_PLAN:
                VisitPlanFragment.open(getArgSession());
                break;
            case PRODUCT_PRICE:
                PriceFragment.open(getArgSession());
                break;
            case FILAIL_ACTION:
                FilialActionFragment.open(getActivity(), getArgSession());
                break;
        }
    }
}

