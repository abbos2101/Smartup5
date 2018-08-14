package uz.greenwhite.smartup5_trade.m_order_info.bean;


import java.math.BigDecimal;

import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OrderCurStock {

    public final String productName;
    public final String income;
    public final String stock;
    public final String market;
    public final String order;

    public OrderCurStock(String productName, String income, String stock, String market, String order) {
        this.productName = productName;
        this.income = income;
        this.stock = stock;
        this.market = market;
        this.order = order;
    }

    public String moneyFormat(String number) {
        try {
            return NumberUtil.formatMoney(new BigDecimal(number));
        } catch (Exception e) {
            return number;
        }
    }

    public static final UzumAdapter<OrderCurStock> UZUM_ADAPTER = new UzumAdapter<OrderCurStock>() {
        @Override
        public OrderCurStock read(UzumReader in) {
            return new OrderCurStock(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OrderCurStock val) {
            out.write(val.productName);
            out.write(val.income);
            out.write(val.stock);
            out.write(val.market);
            out.write(val.order);
        }
    };
}
