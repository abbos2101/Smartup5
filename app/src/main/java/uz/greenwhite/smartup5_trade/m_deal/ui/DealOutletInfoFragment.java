package uz.greenwhite.smartup5_trade.m_deal.ui;// 18.08.2016

import android.os.Parcelable;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.info.VDealInfoForm;
import uz.greenwhite.smartup5_trade.m_outlet.ui.InfoFragment;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class DealOutletInfoFragment extends InfoFragment {

    public ArgDeal getArgDeal() {
        return Mold.parcelableArgument(this, ArgDeal.UZUM_ADAPTER);
    }

    @Override
    public void onResume() {
        super.onResume();

        Parcelable data = Mold.getData(getActivity());
        if (data != null && data instanceof DealData) {
            ((DealData) data).vDeal.start();
        }
    }

    @Override
    public Outlet getOutlet() {
        VDealInfoForm form = DealUtil.getDealForm(this);
        return form.outlet;
    }

    @Override
    public ArgSession getArgument() {
        return getArgDeal();
    }
}
