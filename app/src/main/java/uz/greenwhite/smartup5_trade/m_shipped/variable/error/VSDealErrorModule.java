package uz.greenwhite.smartup5_trade.m_shipped.variable.error;// 04.08.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;

public class VSDealErrorModule extends VSDealModule {

    public final VSDealErrorForm form;

    public VSDealErrorModule(VSDealErrorForm form) {
        super(new VisitModule(VisitModule.M_ERROR, false));
        this.form = form;
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
