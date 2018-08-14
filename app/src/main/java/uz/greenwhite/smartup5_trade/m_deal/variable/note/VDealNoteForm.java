package uz.greenwhite.smartup5_trade.m_deal.variable.note;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealNoteForm extends VDealForm {

    public final ValueArray<VDealNoteType> noteTypes;

    public VDealNoteForm(VisitModule module, ValueArray<VDealNoteType> noteTypes) {
        super(module);
        this.noteTypes = noteTypes;
    }

    @Override
    public boolean hasValue() {
        for (VDealNoteType noteType : noteTypes.getItems()) {
            if (noteType.hasValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return noteTypes.getItems().toSuper();
    }
}
