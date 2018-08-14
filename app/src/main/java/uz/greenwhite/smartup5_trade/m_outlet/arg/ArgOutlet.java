package uz.greenwhite.smartup5_trade.m_outlet.arg;// 29.06.2016

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class ArgOutlet extends ArgSession {

    public final String outletId;

    public ArgOutlet(ArgSession arg, String outletId) {
        super(arg.accountId, arg.filialId);
        this.outletId = outletId;
    }

    public ArgOutlet(UzumReader in) {
        super(in);
        this.outletId = in.readString();
    }

    public Outlet getOutlet() {
        return DSUtil.getOutlet(getScope(), outletId);
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.outletId);
    }

    public static final UzumAdapter<ArgOutlet> UZUM_ADAPTER = new UzumAdapter<ArgOutlet>() {
        @Override
        public ArgOutlet read(UzumReader in) {
            return new ArgOutlet(in);
        }

        @Override
        public void write(UzumWriter out, ArgOutlet val) {
            val.write(out);
        }
    };
}
