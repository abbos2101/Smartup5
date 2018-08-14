package uz.greenwhite.smartup5_trade.m_shipped.variable.pko;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentForm;

public class VSPKOModule extends VSDealModule {

    public final VSPKOForm form;

    public VSPKOModule(VisitModule module, VSDealPaymentForm form) {
        super(module);
        this.form = new VSPKOForm(module, form.payment);
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
        return MyArray.from(form).toSuper();
    }
}
