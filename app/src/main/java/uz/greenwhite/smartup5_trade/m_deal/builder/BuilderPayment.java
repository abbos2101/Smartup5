package uz.greenwhite.smartup5_trade.m_deal.builder;// 30.06.2016

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPayment;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPayment;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentCurrency;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderPayment {

    public final DealRef dealRef;
    public final boolean hasConsignmentModule;
    public final VisitModule module;
    public final MyArray<DealPayment> initialPayment;

    public BuilderPayment(DealRef dealRef, boolean hasConsignmentModule) {
        this.dealRef = dealRef;
        this.hasConsignmentModule = hasConsignmentModule;
        this.module = new VisitModule(VisitModule.M_PAYMENT, false);
        this.initialPayment = getInitialPayment();
    }

    private MyArray<DealPayment> getInitialPayment() {
        DealPaymentModule paymentModule = dealRef.findDealModule(module.id);
        return paymentModule != null ? paymentModule.payments : MyArray.<DealPayment>emptyArray();
    }

    private MyArray<PaymentType> getPaymentTypes() {
        MyArray<PaymentType> result = dealRef.room.paymentTypeIds
                .union(initialPayment.map(new MyMapper<DealPayment, String>() {
                    @Override
                    public String apply(DealPayment dealOrderPayment) {
                        return dealOrderPayment.paymentTypeId;
                    }
                }))
                .map(new MyMapper<String, PaymentType>() {
                    @Override
                    public PaymentType apply(String paymentTypeId) {
                        return dealRef.getPaymentType(paymentTypeId);
                    }
                }).filterNotNull();

        return result.sort(new Comparator<PaymentType>() {
            @Override
            public int compare(PaymentType l, PaymentType r) {
                return l.name.compareTo(r.name);
            }
        });
    }

    private MyArray<String> getCurrencyIds() {
        MyArray<String> priceTypeIds = dealRef.getPriceTypeIds();

        Set<String> cIds = new HashSet<>();
        for (String id : priceTypeIds) {
            PriceType priceType = dealRef.getPriceType(id);
            if (priceType != null) {
                cIds.add(priceType.currencyId);
            }
        }
        return MyArray.from(cIds);
    }

    private MyArray<VDealPayment> makePayment(MyArray<PaymentType> paymentTypes, final Currency currency) {
        return paymentTypes
                .filter(new MyPredicate<PaymentType>() {
                    @Override
                    public boolean apply(PaymentType paymentType) {
                        return currency.currencyId.equals(paymentType.currencyId);
                    }
                })
                .map(new MyMapper<PaymentType, VDealPayment>() {
                    @Override
                    public VDealPayment apply(PaymentType paymentType) {
                        Tuple2 key = DealPayment.getKey(currency.currencyId, paymentType.id);
                        DealPayment dealPayment = initialPayment.find(key, DealPayment.KEY_ADAPTER);
                        BigDecimal amount = null;
                        BigDecimal consignmentAmount = null;
                        String consignmentDate = null;
                        if (dealPayment != null) {
                            amount = dealPayment.value;
                            consignmentDate = dealPayment.consignmentDate;
                            if (!TextUtils.isEmpty(dealPayment.consignmentAmount)) {
                                consignmentAmount = new BigDecimal(dealPayment.consignmentAmount);
                            }
                        }
                        return new VDealPayment(paymentType, amount, consignmentAmount, consignmentDate);
                    }
                }).filterNotNull();
    }

    private VDealPaymentForm makePaymentForm() {
        final MyArray<PaymentType> paymentTypes = getPaymentTypes();
        MyArray<VDealPaymentCurrency> orderPayment = MyArray.emptyArray();
        if (paymentTypes.nonEmpty()) {
            orderPayment = getCurrencyIds()
                    .map(new MyMapper<String, VDealPaymentCurrency>() {

                        @Override
                        public VDealPaymentCurrency apply(String currencyId) {
                            Currency currency = Util.nvl(dealRef.getCurrency(currencyId), Currency.DEFAULT);
                            MyArray<VDealPayment> vDealPayments = makePayment(paymentTypes, currency);
                            return new VDealPaymentCurrency(dealRef, currency, new ValueArray<>(vDealPayments));
                        }
                    });
        }
        return new VDealPaymentForm(module, hasConsignmentModule, new ValueArray<>(orderPayment));
    }

    public VDealPaymentModule build() {
        return new VDealPaymentModule(module, makePaymentForm());
    }
}
