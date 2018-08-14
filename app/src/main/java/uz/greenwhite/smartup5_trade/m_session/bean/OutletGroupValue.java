package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletGroupValue {

    public final String groupId;
    public final String typeId;

    public OutletGroupValue(String groupId, String typeId) {
        this.groupId = groupId;
        this.typeId = typeId;
    }

    public static final MyMapper<OutletGroupValue, String> KEY_ADAPTER = new MyMapper<OutletGroupValue, String>() {
        @Override
        public String apply(OutletGroupValue val) {
            return val.groupId;
        }
    };

    public static final UzumAdapter<OutletGroupValue> UZUM_ADAPTER = new UzumAdapter<OutletGroupValue>() {
        @Override
        public OutletGroupValue read(UzumReader in) {
            return new OutletGroupValue(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletGroupValue val) {
            out.write(val.groupId);
            out.write(val.typeId);
        }
    };
}
