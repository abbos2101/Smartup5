package uz.greenwhite.smartup5_trade.m_product.arg;// 16.08.2016

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class ArgProduct extends ArgSession {

    public final String productId;

    public ArgProduct(String accountId, String filialId, String productId) {
        super(accountId, filialId);
        this.productId = productId;
    }

    public ArgProduct(ArgSession arg, String productId) {
        super(arg.accountId, arg.filialId);
        this.productId = productId;
    }

    @SuppressWarnings("ConstantConditions")
    public Product getProduct() {
        return getScope().ref.getProduct(this.productId);
    }

    public static final UzumAdapter<ArgProduct> UZUM_ADAPTER = new UzumAdapter<ArgProduct>() {
        @Override
        public ArgProduct read(UzumReader in) {
            return new ArgProduct(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, ArgProduct val) {
            out.write(val.accountId);
            out.write(val.filialId);
            out.write(val.productId);
        }
    };
}
