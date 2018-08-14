package uz.greenwhite.smartup5_trade.m_deal.variable.agree;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealAgreeForm extends VDealForm {

    public final ValueArray<VDealAgree> agrees;

    public VDealAgreeForm(VisitModule module, ValueArray<VDealAgree> agrees) {
        super(module);
        this.agrees = agrees;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return agrees.getItems().toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VDealAgree agree : agrees.getItems()) {
            if (agree.hasValue()) {
                return true;
            }
        }
        return false;
    }

}
