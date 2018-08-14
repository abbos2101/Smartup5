package uz.greenwhite.smartup5_trade.m_session.bean;// 14.09.2016

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletPlan {

    public final String filialId;
    public final String outletId;
    public final String date;
    public final String localId;
    public final boolean created;
    public final String roomId;

    public OutletPlan(String filialId, String outletId, String date, String localId, Boolean created, String roomId) {
        this.filialId = filialId;
        this.outletId = outletId;
        this.date = date;
        this.localId = localId;
        this.created = Util.nvl(created, false);
        this.roomId = Util.nvl(roomId);
    }

    public static final OutletPlan DEFAULT = new OutletPlan("", "", "", "", null, null);

    public static String makeDeleteDate(String date) {
        return "d#" + date;
    }

    public static String getKey(String roomId, String outletId, String date) {
        return roomId + ":" + outletId + "#" + date;
    }

    public String getKey() {
        return getKey(roomId, outletId, date);
    }

    public String getDeletedKey() {
        return getKey(roomId, outletId, makeDeleteDate(date));
    }

    public static final MyMapper<OutletPlan, String> KEY_ADAPTER = new MyMapper<OutletPlan, String>() {
        @Override
        public String apply(OutletPlan val) {
            return val.getKey();
        }
    };

    public static final UzumAdapter<OutletPlan> UZUM_ADAPTER = new UzumAdapter<OutletPlan>() {
        @Override
        public OutletPlan read(UzumReader in) {
            return new OutletPlan(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readBoolean(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletPlan val) {
            out.write(val.filialId);
            out.write(val.outletId);
            out.write(val.date);
            out.write(val.localId);
            out.write(val.created);
            out.write(val.roomId);
        }
    };
}
