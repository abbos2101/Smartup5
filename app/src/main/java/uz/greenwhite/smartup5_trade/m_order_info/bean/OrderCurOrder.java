package uz.greenwhite.smartup5_trade.m_order_info.bean;


import java.math.BigDecimal;

import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OrderCurOrder {

    public final String currencyName;
    public final String priceName;
    public final String productName;
    public final String orderQuant;
    public final String orderPrice;
    public final String discountPercent;
    public final String warehouseName;

    public OrderCurOrder(String currencyName,
                         String priceName,
                         String productName,
                         String orderQuant,
                         String orderPrice,
                         String discountPercent,
                         String warehouseName) {
        this.currencyName = currencyName;
        this.priceName = priceName;
        this.productName = productName;
        this.orderQuant = orderQuant;
        this.orderPrice = orderPrice;
        this.discountPercent = discountPercent;
        this.warehouseName = warehouseName;
    }

    public String moneyFormat(String number) {
        try {
            return NumberUtil.formatMoney(new BigDecimal(number));
        } catch (Exception e) {
            return number;
        }
    }

    public static final UzumAdapter<OrderCurOrder> UZUM_ADAPTER = new UzumAdapter<OrderCurOrder>() {
        @Override
        public OrderCurOrder read(UzumReader in) {
            return new OrderCurOrder(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OrderCurOrder val) {
            out.write(val.currencyName);
            out.write(val.priceName);
            out.write(val.productName);
            out.write(val.orderQuant);
            out.write(val.orderPrice);
            out.write(val.discountPercent);
            out.write(val.warehouseName);
        }
    };
}
