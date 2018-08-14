package uz.greenwhite.smartup5_trade.m_presentation.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Specialty;

public class VPrPlanSpeciality extends VariableLike {

    public final Specialty specialty;
    public final ValueBoolean check;

    public VPrPlanSpeciality(Specialty specialty, ValueBoolean check) {
        this.specialty = specialty;
        this.check = check;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(check).toSuper();
    }

    @Override
    public String toString() {
        return specialty.name;
    }
}
