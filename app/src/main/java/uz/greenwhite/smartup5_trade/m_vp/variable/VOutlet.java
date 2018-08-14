package uz.greenwhite.smartup5_trade.m_vp.variable;// 23.09.2016

import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;

public class VOutlet extends VariableLike {

    public final ValueBoolean check = new ValueBoolean();
    public final OutletPlan visit;
    public final Outlet outlet;
    public final CharSequence title;
    public final CharSequence detail;

    public VOutlet(OutletPlan visit, Outlet outlet, boolean check, @Nullable CharSequence detail) {
        this.visit = Util.nvl(visit, OutletPlan.DEFAULT);
        this.outlet = outlet;
        this.check.setValue(check);

        this.title = outlet.name;
        this.detail = Util.nvl(detail, outlet.address);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(check).toSuper();
    }

}
