package uz.greenwhite.smartup5_trade.m_movement.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class MovementProduct {

    public final String productName;
    public final String productCode;
    public final String cardName;
    public final String expireDate;
    public final BigDecimal quantity;
    public final BigDecimal price;

    public MovementProduct(String productName, String productCode, String cardName, String expireDate, BigDecimal quantity, BigDecimal price) {
        this.productName = productName;
        this.productCode = productCode;
        this.cardName = cardName;
        this.expireDate = expireDate;
        this.quantity = quantity;
        this.price = price;
    }

    public static final UzumAdapter<MovementProduct> UZUM_ADAPTER = new UzumAdapter<MovementProduct>() {
        @Override
        public MovementProduct read(UzumReader in) {
            return new MovementProduct(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, MovementProduct val) {
            out.write(val.productName);
            out.write(val.productCode);
            out.write(val.cardName);
            out.write(val.expireDate);
            out.write(val.quantity);
            out.write(val.price);
        }
    };
}
