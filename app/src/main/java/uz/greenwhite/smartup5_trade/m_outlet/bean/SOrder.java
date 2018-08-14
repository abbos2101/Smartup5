package uz.greenwhite.smartup5_trade.m_outlet.bean;// 08.09.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple4;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SOrder {

    public final String productId;
    public final String warehouseId;
    public final String priceTypeId;

    public final BigDecimal originQuant;
    public final BigDecimal deliverQuant;
    public final BigDecimal returnQuant;
    public final BigDecimal soldPrice;
    public final String marginKind;
    public final BigDecimal marginValue;
    public final String currencyId;
    public final String cardCode;

    public SOrder(String productId,
                  String warehouseId,
                  String priceTypeId,
                  BigDecimal originQuant,
                  BigDecimal deliverQuant,
                  BigDecimal returnQuant,
                  BigDecimal soldPrice,
                  String marginKind,
                  BigDecimal marginValue,
                  String currencyId,
                  String cardCode) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.priceTypeId = priceTypeId;
        this.originQuant = originQuant;
        this.deliverQuant = Util.nvl(deliverQuant, BigDecimal.ZERO);
        this.returnQuant = Util.nvl(returnQuant, BigDecimal.ZERO);
        this.soldPrice = soldPrice;
        this.marginKind = marginKind;
        this.marginValue = Util.nvl(marginValue, BigDecimal.ZERO);
        this.currencyId = currencyId;
        this.cardCode = cardCode;
    }

    public static Tuple4 getKey(String productId,
                                String warehouseId,
                                String priceId,
                                String cardCode) {
        return new Tuple4(productId, warehouseId, priceId, cardCode);
    }

    public static final MyMapper<SOrder, Tuple4> KEY_ADAPTER = new MyMapper<SOrder, Tuple4>() {
        @Override
        public Tuple4 apply(SOrder val) {
            return getKey(val.productId, val.warehouseId, val.priceTypeId, val.cardCode);
        }
    };

    public static final UzumAdapter<SOrder> UZUM_ADAPTER = new UzumAdapter<SOrder>() {
        @Override
        public SOrder read(UzumReader in) {
            return new SOrder(in.readString(), in.readString(), in.readString(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal(), in.readBigDecimal(),
                    in.readString(), in.readBigDecimal(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, SOrder val) {
            out.write(val.productId);    // 1
            out.write(val.warehouseId);  // 2
            out.write(val.priceTypeId);  // 3
            out.write(val.originQuant);  // 4
            out.write(val.deliverQuant); // 5
            out.write(val.returnQuant);  // 6
            out.write(val.soldPrice);   // 7
            out.write(val.marginKind);   // 8
            out.write(val.marginValue);  // 9
            out.write(val.currencyId);   // 10
            out.write(val.cardCode);     // 11
        }
    };
}
