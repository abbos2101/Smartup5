package uz.greenwhite.smartup5_trade.m_shipped.ui;// 18.08.2016

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.smartup5_trade.m_outlet.ui.InfoFragment;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.arg.ArgSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.variable.info.VSDealInfoForm;

public class SOutletInfoFragment extends InfoFragment {

    public ArgSDeal getArgsDeal() {
        return Mold.parcelableArgument(this, ArgSDeal.UZUM_ADAPTER);
    }

    @Override
    public Outlet getOutlet() {
        VSDealInfoForm form = ShippedUtil.getDealForm(this);
        return form.outlet;
    }

    @Override
    public ArgSession getArgument() {
        return getArgsDeal();
    }
}
