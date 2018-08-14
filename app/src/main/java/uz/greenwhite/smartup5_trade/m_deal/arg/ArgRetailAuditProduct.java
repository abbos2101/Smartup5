package uz.greenwhite.smartup5_trade.m_deal.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgRetailAuditProduct extends ArgDeal {

    public final String productId;

    public ArgRetailAuditProduct(ArgDeal arg, String productId) {
        super(arg, arg.roomId, arg.dealId, arg.location, arg.accuracy, arg.type);
        this.productId = productId;
    }

    public ArgRetailAuditProduct(UzumReader in) {
        super(in);
        this.productId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(productId);
    }

    public static final UzumAdapter<ArgRetailAuditProduct> UZUM_ADAPTER = new UzumAdapter<ArgRetailAuditProduct>() {
        @Override
        public ArgRetailAuditProduct read(UzumReader in) {
            return new ArgRetailAuditProduct(in);
        }

        @Override
        public void write(UzumWriter out, ArgRetailAuditProduct val) {
            val.write(out);
        }
    };
}
