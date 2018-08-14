package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealNote {

    public final String noteTypeId;
    public final String value;

    public DealNote(String noteTypeId, String value) {
        this.noteTypeId = noteTypeId;
        this.value = value;
    }

    public static final MyMapper<DealNote, String> KEY_ADAPTER = new MyMapper<DealNote, String>() {
        @Override
        public String apply(DealNote val) {
            return val.noteTypeId;
        }
    };

    public static final UzumAdapter<DealNote> UZUM_ADAPTER = new UzumAdapter<DealNote>() {
        @Override
        public DealNote read(UzumReader in) {

            return new DealNote(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealNote val) {
            out.write(val.noteTypeId);
            out.write(val.value);
        }
    };
}
