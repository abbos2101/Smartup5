package uz.greenwhite.smartup5_trade.m_deal.variable.comment;

import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealCommentModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealCommentModule extends VDealModule {

    public final VDealCommentForm form;

    public VDealCommentModule(VisitModule module, VDealCommentForm form) {
        super(module);
        this.form = form;
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (form.items.getItems().isEmpty()) return MyArray.emptyArray();
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        return form.hasValue();
    }

    @Override
    public DealModule convertToDealModule() {
        ArrayList<String> items = new ArrayList<>();
        for (VDealComment c : form.items.getItems()) {
            if (c.check.getValue()) {
                items.add(c.comment.commentId);
            }
        }
        return new DealCommentModule(MyArray.from(items));
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).toSuper();
    }
}
