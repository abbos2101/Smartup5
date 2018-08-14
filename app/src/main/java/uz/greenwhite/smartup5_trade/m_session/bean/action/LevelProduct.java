package uz.greenwhite.smartup5_trade.m_session.bean.action;// 07.12.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class LevelProduct {

    public final String productId;
    public final BigDecimal quantity;

    public LevelProduct(String productId, BigDecimal quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static final UzumAdapter<LevelProduct> UZUM_ADAPTER = new UzumAdapter<LevelProduct>() {
        @Override
        public LevelProduct read(UzumReader in) {
            return new LevelProduct(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, LevelProduct val) {
            out.write(val.productId);
            out.write(val.quantity);
        }
    };
}
