package uz.greenwhite.smartup5_trade.m_shipped.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SOverload {

    public final String productId;
    public final String productName;
    public final BigDecimal soldQuant;
    public final BigDecimal soldPrice;
    public final BigDecimal soldTotalAmount;

    public SOverload(String productId,
                     String productName,
                     BigDecimal soldQuant,
                     BigDecimal soldPrice,
                     BigDecimal soldTotalAmount) {
        this.productId = productId;
        this.productName = productName;
        this.soldQuant = soldQuant;
        this.soldPrice = soldPrice;
        this.soldTotalAmount = soldTotalAmount;
    }

    public static final UzumAdapter<SOverload> UZUM_ADAPTER = new UzumAdapter<SOverload>() {
        @Override
        public SOverload read(UzumReader in) {
            return new SOverload(in.readString(),
                    in.readString(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, SOverload val) {
            out.write(val.productId);
            out.write(val.productName);
            out.write(val.soldQuant);
            out.write(val.soldPrice);
            out.write(val.soldTotalAmount);
        }
    };
}
