package uz.greenwhite.smartup5_trade.m_debtor.variable.debtor;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;

public class VDebtorPayment extends VariableLike {

    public final PaymentType paymentType;
    public final boolean consignment;
    public final BigDecimal debtAmount;
    public final ValueBigDecimal amount;

    public VDebtorPayment(PaymentType paymentType,
                          boolean consignment,
                          BigDecimal debtAmount,
                          BigDecimal amount) {
        this.paymentType = paymentType;
        this.consignment = consignment;
        this.debtAmount = debtAmount;

        this.amount = new ValueBigDecimal(10, 6);
        this.amount.setValue(amount);

    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(amount).toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) return error;

        if (debtAmount.compareTo(amount.getQuantity()) < 0) {
            return ErrorResult.make(DS.getString(R.string.exceeded_the_amount_payment));
        }
        return ErrorResult.NONE;
    }

    public CharSequence tvTitle() {
        ShortHtml html = UI.html().v(paymentType.name);
        if (consignment) {
            html.v(DS.getString(R.string.debtor_info_consignment));
        }
        return html.br()
                .v(DS.getString(R.string.sum)).v(": ")
                .v(NumberUtil.formatMoney(debtAmount)).html();
    }
}
