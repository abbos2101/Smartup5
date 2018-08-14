package uz.greenwhite.smartup5_trade.m_shipped.variable.reasons;// 18.10.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_outlet.bean.ReturnReason;

public class VSReturnReason extends VariableLike {

    public final ReturnReason returnReason;
    public final ValueBoolean check = new ValueBoolean();

    public VSReturnReason(ReturnReason returnReason, boolean check) {
        this.returnReason = returnReason;
        this.check.setValue(check);
    }

    public boolean hasValue() {
        return this.check.getValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(check).toSuper();
    }
}
