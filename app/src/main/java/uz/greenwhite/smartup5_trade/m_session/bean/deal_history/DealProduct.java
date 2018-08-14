package uz.greenwhite.smartup5_trade.m_session.bean.deal_history;


import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealProduct {

    public final String productId;
    public final String quantity;
    public final BigDecimal price;
    public final BigDecimal totalPricePerProduct;
    public final String currencyId;
    public final String priceTypeId;
    public final String bonusId;
    public final String cardCode;
    public final String expiryDate;

    public DealProduct(String productId,
                       String quantity,
                       BigDecimal price,
                       BigDecimal totalPricePerProduct,
                       String currencyId,
                       String priceTypeId,
                       String bonusId,
                       String cardCode,
                       String expiryDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.totalPricePerProduct = totalPricePerProduct;
        this.currencyId = currencyId;
        this.priceTypeId = priceTypeId;
        this.bonusId = bonusId;
        this.cardCode = cardCode;
        this.expiryDate = expiryDate;
    }

    public static final UzumAdapter<DealProduct> UZUM_ADAPTER = new UzumAdapter<DealProduct>() {
        @Override
        public DealProduct read(UzumReader in) {
            return new DealProduct(
                    in.readString(), in.readString(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealProduct val) {
            out.write(val.productId);
            out.write(val.quantity);
            out.write(val.price);
            out.write(val.totalPricePerProduct);
            out.write(val.currencyId);
            out.write(val.priceTypeId);
            out.write(val.bonusId);
            out.write(val.cardCode);
            out.write(val.expiryDate);
        }
    };
}
