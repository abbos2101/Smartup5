package uz.greenwhite.smartup5_trade.m_vp_outlet.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class VPlanWeek extends VariableLike {

    public final MyArray<VPlanDay> days;

    public VPlanWeek() {
        this.days = MyArray.from(
                new VPlanDay("1", DS.getString(R.string.short_monday)),
                new VPlanDay("2", DS.getString(R.string.short_tuesday)),
                new VPlanDay("3", DS.getString(R.string.short_wednesday)),
                new VPlanDay("4", DS.getString(R.string.short_thursday)),
                new VPlanDay("5", DS.getString(R.string.short_friday)),
                new VPlanDay("6", DS.getString(R.string.short_saturday)),
                new VPlanDay("7", DS.getString(R.string.short_sunday)));
    }

    public String toValue() {
        return days.filter(new MyPredicate<VPlanDay>() {
            @Override
            public boolean apply(VPlanDay v) {
                return v.checked.getValue();
            }
        })
                .mkString(",");
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return days.toSuper();
    }
}
