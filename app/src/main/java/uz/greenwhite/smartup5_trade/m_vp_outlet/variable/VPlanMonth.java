package uz.greenwhite.smartup5_trade.m_vp_outlet.variable;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;

public class VPlanMonth extends VariableLike {

    public final MyArray<VPlanDay> days;

    public VPlanMonth() {
        this.days = genDays();
    }


    public String toValue() {
        return days.filter(new MyPredicate<VPlanDay>() {
            @Override
            public boolean apply(VPlanDay d) {
                return d.checked.getValue();
            }
        })
                .mkString(",");
    }

    private static MyArray<VPlanDay> genDays() {
        List<VPlanDay> days = new ArrayList<VPlanDay>(32);
        for (int i = 1; i < 32; i++) {
            String s = String.valueOf(i);
            days.add(new VPlanDay(s, s));
        }
        return MyArray.from(days);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return days.toSuper();
    }
}
