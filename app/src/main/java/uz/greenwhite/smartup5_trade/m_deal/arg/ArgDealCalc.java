package uz.greenwhite.smartup5_trade.m_deal.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;


public class ArgDealCalc extends ArgDeal {

    public final String productId;

    public ArgDealCalc(ArgDeal arg, String productId) {
        super(arg, arg.roomId, arg.dealId, arg.location, arg.accuracy, arg.type);
        this.productId = productId;
    }

    public ArgDealCalc(UzumReader in) {
        super(in);
        this.productId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.productId);
    }

    public static final UzumAdapter<ArgDealCalc> UZUM_ADAPTER = new UzumAdapter<ArgDealCalc>() {
        @Override
        public ArgDealCalc read(UzumReader in) {
            return new ArgDealCalc(in);
        }

        @Override
        public void write(UzumWriter out, ArgDealCalc val) {
            val.write(out);
        }
    };
}
