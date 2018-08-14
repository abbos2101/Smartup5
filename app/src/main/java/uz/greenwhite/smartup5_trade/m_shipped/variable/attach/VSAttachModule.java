package uz.greenwhite.smartup5_trade.m_shipped.variable.attach;// 27.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SAttach;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;

public class VSAttachModule extends VSDealModule {

    public final VSAttachForm form;

    public VSAttachModule(VSAttachForm form) {
        super((VisitModule) form.tag);
        this.form = form;
    }

    public SAttach convertSAttach() {
        return new SAttach(
                form.contract.getValue(),
                form.invoice.getValue(),
                form.powerOfAttorney.getValue()
        );
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
