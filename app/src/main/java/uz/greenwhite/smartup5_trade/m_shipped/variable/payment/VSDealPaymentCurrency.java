package uz.greenwhite.smartup5_trade.m_shipped.variable.payment;// 09.09.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SService;

public class VSDealPaymentCurrency extends VariableLike {

    public final Currency currency;
    public final SService service;
    public final ValueArray<VSDealPayment> payments;

    public VSDealPaymentCurrency(Currency currency, SService service, ValueArray<VSDealPayment> payments) {
        AppError.checkNull(currency, payments);
        this.currency = currency;
        this.service = service;
        this.payments = payments;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(this.payments);
    }

    public ErrorResult getErrorPKO() {
        for (VSDealPayment v : payments.getItems()) {
            ErrorResult error = v.getErrorPKO();
            if (error.isError()) return error;
        }
        return ErrorResult.NONE;
    }

    public boolean hasValue() {
        for (VSDealPayment p : payments.getItems()) {
            if (p.hasValue()) return true;
        }
        return false;
    }

    public boolean hasConsign() {
        for (VSDealPayment p : payments.getItems()) {
            if (p.hasConsign()) return true;
        }
        return false;
    }

    public boolean hasPKO() {
        for (VSDealPayment p : payments.getItems()) {
            if (p.hasPKO()) return true;
        }
        return false;
    }

    public static final MyMapper<VSDealPaymentCurrency, String> KEY_ADAPTER = new MyMapper<VSDealPaymentCurrency, String>() {
        @Override
        public String apply(VSDealPaymentCurrency val) {
            return val.currency.currencyId;
        }
    };
}
