package uz.greenwhite.smartup5_trade.m_incoming.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgIncomingProduct extends ArgIncoming {

    public final String productId;

    public ArgIncomingProduct(ArgIncoming arg, String productId) {
        super(arg, arg.entryId);
        this.productId = productId;
    }

    protected ArgIncomingProduct(UzumReader in) {
        super(in);
        productId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(productId);
    }

    public static final UzumAdapter<ArgIncomingProduct> UZUM_ADAPTER = new UzumAdapter<ArgIncomingProduct>() {
        @Override
        public ArgIncomingProduct read(UzumReader in) {
            return new ArgIncomingProduct(in);
        }

        @Override
        public void write(UzumWriter out, ArgIncomingProduct val) {
            val.write(out);
        }
    };
}
