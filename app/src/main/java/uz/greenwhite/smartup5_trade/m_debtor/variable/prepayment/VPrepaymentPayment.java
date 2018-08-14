package uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;

public class VPrepaymentPayment extends VariableLike {

    public final PaymentType paymentType;
    public final ValueBigDecimal amount;

    public VPrepaymentPayment(PaymentType paymentType, BigDecimal amount) {
        this.paymentType = paymentType;
        this.amount = new ValueBigDecimal(10, 6);
        this.amount.setValue(amount);
    }

    public boolean hasValue() {
        return amount.nonZero();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(amount).toSuper();
    }

    public CharSequence tvTitle() {
        return paymentType.name;
    }
}
