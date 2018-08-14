package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class NoteType {

    public final String noteTypeId;
    public final String name;

    public NoteType(String noteTypeId, String name) {
        this.noteTypeId = noteTypeId;
        this.name = name;
    }

    public static final MyMapper<NoteType, String> KEY_ADAPTER = new MyMapper<NoteType, String>() {
        @Override
        public String apply(NoteType val) {
            return val.noteTypeId;
        }
    };

    public static final UzumAdapter<NoteType> UZUM_ADAPTER = new UzumAdapter<NoteType>() {

        @Override
        public NoteType read(UzumReader in) {
            return new NoteType(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, NoteType val) {
            out.write(val.noteTypeId);
            out.write(val.name);
        }
    };

}
