package uz.greenwhite.smartup5_trade.m_vp_outlet.bean;// 12.12.2016

import java.util.Date;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OutletVisitPlan {

    public final String filialId;
    public final String roomId;
    public final String outletId;
    public final String firstWeekDate;
    public final String weekPlan;
    public final String mothPlan;
    public final String localId;

    public OutletVisitPlan(String filialId,
                           String roomId,
                           String outletId,
                           String firstWeekDate,
                           String weekPlan,
                           String mothPlan,
                           String localId) {
        this.filialId = filialId;
        this.roomId = roomId;
        this.outletId = outletId;
        this.firstWeekDate = firstWeekDate;
        this.weekPlan = weekPlan;
        this.mothPlan = mothPlan;
        this.localId = localId;
    }

    public static OutletVisitPlan makeDefault(String filialId, String roomId, String outletId) {
        String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return new OutletVisitPlan(filialId, roomId, outletId, today, "", "", "");
    }

    public OutletVisitPlan changeLocalId(String localId) {
        return new OutletVisitPlan(this.filialId, this.roomId, this.outletId,
                this.firstWeekDate, this.weekPlan, this.mothPlan, localId);
    }

    public static Tuple3 getKey(String filialId, String roomId, String outletId) {
        return new Tuple3(filialId, roomId, outletId);
    }

    public Tuple3 getKey() {
        return getKey(this.filialId, this.roomId, this.outletId);
    }

    public static final MyMapper<OutletVisitPlan, Tuple3> KEY_ADAPTER = new MyMapper<OutletVisitPlan, Tuple3>() {
        @Override
        public Tuple3 apply(OutletVisitPlan val) {
            return val.getKey();
        }
    };

    public static final UzumAdapter<OutletVisitPlan> UZUM_ADAPTER = new UzumAdapter<OutletVisitPlan>() {
        @Override
        public OutletVisitPlan read(UzumReader in) {
            return new OutletVisitPlan(in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OutletVisitPlan val) {
            out.write(val.filialId);
            out.write(val.roomId);
            out.write(val.outletId);
            out.write(val.firstWeekDate);
            out.write(val.weekPlan);
            out.write(val.mothPlan);
            out.write(val.localId);
        }
    };
}
