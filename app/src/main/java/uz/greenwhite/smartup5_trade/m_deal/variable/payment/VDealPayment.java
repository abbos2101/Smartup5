package uz.greenwhite.smartup5_trade.m_deal.variable.payment;// 15.07.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;

public class VDealPayment extends VariableLike {

    public final PaymentType paymentType;
    public final ValueBigDecimal amount = new ValueBigDecimal(20, 6);
    public final ValueBigDecimal consignmentAmount = new ValueBigDecimal(20, 6);
    public final ValueString consignmentDate = new ValueString(10);

    public VDealPayment(PaymentType paymentType,
                        BigDecimal amount,
                        BigDecimal consignmentAmount,
                        String consignmentDate) {
        this.paymentType = paymentType;
        this.amount.setValue(amount);
        this.consignmentAmount.setValue(consignmentAmount);
        this.consignmentDate.setValue(consignmentDate);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(this.amount,
                this.consignmentAmount, this.consignmentDate);
    }

    public boolean hasValue() {
        return amount.nonZero();
    }

    public boolean hasConsign() {
        return consignmentAmount.nonZero() || consignmentDate.nonEmpty();
    }

    public static final MyMapper<VDealPayment, String> KEY_ADAPTER = new MyMapper<VDealPayment, String>() {
        @Override
        public String apply(VDealPayment val) {
            return val.paymentType.id;
        }
    };
}
