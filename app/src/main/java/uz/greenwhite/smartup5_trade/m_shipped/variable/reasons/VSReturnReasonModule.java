package uz.greenwhite.smartup5_trade.m_shipped.variable.reasons;// 27.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;

public class VSReturnReasonModule extends VSDealModule {

    public final VSReturnReasonForm form;

    public VSReturnReasonModule(VSReturnReasonForm form) {
        super((VisitModule) form.tag);
        this.form = form;
    }

    public String checkedReason() {
        VSReturnReason find = form.reasones.getItems().findFirst(new MyPredicate<VSReturnReason>() {
            @Override
            public boolean apply(VSReturnReason vsReason) {
                return vsReason.hasValue();
            }
        });
        if (find != null) {
            return find.returnReason.id;
        }
        return null;
    }

    public void unCheckAll() {
        for (VSReturnReason r : form.reasones.getItems()) {
            r.check.setValue(false);
        }
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (form.reasones.getItems().isEmpty()) {
            return MyArray.emptyArray();
        }
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
