package uz.greenwhite.smartup5_trade.m_session.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class RoomExpeditor {

    public final String roomId;
    public final String userId;
    public final String name;

    public RoomExpeditor(String roomId, String userId, String name) {
        this.roomId = roomId;
        this.userId = userId;
        this.name = name;
    }

    public static final UzumAdapter<RoomExpeditor> UZUM_ADAPTER = new UzumAdapter<RoomExpeditor>() {
        @Override
        public RoomExpeditor read(UzumReader in) {
            return new RoomExpeditor(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, RoomExpeditor val) {
            out.write(val.roomId);
            out.write(val.userId);
            out.write(val.name);
        }
    };
}
