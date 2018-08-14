package uz.greenwhite.smartup5_trade.m_order_info.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgOrderPhoto extends ArgOrderInfo {

    public final String sha;

    public ArgOrderPhoto(ArgOrderInfo arg, String sha) {
        super(arg, arg.dealId, arg.state);
        this.sha = sha;
    }

    protected ArgOrderPhoto(UzumReader in) {
        super(in);
        this.sha = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(sha);
    }

    public static final UzumAdapter<ArgOrderPhoto> UZUM_ADAPTER = new UzumAdapter<ArgOrderPhoto>() {
        @Override
        public ArgOrderPhoto read(UzumReader in) {
            return new ArgOrderPhoto(in);
        }

        @Override
        public void write(UzumWriter out, ArgOrderPhoto val) {
            val.write(out);
        }
    };
}
