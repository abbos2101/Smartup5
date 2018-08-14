package uz.greenwhite.smartup5_trade.m_shipped.variable.info;// 07.07.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSDealInfoForm extends VSDealForm {

    public final Outlet outlet;

    public VSDealInfoForm(SDealRef dealRef) {
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
