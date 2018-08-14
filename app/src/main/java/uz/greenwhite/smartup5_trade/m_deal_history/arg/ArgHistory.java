package uz.greenwhite.smartup5_trade.m_deal_history.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgHistory extends ArgSession {

    public final String outletId;

    public ArgHistory(ArgSession arg, String outletId) {
        super(arg.accountId, arg.filialId);
        this.outletId = outletId;
    }

    public ArgHistory(ArgSession arg) {
        super(arg.accountId, arg.filialId);
        this.outletId = "";
    }

    public ArgHistory(UzumReader in) {
        super(in);
        this.outletId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(outletId);
    }

    public static final UzumAdapter<ArgHistory> UZUM_ADAPTER = new UzumAdapter<ArgHistory>() {
        @Override
        public ArgHistory read(UzumReader uzumReader) {
            return new ArgHistory(uzumReader);
        }

        @Override
        public void write(UzumWriter uzumWriter, ArgHistory argHistory) {
            argHistory.write(uzumWriter);
        }
    };
}
