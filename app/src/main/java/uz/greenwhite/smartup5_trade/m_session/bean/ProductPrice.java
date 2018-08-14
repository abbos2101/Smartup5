package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.Utils;

public class ProductPrice {

    public final String productId;
    public final String priceTypeId;
    public final String cardCode;
    public final BigDecimal price;

    public ProductPrice(String productId,
                        String priceTypeId,
                        String cardCode,
                        BigDecimal price) {
        this.productId = productId;
        this.priceTypeId = priceTypeId;
        this.cardCode = Util.nvl(cardCode);
        this.price = price;
    }


    public static Tuple3 getKey(String priceTypeId, String productId, String cardCode) {
        return new Tuple3(priceTypeId, productId, cardCode);
    }

    public static final MyMapper<ProductPrice, Tuple3> KEY_ADAPTER = new MyMapper<ProductPrice, Tuple3>() {
        @Override
        public Tuple3 apply(ProductPrice val) {
            return getKey(val.priceTypeId, val.productId, val.cardCode);
        }
    };

    public static final UzumAdapter<ProductPrice> UZUM_ADAPTER = new UzumAdapter<ProductPrice>() {
        @Override
        public ProductPrice read(UzumReader in) {
            return new ProductPrice(in.readString(), in.readString(), in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, ProductPrice val) {
            out.write(val.productId);
            out.write(val.priceTypeId);
            out.write(val.cardCode);
            out.write(val.price);
        }
    };
}
