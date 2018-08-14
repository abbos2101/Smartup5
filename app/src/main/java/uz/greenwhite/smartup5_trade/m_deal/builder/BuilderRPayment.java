package uz.greenwhite.smartup5_trade.m_deal.builder;// 06.10.2016

import java.math.BigDecimal;
import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealRPayment;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealRPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.rpayment.VDealRPayment;
import uz.greenwhite.smartup5_trade.m_deal.variable.rpayment.VDealRPaymentForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.rpayment.VDealRPaymentModule;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderRPayment {

    private final DealRef dealRef;
    private final VisitModule module;
    public final MyArray<DealRPayment> initial;

    public BuilderRPayment(DealRef dealRef) {
        this.dealRef = dealRef;
        this.module = new VisitModule(VisitModule.M_RETURN_PAYMENT, false);
        this.initial = getInitialOrders();
    }

    private MyArray<DealRPayment> getInitialOrders() {
        DealRPaymentModule module = dealRef.findDealModule(this.module.id);
        return module != null ? module.returnPayments : MyArray.<DealRPayment>emptyArray();
    }

    public final MyArray<PaymentType> getPaymentTypes() {
        MyArray<PaymentType> result = dealRef.room.paymentTypeIds
                .union(initial.map(new MyMapper<DealRPayment, String>() {
                    @Override
                    public String apply(DealRPayment dealRPayment) {
                        return dealRPayment.paymentTypeId;
                    }
                })).map(new MyMapper<String, PaymentType>() {
                    @Override
                    public PaymentType apply(String paymentTypeId) {
                        return dealRef.getPaymentType(paymentTypeId);
                    }
                });

        result.checkNotNull();
        return result.sort(new Comparator<PaymentType>() {
            @Override
            public int compare(PaymentType l, PaymentType r) {
                return l.name.compareTo(r.name);
            }
        });
    }

    private VDealRPaymentForm makeForm() {
        MyArray<PaymentType> paymentTypes = getPaymentTypes();

        MyArray<VDealRPayment> result = paymentTypes.map(new MyMapper<PaymentType, VDealRPayment>() {
            @Override
            public VDealRPayment apply(PaymentType paymentType) {
                BigDecimal amount = null;
                DealRPayment find = initial.find(paymentType.id, DealRPayment.KEY_ADAPTER);
                if (find != null) {
                    amount = find.amount;
                }
                return new VDealRPayment(paymentType, amount);
            }
        });
        return new VDealRPaymentForm(module, new ValueArray<>(result));
    }

    public VDealRPaymentModule build() {
        return new VDealRPaymentModule(module, makeForm());
    }
}
