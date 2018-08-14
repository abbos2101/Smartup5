package uz.greenwhite.smartup5_trade.m_deal.variable.note;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealNote;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealNoteModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealNoteModule extends VDealModule {

    public final VDealNoteForm form;

    public VDealNoteModule(VisitModule module, VDealNoteForm form) {
        super(module);
        this.form = form;
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealNote> r = new ArrayList<>();
        for (VDealNoteType note : form.noteTypes.getItems()) {
            if (note.hasValue()) {
                r.add(new DealNote(note.noteType.noteTypeId, note.note.getValue()));
            }
        }
        return new DealNoteModule(MyArray.from(r));
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (form.noteTypes.getItems().isEmpty()) {
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
