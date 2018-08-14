package uz.greenwhite.smartup5_trade.m_session.bean;// 20.12.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Specialty {

    public final String id;
    public final String name;

    public Specialty(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final Specialty DEFAULT = new Specialty("", "");

    public static final MyMapper<Specialty, String> KEY_ADAPTER = new MyMapper<Specialty, String>() {
        @Override
        public String apply(Specialty region) {
            return region.id;
        }
    };

    public static final UzumAdapter<Specialty> UZUM_ADAPTER = new UzumAdapter<Specialty>() {
        @Override
        public Specialty read(UzumReader in) {
            return new Specialty(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Specialty val) {
            out.write(val.id);
            out.write(val.name);
        }
    };
}
