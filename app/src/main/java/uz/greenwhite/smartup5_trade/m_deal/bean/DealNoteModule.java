package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealNoteModule extends DealModule {

    public final MyArray<DealNote> notes;

    public DealNoteModule(MyArray<DealNote> notes) {
        super(VisitModule.M_NOTE);
        this.notes = notes;
        this.notes.checkUniqueness(DealNote.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealNoteModule(in.readArray(DealNote.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            out.write(((DealNoteModule) val).notes, DealNote.UZUM_ADAPTER);

        }
    };

}
