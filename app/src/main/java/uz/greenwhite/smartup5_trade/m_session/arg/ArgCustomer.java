package uz.greenwhite.smartup5_trade.m_session.arg;// 25.11.2016

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgCustomer extends ArgSession {

    public final int formId;

    public ArgCustomer(ArgSession arg, int formId) {
        super(arg.accountId, arg.filialId);
        this.formId = formId;
    }

    public ArgCustomer(UzumReader in) {
        super(in);
        this.formId = in.readInt();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.formId);
    }

    public static final UzumAdapter<ArgCustomer> UZUM_ADAPTER = new UzumAdapter<ArgCustomer>() {
        @Override
        public ArgCustomer read(UzumReader in) {
            return new ArgCustomer(in);
        }

        @Override
        public void write(UzumWriter out, ArgCustomer val) {
            val.write(out);
        }
    };
}
