package uz.greenwhite.smartup5_trade.m_deal.variable.error;// 04.08.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealErrorModule extends VDealModule {

    public final VDealErrorForm form;

    public VDealErrorModule(VDealErrorForm form) {
        super(new VisitModule(VisitModule.M_ERROR, false));
        this.form = form;
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
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }
}
