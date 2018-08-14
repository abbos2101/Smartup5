package uz.greenwhite.smartup5_trade.m_deal.variable.note;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.NoteType;

public class VDealNoteType extends VariableLike {

    public final NoteType noteType;
    public final ValueString note;

    public VDealNoteType(NoteType noteType, String note) {
        this.noteType = noteType;
        this.note = new ValueString(200, note);
    }

    public boolean hasValue() {
        return note.nonEmpty();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(note).toSuper();
    }
}
