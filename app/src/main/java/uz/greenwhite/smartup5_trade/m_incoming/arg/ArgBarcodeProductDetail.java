package uz.greenwhite.smartup5_trade.m_incoming.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ArgBarcodeProductDetail extends ArgIncoming {

    public final String productId;

    public ArgBarcodeProductDetail(ArgIncoming arg, String productId) {
        super(arg, arg.entryId);
        this.productId = productId;
    }

    protected ArgBarcodeProductDetail(UzumReader in) {
        super(in);
        this.productId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.productId);
    }

    public static final UzumAdapter<ArgBarcodeProductDetail> UZUM_ADAPTER = new UzumAdapter<ArgBarcodeProductDetail>() {
        @Override
        public ArgBarcodeProductDetail read(UzumReader in) {
            return new ArgBarcodeProductDetail(in);
        }

        @Override
        public void write(UzumWriter out, ArgBarcodeProductDetail val) {
            val.write(out);
        }
    };
}
