package uz.greenwhite.smartup5_trade.m_shipped.variable.note;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SNote;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSDealNoteForm extends VSDealForm {

    public final SNote note;

    public VSDealNoteForm(SNote note) {
        super(new VisitModule(VisitModule.M_NOTE, false));
        this.note = note;
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
