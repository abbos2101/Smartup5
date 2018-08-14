package uz.greenwhite.smartup5_trade.m_shipped.variable.pko;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPayment;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentCurrency;

public class VSPKOForm extends VSDealForm {

    public final ValueArray<VSDealPaymentCurrency> items;

    public VSPKOForm(VisitModule module, ValueArray<VSDealPaymentCurrency> items) {
        super(module);
        this.items = items;
    }

    @Override
    public boolean hasValue() {
        for (VSDealPaymentCurrency c : items.getItems()) {
            for (VSDealPayment p : c.payments.getItems()) {
                if (p.pkoAmount.nonZero()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return items.getItems().toSuper();
    }

    @Override
    public ErrorResult getError() {
        for (VSDealPaymentCurrency c : items.getItems()) {
            ErrorResult error = c.getErrorPKO();
            if (error.isError()) return error;
        }
        return ErrorResult.NONE;
    }
}
