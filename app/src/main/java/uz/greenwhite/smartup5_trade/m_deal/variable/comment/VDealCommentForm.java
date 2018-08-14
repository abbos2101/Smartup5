package uz.greenwhite.smartup5_trade.m_deal.variable.comment;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealCommentForm extends VDealForm {

    public final ValueArray<VDealComment> items;

    public VDealCommentForm(VisitModule module, ValueArray<VDealComment> items) {
        super(module);
        this.items = items;
    }

    @Override
    public boolean hasValue() {
        for (VDealComment c : items.getItems()) if (c.check.getValue()) return true;
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return items.getItems().toSuper();
    }
}
