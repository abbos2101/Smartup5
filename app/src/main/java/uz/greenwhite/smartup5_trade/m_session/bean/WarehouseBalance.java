package uz.greenwhite.smartup5_trade.m_session.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class WarehouseBalance {

    public final String warehouseId;
    public final String productId;
    public final String cardCode;
    public final String expireDate;
    public final BigDecimal balance;
    public final BigDecimal booked;

    public WarehouseBalance(String warehouseId,
                            String productId,
                            String cardCode,
                            String expireDate,
                            BigDecimal balance,
                            BigDecimal booked) {
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.cardCode = cardCode;
        this.expireDate = expireDate;
        this.balance = balance;
        this.booked = booked;
    }

    public static final UzumAdapter<WarehouseBalance> UZUM_ADAPTER = new UzumAdapter<WarehouseBalance>() {
        @Override
        public WarehouseBalance read(UzumReader in) {
            return new WarehouseBalance(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, WarehouseBalance val) {
            out.write(val.warehouseId);
            out.write(val.productId);
            out.write(val.cardCode);
            out.write(val.expireDate);
            out.write(val.balance);
            out.write(val.booked);
        }
    };
}
