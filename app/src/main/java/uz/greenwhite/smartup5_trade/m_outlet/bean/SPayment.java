package uz.greenwhite.smartup5_trade.m_outlet.bean;// 08.09.2016

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SPayment {

    public final String paymentTypeId;
    public final String currencyId;
    public final BigDecimal amount;
    public final String consignmentAmount;
    public final String consignmentDate;
    public final BigDecimal pkoAmount;

    public SPayment(String paymentTypeId,
                    String currencyId,
                    BigDecimal amount,
                    String consignmentAmount,
                    String consignmentDate,
                    BigDecimal pkoAmount) {
        this.paymentTypeId = paymentTypeId;
        this.currencyId = currencyId;
        this.amount = amount;
        this.consignmentAmount = Util.nvl(consignmentAmount);
        this.consignmentDate = consignmentDate;
        this.pkoAmount = Util.nvl(pkoAmount, BigDecimal.ZERO);
    }


    public BigDecimal getConsignment() {
        if (TextUtils.isEmpty(this.consignmentAmount)) {
            return null;
        }
        return new BigDecimal(this.consignmentAmount);
    }

    public static Tuple2 getKey(String currencyId, String paymentTypeId) {
        return new Tuple2(currencyId, paymentTypeId);
    }

    public static final MyMapper<SPayment, Tuple2> KEY_ADAPTER = new MyMapper<SPayment, Tuple2>() {
        @Override
        public Tuple2 apply(SPayment val) {
            return getKey(val.currencyId, val.paymentTypeId);
        }
    };

    public static final UzumAdapter<SPayment> UZUM_ADAPTER = new UzumAdapter<SPayment>() {
        @Override
        public SPayment read(UzumReader in) {
            return new SPayment(in.readString(), in.readString(), in.readBigDecimal(),
                    in.readString(), in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, SPayment val) {
            out.write(val.paymentTypeId);        // 1
            out.write(val.currencyId);           // 2
            out.write(val.amount);               // 3
            out.write(val.consignmentAmount);    // 4
            out.write(val.consignmentDate);      // 5
            out.write(val.pkoAmount);            // 6
        }
    };
}
