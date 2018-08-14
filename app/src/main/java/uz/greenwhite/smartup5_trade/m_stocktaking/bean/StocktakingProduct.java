package uz.greenwhite.smartup5_trade.m_stocktaking.bean;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class StocktakingProduct {

    public final String productId; // Продукция
    public final String cardCode;
    public final String expireDate;
    public final BigDecimal quantity; // Количество
    public final BigDecimal stocktakingQuantity; // Корректировка
    public final String price; // Цена BigDecimal

    public StocktakingProduct(String productId,
                              String cardCode,
                              String expireDate,
                              BigDecimal quantity,
                              BigDecimal stocktakingQuantity,
                              String price) {
        this.productId = productId;
        this.cardCode = cardCode;
        this.expireDate = expireDate;
        this.quantity = quantity;
        this.stocktakingQuantity = stocktakingQuantity;
        this.price = price;
    }

    @Nullable
    public BigDecimal getPrice() {
        if (TextUtils.isEmpty(this.price)) {
            return null;
        }
        return new BigDecimal(this.price);
    }

    public static final UzumAdapter<StocktakingProduct> UZUM_ADAPTER = new UzumAdapter<StocktakingProduct>() {
        @Override
        public StocktakingProduct read(UzumReader in) {
            return new StocktakingProduct(in.readString(),
                    in.readString(), in.readString(),
                    in.readBigDecimal(), in.readBigDecimal(), in.readString());
        }

        @Override
        public void write(UzumWriter out, StocktakingProduct val) {
            out.write(val.productId);
            out.write(val.cardCode);
            out.write(val.expireDate);
            out.write(val.quantity);
            out.write(val.stocktakingQuantity);
            out.write(val.price);
        }
    };
}
