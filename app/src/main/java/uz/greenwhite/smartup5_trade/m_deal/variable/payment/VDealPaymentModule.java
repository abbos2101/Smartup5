package uz.greenwhite.smartup5_trade.m_deal.variable.payment;// 30.06.2016

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPayment;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealPaymentModule extends VDealModule {

    public final VDealPaymentForm form;

    public VDealPaymentModule(VisitModule module, VDealPaymentForm accountForm) {
        super(module);
        this.form = accountForm;
    }

    public boolean hasConsignment() {
        MyArray<VDealPaymentCurrency> items = form.payment.getItems();
        for (VDealPaymentCurrency c : items) {
            for (VDealPayment p : c.getMultyPayment()) {
                if (p.consignmentAmount.nonZero()) {
                    return true;
                }
            }
        }
        return false;
    }

    public BigDecimal getTotalPaymentSum(){
        return form.getTotalPayment();
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        return form.hasValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).toSuper();
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealPayment> as = new ArrayList<>();
        for (VDealPaymentCurrency c : form.payment.getItems()) {
            for (VDealPayment p : c.getMultyPayment()) {
                if (p.hasValue()) {
                    String consignAmount = p.consignmentAmount.getText();
                    if ("0".equals(consignAmount)) {
                        consignAmount = "";
                    }
                    as.add(new DealPayment(c.currency.currencyId, p.paymentType.id,
                            p.amount.getValue(), consignAmount,
                            p.consignmentDate.getValue()));
                }
            }
        }
        return new DealPaymentModule(MyArray.from(as));
    }
}
