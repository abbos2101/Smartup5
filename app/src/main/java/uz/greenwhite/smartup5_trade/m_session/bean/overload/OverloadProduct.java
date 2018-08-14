package uz.greenwhite.smartup5_trade.m_session.bean.overload;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OverloadProduct {

    public final String productId;
    public final BigDecimal loadValue;

    public OverloadProduct(String productId, BigDecimal loadValue) {
        this.productId = productId;
        this.loadValue = loadValue;
    }

    public static final MyMapper<OverloadProduct, String> KEY_ADAPTER = new MyMapper<OverloadProduct, String>() {
        @Override
        public String apply(OverloadProduct overloadProduct) {
            return overloadProduct.productId;
        }
    };

    public static final UzumAdapter<OverloadProduct> UZUM_ADAPTER = new UzumAdapter<OverloadProduct>() {
        @Override
        public OverloadProduct read(UzumReader in) {
            return new OverloadProduct(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, OverloadProduct val) {
            out.write(val.productId);
            out.write(val.loadValue);
        }
    };
}
