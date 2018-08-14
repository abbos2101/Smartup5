package uz.greenwhite.smartup5_trade.m_shipped.variable.overload;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SOverload;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;

public class VSOverloadModule extends VSDealModule {

    public final VSOverloadForm form;

    public VSOverloadModule(MyArray<SOverload> overloads) {
        super(new VisitModule(VisitModule.M_OVERLOAD, false));
        this.form = new VSOverloadForm((VisitModule) tag, overloads);
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
