package uz.greenwhite.smartup5_trade.m_vp_outlet.variable;

import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_vp_outlet.bean.OutletVisitPlan;

public class VPlan extends VariableLike {

    public static final String DATE_ERROR = "date_error#";

    public final OutletVisitPlan visitPlan;
    public final ValueString startDate = new ValueString(15);
    public final VPlanWeek week1 = new VPlanWeek();
    public final VPlanWeek week2 = new VPlanWeek();
    public final VPlanWeek week3 = new VPlanWeek();
    public final VPlanWeek week4 = new VPlanWeek();
    public final VPlanMonth month = new VPlanMonth();

    public VPlan(OutletVisitPlan visitPlan) {
        this.visitPlan = visitPlan;
    }

    public OutletVisitPlan toValue() {
        return new OutletVisitPlan(
                this.visitPlan.filialId,
                this.visitPlan.roomId,
                this.visitPlan.outletId,
                startDate.getText(),
                MyArray.from(week1.toValue(), week2.toValue(), week3.toValue(), week4.toValue()).mkString(";"),
                month.toValue(),
                this.visitPlan.localId);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(startDate, week1, week2, week3, week4, month);
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }
        if (this.startDate.isEmpty()) {
            return ErrorResult.make(DATE_ERROR + DS.getString(R.string.outlet_plan_start_date_error));
        }

        String d = this.startDate.getText();
        String startDate = DateUtil.convert(d, DateUtil.FORMAT_AS_NUMBER);
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER);

        if (Integer.parseInt(startDate) < Integer.parseInt(today)) {
            return ErrorResult.make(DATE_ERROR + DS.getString(R.string.outlet_plan_start_date_error_today));
        }

        return ErrorResult.NONE;
    }
}
