package uz.greenwhite.smartup5_trade.m_session.bean.action;


import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class BonusProduct {

    public static final String K_QUANTITY = "Q";
    public static final String K_DISCOUNT = "D";

    public final String productId;
    public final String bonusKind;
    public final BigDecimal bonusValue;
    public final BigDecimal maxValue;

    public BonusProduct(String productId, String bonusKind, BigDecimal bonusValue, BigDecimal maxValue) {
        this.productId = productId;
        this.bonusKind = bonusKind;
        this.bonusValue = bonusValue;
        this.maxValue = maxValue;
    }

    public static final UzumAdapter<BonusProduct> UZUM_ADAPTER = new UzumAdapter<BonusProduct>() {
        @Override
        public BonusProduct read(UzumReader in) {
            return new BonusProduct(in.readString(), in.readString(),
                    in.readBigDecimal(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, BonusProduct val) {
            out.write(val.productId);
            out.write(val.bonusKind);
            out.write(val.bonusValue);
            out.write(val.maxValue);
        }
    };
}
