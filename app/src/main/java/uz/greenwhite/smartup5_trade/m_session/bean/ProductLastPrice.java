package uz.greenwhite.smartup5_trade.m_session.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductLastPrice {

    public final String productId;
    public final BigDecimal price;

    public ProductLastPrice(String productId, BigDecimal price) {
        this.productId = productId;
        this.price = Util.nvl(price, BigDecimal.ZERO);
    }

    public static final MyMapper<ProductLastPrice, String> KEY_ADAPTER = new MyMapper<ProductLastPrice, String>() {
        @Override
        public String apply(ProductLastPrice val) {
            return val.productId;
        }
    };

    public static final UzumAdapter<ProductLastPrice> UZUM_ADAPTER = new UzumAdapter<ProductLastPrice>() {
        @Override
        public ProductLastPrice read(UzumReader in) {
            return new ProductLastPrice(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, ProductLastPrice val) {
            out.write(val.productId);
            out.write(val.price);
        }
    };
}
