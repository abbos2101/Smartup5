package uz.greenwhite.smartup5_trade.m_incoming.bean;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class IncomingProduct {

    public final String productId; // Продукция
    public final String cardNumber; // Номер карточки
    public final String manufacturerPrice; // Цена производителя BigDecimal
    public final BigDecimal quantity; // Количество
    public final String expireDate; // Срок годности
    public final String price; // Цена BigDecimal

    public IncomingProduct(String productId,
                           String cardNumber,
                           String manufacturerPrice,
                           BigDecimal quantity,
                           String expireDate,
                           String price) {
        this.productId = productId;
        this.cardNumber = cardNumber;
        this.manufacturerPrice = manufacturerPrice;
        this.quantity = quantity;
        this.expireDate = expireDate;
        this.price = price;
    }

    @Nullable
    public BigDecimal getManufacturerPrice() {
        if (TextUtils.isEmpty(this.manufacturerPrice)) {
            return null;
        }
        return new BigDecimal(this.manufacturerPrice);
    }

    @Nullable
    public BigDecimal getPrice() {
        if (TextUtils.isEmpty(this.price)) {
            return null;
        }
        return new BigDecimal(this.price);
    }

    public static final UzumAdapter<IncomingProduct> UZUM_ADAPTER = new UzumAdapter<IncomingProduct>() {
        @Override
        public IncomingProduct read(UzumReader in) {
            return new IncomingProduct(in.readString(),
                    in.readString(), in.readString(),
                    in.readBigDecimal(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, IncomingProduct val) {
            out.write(val.productId);
            out.write(val.cardNumber);
            out.write(val.manufacturerPrice);
            out.write(val.quantity);
            out.write(val.expireDate);
            out.write(val.price);
        }
    };
}
