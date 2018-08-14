package uz.greenwhite.smartup5_trade.m_debtor.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgPrepaymentOutlet extends ArgSession {

    public final String paymentKind;

    public ArgPrepaymentOutlet(ArgSession arg, String paymentKind) {
        super(arg.accountId, arg.filialId);
        this.paymentKind = paymentKind;
    }

    public ArgPrepaymentOutlet(UzumReader in) {
        super(in);
        this.paymentKind = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(paymentKind);
    }

    public static final UzumAdapter<ArgPrepaymentOutlet> UZUM_ADAPTER = new UzumAdapter<ArgPrepaymentOutlet>() {
        @Override
        public ArgPrepaymentOutlet read(UzumReader in) {
            return new ArgPrepaymentOutlet(in);
        }

        @Override
        public void write(UzumWriter out, ArgPrepaymentOutlet val) {
            val.write(out);
        }
    };
}
