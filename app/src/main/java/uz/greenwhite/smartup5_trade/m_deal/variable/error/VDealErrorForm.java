package uz.greenwhite.smartup5_trade.m_deal.variable.error;// 07.07.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealErrorForm extends VDealForm {

    public final CharSequence error;

    public VDealErrorForm(DealRef dealRef) {
        super(new VisitModule(VisitModule.M_ERROR, false));
        this.error = dealRef.dealHolder.entryState.getErrorText();
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
