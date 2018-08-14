package uz.greenwhite.smartup5_trade.m_session.bean;// 20.12.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Hospital {

    public final String id;
    public final String name;

    public Hospital(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final Hospital DEFAULT = new Hospital("", "");

    public static final MyMapper<Hospital, String> KEY_ADAPTER = new MyMapper<Hospital, String>() {
        @Override
        public String apply(Hospital region) {
            return region.id;
        }
    };

    public static final UzumAdapter<Hospital> UZUM_ADAPTER = new UzumAdapter<Hospital>() {
        @Override
        public Hospital read(UzumReader in) {
            return new Hospital(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Hospital val) {
            out.write(val.id);
            out.write(val.name);
        }
    };
}
