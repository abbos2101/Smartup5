package uz.greenwhite.smartup5_trade.m_deal.variable.payment;// 30.06.2016


import java.math.BigDecimal;
import java.util.Map;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealPaymentForm extends VDealForm {

    private static final String PAYMENT_CODE = ":payment";

    public final boolean hasConsignmentModule;
    public final ValueArray<VDealPaymentCurrency> payment;

    public VDealPaymentForm(VisitModule module, boolean hasConsignmentModule, ValueArray<VDealPaymentCurrency> payment) {
        super(module, "" + module.id + PAYMENT_CODE);
        this.hasConsignmentModule = hasConsignmentModule;
        this.payment = payment;
    }

    @Override
    public CharSequence getTitle() {
        return DS.getString(R.string.payment);
    }

    @Override
    public CharSequence getDetail() {
        ErrorResult error = getError();
        if (error.isError()) {
            return UI.toRedText(error.getErrorMessage());
        }
        return super.getDetail();
    }

    public void setCurrencyOrderSums(Map<String, BigDecimal> currencySums) {
        for (VDealPaymentCurrency r : payment.getItems()) {
            BigDecimal sum = currencySums.get(r.currency.currencyId);
            if (sum != null) r.setOrderCurrencySum(sum);
        }
    }

    public BigDecimal getTotalPayment() {
        return payment.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealPaymentCurrency>() {
            @Override
            public BigDecimal apply(BigDecimal result, VDealPaymentCurrency val) {
                return result.add(val.getQuantity());
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return payment.getItems().toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VDealPaymentCurrency account : payment.getItems()) {
            if (account.hasValue()) {
                return true;
            }
        }
        return false;
    }
}