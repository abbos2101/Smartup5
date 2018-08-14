package uz.greenwhite.smartup5_trade.m_deal.variable.memo;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealMemo;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealMemoModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealMemoModule extends VDealModule {

    public final VDealMemoForm form;

    public VDealMemoModule(VisitModule module, VDealMemoForm form) {
        super(module);
        this.form = form;
    }

    @Override
    public DealModule convertToDealModule() {
        DealMemo dealMemo = new DealMemo(form.memo.getValue());
        return new DealMemoModule(dealMemo);
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return MyArray.from((VForm) form);
    }

    @Override
    public boolean hasValue() {
        return form.hasValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).toSuper();
    }
}



