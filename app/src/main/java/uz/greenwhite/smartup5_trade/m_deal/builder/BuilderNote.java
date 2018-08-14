package uz.greenwhite.smartup5_trade.m_deal.builder;

import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealNote;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealNoteModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.note.VDealNoteForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.note.VDealNoteModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.note.VDealNoteType;
import uz.greenwhite.smartup5_trade.m_session.bean.NoteType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderNote {

    public final DealRef dealRef;
    public final VisitModule module;
    public final MyArray<DealNote> initial;

    public BuilderNote(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
    }

    private MyArray<DealNote> getInitial() {
        DealNoteModule dealModule = dealRef.findDealModule(module.id);
        return dealModule != null ? dealModule.notes : MyArray.<DealNote>emptyArray();
    }

    private MyArray<NoteType> getNoteTypes() {
        Set<String> noteTypeIds = dealRef.filialSetting.noteTypeIds.asSet();
        for (DealNote note : initial) {
            noteTypeIds.add(note.noteTypeId);
        }
        return MyArray.from(noteTypeIds).map(new MyMapper<String, NoteType>() {
            @Override
            public NoteType apply(String noteTypeId) {
                return dealRef.getNoteType(noteTypeId);
            }
        }).filterNotNull();
    }

    public VDealNoteForm makeNoteForms() {
        MyArray<VDealNoteType> result = getNoteTypes()
                .map(new MyMapper<NoteType, VDealNoteType>() {
                    @Override
                    public VDealNoteType apply(NoteType noteType) {
                        DealNote note = initial.find(noteType.noteTypeId, DealNote.KEY_ADAPTER);

                        String noteText = null;
                        if (note != null) {
                            noteText = note.value;
                        }
                        return new VDealNoteType(noteType, noteText);
                    }
                });

        return new VDealNoteForm(module, new ValueArray<>(result));
    }

    public VDealNoteModule build() {
        return new VDealNoteModule(module, makeNoteForms());
    }
}

