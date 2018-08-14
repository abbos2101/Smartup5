package uz.greenwhite.smartup5_trade.m_session.bean;// 28.07.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ProductBalance {

    public final String warehouseId;
    public final String productId;
    public final String cardCode;
    public final BigDecimal balance;
    public final String expireDate;

    public ProductBalance(String warehouseId,
                          String productId,
                          String cardCode,
                          BigDecimal balance,
                          String expireDate) {
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.cardCode = cardCode;
        this.balance = Util.nvl(balance, BigDecimal.ZERO);
        this.expireDate = Util.nvl(expireDate);
    }

    public static Tuple3 getKey(String warehouseId, String productId, String cardCode) {
        return new Tuple3(warehouseId, productId, cardCode);
    }

    public static final MyMapper<ProductBalance, Tuple3> KEY_ADAPTER = new MyMapper<ProductBalance, Tuple3>() {
        @Override
        public Tuple3 apply(ProductBalance v) {
            return getKey(v.warehouseId, v.productId, v.cardCode);
        }
    };

    public static final UzumAdapter<ProductBalance> UZUM_ADAPTER = new UzumAdapter<ProductBalance>() {
        @Override
        public ProductBalance read(UzumReader in) {
            return new ProductBalance(in.readString(), in.readString(),
                    in.readString(), in.readBigDecimal(), in.readString());
        }

        @Override
        public void write(UzumWriter out, ProductBalance val) {
            out.write(val.warehouseId);
            out.write(val.productId);
            out.write(val.cardCode);
            out.write(val.balance);
            out.write(val.expireDate);
        }
    };

}
