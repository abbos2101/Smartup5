package uz.greenwhite.smartup5_trade.m_deal.variable.agree;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.agree.DealAgree;
import uz.greenwhite.smartup5_trade.m_deal.bean.agree.DealAgreeModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealAgreeModule extends VDealModule {

    public final VDealAgreeForm form;

    public VDealAgreeModule(VisitModule module, VDealAgreeForm form) {
        super(module);
        this.form = form;
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (form == null || form.agrees.getItems().isEmpty()) return MyArray.emptyArray();
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        return form.hasValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).toSuper();
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealAgree> r = new ArrayList<>();

        for (VDealAgree dealAgree : form.agrees.getItems()) {
            if (dealAgree.hasValue()) {
                String productId = dealAgree.product.id;
                String oldCurValue = dealAgree.oldCurValue;
                String oldNewValue = dealAgree.oldNewValue;
                String curValue = dealAgree.curValue.getText();
                String newValue = dealAgree.newValue.getText();
                String period = dealAgree.period.getText();
                r.add(new DealAgree(productId, oldCurValue, oldNewValue, curValue, newValue, period));
            }
        }

        return new DealAgreeModule(MyArray.from(r));
    }
}
