package uz.greenwhite.smartup5_trade.m_shipped.variable.error;// 07.07.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSDealErrorForm extends VSDealForm {

    public final CharSequence error;

    public VSDealErrorForm(SDealRef dealRef) {
        super(new VisitModule(VisitModule.M_ERROR, false));
        this.error = dealRef.holder.entryState.getErrorText();
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
