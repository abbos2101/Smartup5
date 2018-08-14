package uz.greenwhite.smartup5_trade.m_deal.bean;// 07.10.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealRPayment {

    public final String paymentTypeId;
    public final BigDecimal amount;

    public DealRPayment(String paymentTypeId, BigDecimal amount) {
        AppError.checkNull(paymentTypeId, amount);

        this.paymentTypeId = paymentTypeId;
        this.amount = amount;
    }

    public static MyMapper<DealRPayment, String> KEY_ADAPTER = new MyMapper<DealRPayment, String>() {
        @Override
        public String apply(DealRPayment dealRPayment) {
            return dealRPayment.paymentTypeId;
        }
    };

    public static final UzumAdapter<DealRPayment> UZUM_ADAPTER = new UzumAdapter<DealRPayment>() {
        @Override
        public DealRPayment read(UzumReader in) {
            return new DealRPayment(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, DealRPayment val) {
            out.write(val.paymentTypeId);
            out.write(val.amount);
        }
    };
}
