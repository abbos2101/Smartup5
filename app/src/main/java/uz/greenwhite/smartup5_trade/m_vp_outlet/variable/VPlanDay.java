package uz.greenwhite.smartup5_trade.m_vp_outlet.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;

public class VPlanDay extends VariableLike {

    public final String day;
    public final CharSequence title;
    public final ValueBoolean checked = new ValueBoolean();

    public VPlanDay(String day, CharSequence title) {
        this.day = day;
        this.title = title;
    }

    @Override
    public String toString() {
        return day;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(checked).toSuper();
    }
}
