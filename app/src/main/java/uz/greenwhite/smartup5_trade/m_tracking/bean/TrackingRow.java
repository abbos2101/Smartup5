package uz.greenwhite.smartup5_trade.m_tracking.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class TrackingRow {

    public final int userId;
    public final String name;
    public final String role;

    public TrackingRow(int userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public static final MyMapper<TrackingRow, Integer> KEY_ADAPTER = new MyMapper<TrackingRow, Integer>() {
        @Override
        public Integer apply(TrackingRow trackingRow) {
            return trackingRow.userId;
        }
    };

    public static final UzumAdapter<TrackingRow> UZUM_ADAPTER = new UzumAdapter<TrackingRow>() {

        @Override
        public TrackingRow read(UzumReader in) {
            return new TrackingRow(in.readInt(),in.readString(),in.readString());
        }

        @Override
        public void write(UzumWriter out, TrackingRow val) {
            out.write(val.userId);
            out.write(val.name);
            out.write(val.role);
        }


    };
}
