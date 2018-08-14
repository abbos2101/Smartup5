package uz.greenwhite.smartup5_trade.m_movement.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class MovementIncomingPost {

    public final String entryId;
    public final String movementId;
    public final String date;
    public final String warehouseId;

    public MovementIncomingPost(String entryId, String movementId, String date, String warehouseId) {
        this.entryId = entryId;
        this.movementId = movementId;
        this.date = date;
        this.warehouseId = warehouseId;
    }

    public static final UzumAdapter<MovementIncomingPost> UZUM_ADAPTER = new UzumAdapter<MovementIncomingPost>() {
        @Override
        public MovementIncomingPost read(UzumReader in) {
            return new MovementIncomingPost(in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, MovementIncomingPost val) {
            out.write(val.entryId);
            out.write(val.movementId);
            out.write(val.date);
            out.write(val.warehouseId);
        }
    };
}
