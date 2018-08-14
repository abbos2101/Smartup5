package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple4;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealOrder {

    public final String productId;
    public final String warehouseId;
    public final String priceTypeId;
    public final String cardCode;
    public final BigDecimal pricePerQuant;
    public final BigDecimal quantity;
    public final BigDecimal discount;
    public final String currencyId;
    public final MyArray<CardQuantity> orderQuantities;
    public final boolean isMml;
    public final String bonusId;
    public final String productUnitId;

    public DealOrder(String productId,
                     String warehouseId,
                     String priceTypeId,
                     String cardCode,
                     BigDecimal pricePerQuant,
                     BigDecimal quantity,
                     BigDecimal discount,
                     String currencyId,
                     MyArray<CardQuantity> orderQuantities,
                     Boolean isMml,
                     String bonusId,
                     String productUnitId) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.priceTypeId = priceTypeId;
        this.cardCode = cardCode;
        this.pricePerQuant = pricePerQuant;
        this.quantity = quantity;
        this.discount = discount;
        this.currencyId = Util.nvl(currencyId);
        this.orderQuantities = Util.nvl(orderQuantities, MyArray.<CardQuantity>emptyArray());
        this.isMml = Util.nvl(isMml, false);
        this.bonusId = Util.nvl(bonusId);
        this.productUnitId = Util.nvl(productUnitId);

        if (orderQuantities != null) {
            orderQuantities.checkUniqueness(CardQuantity.KEY_ADAPTER);
        }
    }

    public static Tuple4 getKey(String productId,
                                String warehouseId,
                                String priceId,
                                String cardCode) {
        return new Tuple4(productId, warehouseId, priceId, cardCode);
    }

    public static final MyMapper<DealOrder, Tuple4> KEY_ADAPTER = new MyMapper<DealOrder, Tuple4>() {
        @Override
        public Tuple4 apply(DealOrder val) {
            return DealOrder.getKey(val.productId, val.warehouseId, val.priceTypeId, val.cardCode);
        }
    };

    public static final UzumAdapter<DealOrder> UZUM_ADAPTER = new UzumAdapter<DealOrder>() {
        @Override
        public DealOrder read(UzumReader in) {
            return new DealOrder(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal(), in.readString(),
                    in.readArray(CardQuantity.UZUM_ADAPTER), in.readBoolean(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealOrder val) {
            out.write(val.productId);
            out.write(val.warehouseId);
            out.write(val.priceTypeId);
            out.write(val.cardCode);
            out.write(val.pricePerQuant);
            out.write(val.quantity);
            out.write(val.discount);
            out.write(val.currencyId);
            out.write(val.orderQuantities, CardQuantity.UZUM_ADAPTER);
            out.write(val.isMml);
            out.write(val.bonusId);
            out.write(val.productUnitId);
        }
    };
}
