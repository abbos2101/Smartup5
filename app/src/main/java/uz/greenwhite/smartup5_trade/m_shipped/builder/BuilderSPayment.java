package uz.greenwhite.smartup5_trade.m_shipped.builder;// 09.09.2016

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SPayment;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SOverload;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SService;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPayment;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentCurrency;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentModule;

public class BuilderSPayment {

    public final SDealRef sDealRef;
    public final boolean hasConsignmentModule;
    public final boolean hasPKOModule;
    public final VisitModule module;
    public final MyArray<SPayment> initialPayment;

    public BuilderSPayment(SDealRef sDealRef) {
        MyArray<VisitModule> visitModules = sDealRef.filial.getVisitModules(sDealRef.outlet.personKind);

        this.sDealRef = sDealRef;

        this.hasConsignmentModule = visitModules.contains(VisitModule.M_CONSIGNMENT, VisitModule.KEY_ADAPTER);
        this.hasPKOModule = visitModules.contains(VisitModule.M_PKO, VisitModule.KEY_ADAPTER);

        this.module = new VisitModule(VisitModule.M_PAYMENT, false);
        this.initialPayment = getInitialPayments();
    }

    private MyArray<SPayment> getInitialPayments() {
        MyArray<SPayment> orders = sDealRef.holder.deal.payments;
        return orders != null ? orders : MyArray.<SPayment>emptyArray();
    }

    private MyArray<PaymentType> getPaymentTypes() {
        Set<String> paymentIds = sDealRef.sDeal.payments.map(new MyMapper<SPayment, String>() {
            @Override
            public String apply(SPayment sPayment) {
                return sPayment.paymentTypeId;
            }
        }).asSet();
        ArrayList<PaymentType> r = new ArrayList<>();
        for (String paymentTypeId : paymentIds) {
            PaymentType paymentType = sDealRef.getPaymentType(paymentTypeId);
            if (paymentType != null) r.add(paymentType);
        }
        return MyArray.from(r);
    }

    private MyArray<Currency> getCurrencies() {
        Set<String> currencyIds = sDealRef.sDeal.payments.map(new MyMapper<SPayment, String>() {
            @Override
            public String apply(SPayment sPayment) {
                return sPayment.currencyId;
            }
        }).asSet();

        ArrayList<Currency> r = new ArrayList<>();
        for (String currencyId : currencyIds) {
            Currency currency = sDealRef.getCurrency(currencyId);
            if (currency != null) r.add(currency);
        }
        return MyArray.from(r);
    }

    private ValueArray<VSDealPayment> makePayment(final SService sService, MyArray<PaymentType> paymentTypes, final Currency currency) {
        MyArray<VSDealPayment> result = paymentTypes
                .map(new MyMapper<PaymentType, VSDealPayment>() {
                    @Override
                    public VSDealPayment apply(PaymentType paymentType) {
                        Tuple2 key = SPayment.getKey(currency.currencyId, paymentType.id);
                        SPayment val = initialPayment.find(key, SPayment.KEY_ADAPTER);
                        BigDecimal amount = null;
                        BigDecimal consignAmount = null;
                        String consignDate = "";
                        BigDecimal pkoAmount = null;
                        if (val != null) {
                            amount = val.amount;
                            consignAmount = val.getConsignment();
                            consignDate = Util.nvl(val.consignmentDate);
                            pkoAmount = val.pkoAmount;
                        } else {
                            val = sDealRef.sDeal.payments.find(key, SPayment.KEY_ADAPTER);
                        }
                        if (val == null) return null;
                        return new VSDealPayment(val, paymentType, sService, amount, consignAmount, consignDate, pkoAmount);
                    }
                }).filterNotNull();

        return new ValueArray<>(result);
    }

    private VSDealPaymentForm makeForm() {
        final MyArray<PaymentType> paymentTypes = getPaymentTypes();

        MyArray<VSDealPaymentCurrency> result = getCurrencies().map(new MyMapper<Currency, VSDealPaymentCurrency>() {
            @Override
            public VSDealPaymentCurrency apply(Currency currency) {
                SService sService = sDealRef.sDeal.service.find(currency.currencyId, SService.KEY_ADAPTER);
                return new VSDealPaymentCurrency(currency, sService, makePayment(sService, paymentTypes, currency));
            }
        }).filterNotNull();
        return new VSDealPaymentForm(module, hasConsignmentModule, hasPKOModule, new ValueArray<>(result),
                sDealRef.sDeal.overload);
    }

    public VSDealPaymentModule build() {
        return new VSDealPaymentModule(module, makeForm());
    }

}