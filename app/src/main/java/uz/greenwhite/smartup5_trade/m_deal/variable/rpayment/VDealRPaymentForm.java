package uz.greenwhite.smartup5_trade.m_deal.variable.rpayment;// 07.10.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealRPaymentForm extends VDealForm {

    public final ValueArray<VDealRPayment> payments;
    public VDealRPaymentModule paymentModule;
    private BigDecimal orderTotalSumCache;

    public VDealRPaymentForm(VisitModule module, ValueArray<VDealRPayment> payments) {
        super(module);
        this.payments = payments;
    }

    public void cacheOrderTotalSum() {
        this.orderTotalSumCache = paymentModule.totalWarehouseSum();
    }

    public BigDecimal getRemainTotalSum() {
        return getReturnTotalSum().subtract(getPaymentTotalSum());
    }

    public BigDecimal getReturnTotalSum() {
        return orderTotalSumCache;
    }

    public BigDecimal getPaymentTotalSum() {
        return payments.getItems()
                .filter(new MyPredicate<VDealRPayment>() {
                    @Override
                    public boolean apply(VDealRPayment val) {
                        return val.amount.nonZero();
                    }
                })
                .reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealRPayment>() {
                    @Override
                    public BigDecimal apply(BigDecimal result, VDealRPayment val) {
                        return result.add(val.amount.getQuantity());
                    }
                });
    }

    @Override
    public boolean hasValue() {
        for (VDealRPayment r : payments.getItems()) {
            if (r.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return payments.getItems().toSuper();
    }
}
