package uz.greenwhite.smartup5_trade.m_take_location.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgTakeMyLocation extends ArgSession {

    public final boolean requiredLocation;

    public ArgTakeMyLocation(ArgSession arg, boolean requiredLocation) {
        super(arg.accountId, arg.filialId);
        this.requiredLocation = requiredLocation;
    }

    public ArgTakeMyLocation(UzumReader in) {
        super(in);
        this.requiredLocation = in.readBoolean();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(requiredLocation);
    }

    public static final UzumAdapter<ArgTakeMyLocation> UZUM_ADAPTER = new UzumAdapter<ArgTakeMyLocation>() {
        @Override
        public ArgTakeMyLocation read(UzumReader in) {
            return new ArgTakeMyLocation(in);
        }

        @Override
        public void write(UzumWriter out, ArgTakeMyLocation val) {
            val.write(out);
        }
    };
}
