package uz.greenwhite.smartup5_trade.m_debtor.variable.debtor;

import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;

public class VDebtorCurrency extends VariableLike {

    public final Currency currency;
    public final VDebtorPayment debtPayment;
    @Nullable
    public final VDebtorPayment consignPayment;

    public VDebtorCurrency(Currency currency,
                           VDebtorPayment debtPayment,
                           @Nullable VDebtorPayment consignPayment) {
        this.currency = currency;
        this.debtPayment = debtPayment;
        this.consignPayment = consignPayment;
    }

    public boolean hasValue() {
        return debtPayment.amount.nonZero() || (consignPayment != null && consignPayment.amount.nonZero());
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(debtPayment, consignPayment).filterNotNull().toSuper();
    }
}
