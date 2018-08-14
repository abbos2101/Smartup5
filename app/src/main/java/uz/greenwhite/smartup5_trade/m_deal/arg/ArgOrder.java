package uz.greenwhite.smartup5_trade.m_deal.arg;// 20.09.2016

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgOrder extends ArgDealCalc {

    public final String cardCode;

    public ArgOrder(ArgDeal arg, String productId, String cardCode) {
        super(arg, productId);
        this.cardCode = cardCode;
    }

    public ArgOrder(UzumReader in) {
        super(in);
        this.cardCode = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.cardCode);
    }

    public static final UzumAdapter<ArgOrder> UZUM_ADAPTER = new UzumAdapter<ArgOrder>() {
        @Override
        public ArgOrder read(UzumReader in) {
            return new ArgOrder(in);
        }

        @Override
        public void write(UzumWriter out, ArgOrder val) {
            val.write(out);
        }
    };
}
