package uz.greenwhite.smartup5_trade.m_order_info.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class OrderCurAccount {

    public final String currencyId;
    public final String currencyName;
    public final String paymentTypeId;
    public final String paymentTypeName;
    public final String amount;

    public OrderCurAccount(String currencyId,
                           String currencyName,
                           String paymentTypeId,
                           String paymentTypeName,
                           String amount) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.paymentTypeId = paymentTypeId;
        this.paymentTypeName = paymentTypeName;
        this.amount = amount;
    }

    public String getAmount() {
        try {
            return NumberUtil.formatMoney(new BigDecimal(amount));
        } catch (Exception e) {
            return amount;
        }
    }

    public static final UzumAdapter<OrderCurAccount> UZUM_ADAPTER = new UzumAdapter<OrderCurAccount>() {
        @Override
        public OrderCurAccount read(UzumReader in) {
            return new OrderCurAccount(in.readString(), in.readString(),
                    in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, OrderCurAccount val) {
            out.write(val.currencyId);
            out.write(val.currencyName);
            out.write(val.paymentTypeId);
            out.write(val.paymentTypeName);
            out.write(val.amount);
        }
    };
}
