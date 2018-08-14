package uz.greenwhite.smartup5_trade.m_order_info.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgOrderInfo extends ArgSession {

    public final String dealId;
    public final String state;

    public ArgOrderInfo(ArgSession arg, String dealId, String state) {
        super(arg.accountId, arg.filialId);
        this.dealId = dealId;
        this.state = state;
    }

    protected ArgOrderInfo(UzumReader in) {
        super(in);
        this.dealId = in.readString();
        this.state = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(dealId);
        w.write(state);
    }

    public static final UzumAdapter<ArgOrderInfo> UZUM_ADAPTER = new UzumAdapter<ArgOrderInfo>() {
        @Override
        public ArgOrderInfo read(UzumReader in) {
            return new ArgOrderInfo(in);
        }

        @Override
        public void write(UzumWriter out, ArgOrderInfo val) {
            val.write(out);
        }
    };
}
