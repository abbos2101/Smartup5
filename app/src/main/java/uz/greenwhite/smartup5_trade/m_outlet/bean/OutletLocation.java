package uz.greenwhite.smartup5_trade.m_outlet.bean;// 14.10.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletLocation {

    public final String outletId;
    public final String location;

    public OutletLocation(String outletId, String location) {
        this.outletId = outletId;
        this.location = location;
    }

    public static final MyMapper<OutletLocation, String> KEY_ADAPTER = new MyMapper<OutletLocation, String>() {
        @Override
        public String apply(OutletLocation val) {
            return val.outletId;
        }
    };

    public static final UzumAdapter<OutletLocation> UZUM_ADAPTER = new UzumAdapter<OutletLocation>() {
        @Override
        public OutletLocation read(UzumReader in) {
            return new OutletLocation(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletLocation val) {
            out.write(val.outletId);
            out.write(val.location);
        }
    };
}
