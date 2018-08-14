package uz.greenwhite.smartup5_trade.m_shipped.variable.note;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;

public class VSDealNoteModule extends VSDealModule {

    private VSDealNoteForm form;

    public VSDealNoteModule(VSDealNoteForm form) {
        super((VisitModule) form.tag);
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
