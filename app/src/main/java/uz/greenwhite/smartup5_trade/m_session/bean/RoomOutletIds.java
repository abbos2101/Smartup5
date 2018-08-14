package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class RoomOutletIds {

    public final String roomId;
    public final MyArray<String> outletIds;

    public RoomOutletIds(String roomId, MyArray<String> outletIds) {
        this.roomId = roomId;
        this.outletIds = outletIds;
    }

    public static final MyMapper<RoomOutletIds, String> KEY_ADAPTER = new MyMapper<RoomOutletIds, String>() {
        @Override
        public String apply(RoomOutletIds val) {
            return val.roomId;
        }
    };

    public static final UzumAdapter<RoomOutletIds> UZUM_ADAPTER = new UzumAdapter<RoomOutletIds>() {
        @Override
        public RoomOutletIds read(UzumReader in) {
            return new RoomOutletIds(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, RoomOutletIds val) {
            out.write(val.roomId);
            out.write(val.outletIds, STRING_ARRAY);
        }
    };
}
