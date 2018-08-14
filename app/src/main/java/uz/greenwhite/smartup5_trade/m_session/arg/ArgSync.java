package uz.greenwhite.smartup5_trade.m_session.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgSync extends ArgSession {

    public final boolean shortSync;

    public ArgSync(ArgSession arg, boolean shortSync) {
        super(arg.accountId, arg.filialId);
        this.shortSync = shortSync;
    }

    public ArgSync(UzumReader in) {
        super(in);
        this.shortSync = in.readBoolean();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(shortSync);
    }

    public static final UzumAdapter<ArgSync> UZUM_ADAPTER = new UzumAdapter<ArgSync>() {
        @Override
        public ArgSync read(UzumReader uzumReader) {
            return new ArgSync(uzumReader);
        }

        @Override
        public void write(UzumWriter uzumWriter, ArgSync argSync) {
            argSync.write(uzumWriter);
        }
    };
}
