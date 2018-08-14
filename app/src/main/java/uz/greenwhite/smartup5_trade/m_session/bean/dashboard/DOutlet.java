package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;// 28.12.2016

import android.support.annotation.NonNull;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DOutlet {

    public final String outletId;
    public final String name;
    public final String address;
    public final String type;
    public final String visitDate;

    public DOutlet(String outletId, String name, String address, String type, String visitDate) {
        this.outletId = outletId;
        this.name = name;
        this.address = address;
        this.type = type;
        this.visitDate = visitDate;
    }

    public boolean is(@NonNull String type) {
        return this.type.equals(type);
    }

    public static final MyMapper<DOutlet, String> KEY_ADAPTER = new MyMapper<DOutlet, String>() {
        @Override
        public String apply(DOutlet val) {
            return val.outletId;
        }
    };

    public static final UzumAdapter<DOutlet> UZUM_ADAPTER = new UzumAdapter<DOutlet>() {
        @Override
        public DOutlet read(UzumReader in) {
            return new DOutlet(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DOutlet val) {
            out.write(val.outletId);
            out.write(val.name);
            out.write(val.address);
            out.write(val.type);
            out.write(val.visitDate);
        }
    };
}
