package uz.greenwhite.smartup5_trade.m_incoming.bean;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;

public class IncomingHolder {

    public final Incoming incoming;
    public final EntryState state;

    public IncomingHolder(Incoming incoming, EntryState state) {
        this.incoming = incoming;
        this.state = state;
    }

    public static final UzumAdapter<IncomingHolder> UZUM_ADAPTER = new UzumAdapter<IncomingHolder>() {
        @Override
        public IncomingHolder read(UzumReader in) {
            return new IncomingHolder(in.readValue(Incoming.UZUM_ADAPTER),
                    in.readValue(EntryState.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, IncomingHolder val) {
            out.write(val.incoming, Incoming.UZUM_ADAPTER);
            out.write(val.state, EntryState.UZUM_ADAPTER);
        }
    };
}
