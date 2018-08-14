package uz.greenwhite.smartup5_trade.m_movement.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;

public class MovementIncomingHolder {

    public final MovementIncomingPost incoming;
    public final EntryState state;

    public MovementIncomingHolder(MovementIncomingPost incoming, EntryState state) {
        this.incoming = incoming;
        this.state = state;
    }

    public static final MyMapper<MovementIncomingHolder, String> KEY_ADAPTER = new MyMapper<MovementIncomingHolder, String>() {
        @Override
        public String apply(MovementIncomingHolder val) {
            return val.incoming.movementId;
        }
    };

    public static final UzumAdapter<MovementIncomingHolder> UZUM_ADAPTER = new UzumAdapter<MovementIncomingHolder>() {
        @Override
        public MovementIncomingHolder read(UzumReader in) {
            return new MovementIncomingHolder(in.readValue(MovementIncomingPost.UZUM_ADAPTER),
                    in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, MovementIncomingHolder val) {
            out.write(val.incoming, MovementIncomingPost.UZUM_ADAPTER);
            out.write(val.state, EntryState.UZUM_ADAPTER);
        }
    };
}
