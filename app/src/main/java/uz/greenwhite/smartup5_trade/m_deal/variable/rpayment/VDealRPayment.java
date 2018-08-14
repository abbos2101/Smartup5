package uz.greenwhite.smartup5_trade.m_deal.variable.rpayment;// 07.10.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;

public class VDealRPayment extends VariableLike {

    public final PaymentType paymentType;
    public final ValueBigDecimal amount = new ValueBigDecimal(20, 6);

    public VDealRPayment(PaymentType paymentType, BigDecimal amount) {
        this.paymentType = paymentType;
        this.amount.setValue(amount);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(amount).toSuper();
    }

    public boolean hasValue() {
        return amount.nonEmpty();
    }
}
