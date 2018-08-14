package uz.greenwhite.smartup5_trade.m_report.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgNewReport extends ArgSession {

    public final String uri;

    public ArgNewReport(ArgSession arg, String uri) {
        super(arg.accountId, arg.filialId);
        this.uri = uri;
    }

    protected ArgNewReport(UzumReader in) {
        super(in);
        this.uri = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(uri);
    }

    public static final UzumAdapter<ArgNewReport> UZUM_ADAPTER = new UzumAdapter<ArgNewReport>() {
        @Override
        public ArgNewReport read(UzumReader in) {
            return new ArgNewReport(in);
        }

        @Override
        public void write(UzumWriter out, ArgNewReport val) {
            val.write(out);
        }
    };
}
