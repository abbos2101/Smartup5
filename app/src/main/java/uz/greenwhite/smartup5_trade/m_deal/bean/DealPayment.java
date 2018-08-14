package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealPayment {

    public final String currencyId;
    public final String paymentTypeId;
    public final BigDecimal value;
    public final String consignmentAmount;
    public final String consignmentDate;

    public DealPayment(String currencyId,
                       String paymentTypeId,
                       BigDecimal value,
                       String consignmentAmount,
                       String consignmentDate) {
        AppError.checkNull(currencyId, paymentTypeId, value);
        this.currencyId = currencyId;
        this.paymentTypeId = paymentTypeId;
        this.value = value;
        this.consignmentAmount = Util.nvl(consignmentAmount);
        this.consignmentDate = consignmentDate;
    }

    public static Tuple2 getKey(String currencyId, String paymentTypeId) {
        return new Tuple2(currencyId, paymentTypeId);
    }

    public static final MyMapper<DealPayment, Tuple2> KEY_ADAPTER = new MyMapper<DealPayment, Tuple2>() {
        @Override
        public Tuple2 apply(DealPayment val) {
            return getKey(val.currencyId, val.paymentTypeId);
        }
    };

    public static final UzumAdapter<DealPayment> UZUM_ADAPTER = new UzumAdapter<DealPayment>() {
        @Override
        public DealPayment read(UzumReader in) {
            return new DealPayment(in.readString(), in.readString(), in.readBigDecimal(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, DealPayment val) {
            out.write(val.currencyId);
            out.write(val.paymentTypeId);
            out.write(val.value);
            out.write(val.consignmentAmount);
            out.write(val.consignmentDate);
        }
    };
}
