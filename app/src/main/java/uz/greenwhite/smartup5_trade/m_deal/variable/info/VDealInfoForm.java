package uz.greenwhite.smartup5_trade.m_deal.variable.info;// 07.07.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealInfoForm extends VDealForm {

    public final Outlet outlet;

    public VDealInfoForm(DealRef dealRef) {
        super(new VisitModule(VisitModule.M_INFO, false));
        this.outlet = dealRef.outlet;
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }
}
