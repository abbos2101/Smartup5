package uz.greenwhite.smartup5_trade.m_shipped.bean;


import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SNote {
    public final String visitId;
    public final String noteTypeId;
    public final String filialId;
    public final String note;

    public SNote(String visitId, String noteTypeId, String filialId, String note) {
        this.visitId = visitId;
        this.noteTypeId = noteTypeId;
        this.filialId = filialId;
        this.note = note;
    }

    public static final UzumAdapter<SNote> UZUM_ADAPTER = new UzumAdapter<SNote>() {
        @Override
        public SNote read(UzumReader in) {
            return new SNote(in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, SNote val) {
            out.write(val.visitId);
            out.write(val.noteTypeId);
            out.write(val.filialId);
            out.write(val.note);
        }
    };
}
