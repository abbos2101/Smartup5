package uz.greenwhite.smartup5_trade.m_session.bean;// 05.09.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletGroup {

    public final String groupId;
    public final String name;

    public OutletGroup(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

    public static final MyMapper<OutletGroup, String> KEY_ADAPTER = new MyMapper<OutletGroup, String>() {
        @Override
        public String apply(OutletGroup val) {
            return val.groupId;
        }
    };

    public static final UzumAdapter<OutletGroup> UZUM_ADAPTER = new UzumAdapter<OutletGroup>() {
        @Override
        public OutletGroup read(UzumReader in) {
            return new OutletGroup(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletGroup val) {
            out.write(val.groupId);
            out.write(val.name);
        }
    };
}
