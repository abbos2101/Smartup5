package uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;

public class VPrepaymentCurrency extends VariableLike {

    public final Currency currency;
    public final ValueArray<VPrepaymentPayment> payments;

    public VPrepaymentCurrency(Currency currency, ValueArray<VPrepaymentPayment> payments) {
        this.currency = currency;
        this.payments = payments;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return payments.getItems().toSuper();
    }
}
