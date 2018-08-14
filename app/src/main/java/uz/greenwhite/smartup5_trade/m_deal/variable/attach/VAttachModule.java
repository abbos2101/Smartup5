package uz.greenwhite.smartup5_trade.m_deal.variable.attach;// 25.10.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VAttachModule extends VDealModule {

    public final VAttachForm form;

    public VAttachModule() {
        super(new VisitModule(VisitModule.M_ATTACH, false));
        this.form = new VAttachForm((VisitModule) tag);
    }

    @Override
    public DealModule convertToDealModule() {
        return null;
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        return form.hasValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }
}
