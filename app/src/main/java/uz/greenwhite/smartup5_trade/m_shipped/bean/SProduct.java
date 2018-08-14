package uz.greenwhite.smartup5_trade.m_shipped.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SProduct {

    public final String productId;
    public final BigDecimal lockQuant;

    public SProduct(String productId, BigDecimal lockQuant) {
        this.productId = productId;
        this.lockQuant = lockQuant;
    }

    public static final MyMapper<SProduct, String> KEY_ADAPTER = new MyMapper<SProduct, String>() {
        @Override
        public String apply(SProduct sProduct) {
            return sProduct.productId;
        }
    };

    public static final UzumAdapter<SProduct> UZUM_ADAPTER = new UzumAdapter<SProduct>() {
        @Override
        public SProduct read(UzumReader in) {
            return new SProduct(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, SProduct val) {
            out.write(val.productId);
            out.write(val.lockQuant);
        }
    };
}
