package uz.greenwhite.smartup5_trade.m_presentation.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_presentation.bean.PrPlan;
import uz.greenwhite.smartup5_trade.m_presentation.bean.VPrPlanSpeciality;

public class VPrPlan extends VariableLike {

    public final PrPlan cPrPlan;
    public final ValueString vDate;
    public final ValueString vTime;
    public final ValueString vFile;
    public final ValueSpinner vPerson;
    public final ValueString vPresentationName;
    public final ValueBigDecimal vEmployees;
    public final ValueArray<VPrPlanSpeciality> vSpecialties;
    public final ValueSpinner vProduct;
    public final ValueString vNote;

    public VPrPlan(PrPlan cPrPlan,
                   ValueString vDate,
                   ValueString vTime,
                   ValueString vFile,
                   ValueSpinner vPerson,
                   ValueString vPresentationName,
                   ValueBigDecimal vEmployees,
                   ValueArray<VPrPlanSpeciality> vSpecialties,
                   ValueSpinner vProduct,
                   ValueString vNote) {
        this.cPrPlan = cPrPlan;
        this.vDate = vDate;
        this.vTime = vTime;
        this.vFile = vFile;
        this.vPerson = vPerson;
        this.vPresentationName = vPresentationName;
        this.vEmployees = vEmployees;
        this.vSpecialties = vSpecialties;
        this.vProduct = vProduct;
        this.vNote = vNote;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(vDate, vTime, vFile, vPerson,
                vPresentationName, vEmployees, vSpecialties, vProduct, vNote);
    }
}
