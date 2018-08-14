package uz.greenwhite.smartup5_trade.m_shipped.variable.reasons;// 27.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSReturnReasonForm extends VSDealForm {

    public final ValueArray<VSReturnReason> reasones;

    public VSReturnReasonForm(VisitModule module, ValueArray<VSReturnReason> reasones) {
        super(module);
        this.reasones = reasones;
    }

    @Override
    public boolean hasValue() {
        for (VSReturnReason r : reasones.getItems()) {
            if (r.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return reasones.getItems().toSuper();
    }
}
