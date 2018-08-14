package uz.greenwhite.smartup5_trade.m_shipped.variable.payment;// 09.09.2016

import java.math.BigDecimal;
import java.util.Map;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SOverload;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class VSDealPaymentForm extends VSDealForm {

    private static final String PAYMENT_CODE = ":payment";

    public final boolean hasConsignmentModule;
    public final boolean hasPKOModule;

    public final ValueArray<VSDealPaymentCurrency> payment;
    public VSDealPaymentModule paymentModule;
    private Map<String, BigDecimal> orderTotalSumCache;

    private final MyArray<SOverload> overloads;


    public VSDealPaymentForm(VisitModule module,
                             boolean hasConsignmentModule,
                             boolean hasPKOModule,
                             ValueArray<VSDealPaymentCurrency> payment,
                             MyArray<SOverload> overloads) {
        super(module, "" + module.id + PAYMENT_CODE);
        this.payment = payment;
        this.hasConsignmentModule = hasConsignmentModule;
        this.hasPKOModule = hasPKOModule;
        this.overloads = overloads;
    }

    @Override
    public CharSequence getDetail() {
        ErrorResult error = getError();
        if (error.isError()) {
            return UI.toRedText(error.getErrorMessage());
        }
        return super.getDetail();
    }

    public void cacheOrderTotalSum() {
        this.orderTotalSumCache = paymentModule.totalWarehouseSum();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public BigDecimal getOrderTotalSumCache(String currencyId) {
        if (orderTotalSumCache == null) {
            cacheOrderTotalSum();
        }
        BigDecimal overloadTotalAmount = overloads.reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, SOverload>() {
            @Override
            public BigDecimal apply(BigDecimal quantity, SOverload sOverload) {
                return quantity.add(sOverload.soldTotalAmount);
            }
        });
        return Util.nvl(orderTotalSumCache.get(currencyId), BigDecimal.ZERO).add(overloadTotalAmount);
    }

    @Override
    public boolean hasValue() {
        for (VSDealPaymentCurrency c : payment.getItems()) {
            if (c.hasValue()) return true;
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return payment.getItems().toSuper();
    }
}
