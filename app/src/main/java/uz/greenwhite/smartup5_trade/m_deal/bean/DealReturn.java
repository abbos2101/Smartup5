package uz.greenwhite.smartup5_trade.m_deal.bean;// 06.10.2016

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealReturn {

    public final String warehouseId;
    public final String productId;
    public final String quantity;
    public final String price;
    public final String expiryDate;
    public final String cardCode;

    public DealReturn(String warehouseId,
                      String productId,
                      String quantity,
                      String price,
                      String expiryDate,
                      String cardCode) {
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
        this.cardCode = cardCode;
    }

    public BigDecimal tryDecimal(String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        return new BigDecimal(number);
    }

    public BigDecimal getQuantity() {
        return tryDecimal(this.quantity);
    }

    public BigDecimal getPrice() {
        return tryDecimal(this.price);
    }

    public static Tuple2 getKey(String warehouseId, String productId) {
        return new Tuple2(warehouseId, productId);
    }

    public static final UzumAdapter<DealReturn> UZUM_ADAPTER = new UzumAdapter<DealReturn>() {
        @Override
        public DealReturn read(UzumReader in) {
            return new DealReturn(in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealReturn val) {
            out.write(val.warehouseId);
            out.write(val.productId);
            out.write(val.quantity);
            out.write(val.price);
            out.write(val.expiryDate);
            out.write(val.cardCode);
        }
    };
}
