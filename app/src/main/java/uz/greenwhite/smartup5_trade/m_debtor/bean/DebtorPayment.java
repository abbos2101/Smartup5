package uz.greenwhite.smartup5_trade.m_debtor.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DebtorPayment {

    public final String currencyId;                // 1
    public final String paymentTypeId;             // 2
    public final BigDecimal amount;                // 3
    public final BigDecimal consignAmount;         // 4

    public DebtorPayment(String currencyId, String paymentTypeId, BigDecimal amount, BigDecimal consignAmount) {
        this.currencyId = currencyId;
        this.paymentTypeId = paymentTypeId;
        this.amount = amount;
        this.consignAmount = Util.nvl(consignAmount, BigDecimal.ZERO);
    }

    public static Tuple2 getKey(String currencyId, String paymentTypeId) {
        return new Tuple2(currencyId, paymentTypeId);
    }

    public static final MyMapper<DebtorPayment, Tuple2> KEY_ADAPTER = new MyMapper<DebtorPayment, Tuple2>() {
        @Override
        public Tuple2 apply(DebtorPayment val) {
            return getKey(val.currencyId, val.paymentTypeId);
        }
    };

    public static final UzumAdapter<DebtorPayment> UZUM_ADAPTER = new UzumAdapter<DebtorPayment>() {
        @Override
        public DebtorPayment read(UzumReader in) {
            return new DebtorPayment(in.readString(), in.readString(),
                    in.readBigDecimal(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, DebtorPayment val) {
            out.write(val.currencyId);     // 1
            out.write(val.paymentTypeId);  // 2
            out.write(val.amount);         // 3
            out.write(val.consignAmount);  // 4
        }
    };
}
