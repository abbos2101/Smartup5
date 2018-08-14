package uz.greenwhite.smartup5_trade.m_shipped.variable.payment;// 09.09.2016

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SPayment;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderModule;

public class VSDealPaymentModule extends VSDealModule {

    public final VSDealPaymentForm form;
    public VSDealOrderModule orderModule;

    public VSDealPaymentModule(VisitModule module, VSDealPaymentForm form) {
        super(module);
        this.form = form;
        attachPaymentModule();
    }

    public void attachPaymentModule() {
        form.paymentModule = this;
    }

    public Map<String, BigDecimal> totalWarehouseSum() {
        Map<String, BigDecimal> m = new HashMap<>();
        for (VSDealOrderForm f : orderModule.forms.getItems()) {
            BigDecimal totalSum = Util.nvl(m.get(f.currency.currencyId), BigDecimal.ZERO);
            m.put(f.currency.currencyId, totalSum.add(f.getTotalSum()));
        }
        return m;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).toSuper();
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VSDealPaymentCurrency c : form.payment.getItems()) {
            if (c.hasValue()) {
                return true;
            }
        }
        return false;
    }

    public MyArray<SPayment> convertSPayment() {
        MyArray<VSDealPaymentCurrency> paymentCurrencies = form.payment.getItems();

        if (paymentCurrencies.nonEmpty()) {
            VSDealPaymentCurrency vsDealPaymentCurrency = paymentCurrencies.get(0);
            MyArray<VSDealPayment> items = vsDealPaymentCurrency.payments.getItems();

            if (items.nonEmpty()) {
                VSDealPayment vsDealPayment = items.get(0);
                SPayment payment = vsDealPayment.sPayment;

                String consignAmount = vsDealPayment.consignmentAmount.getText();
                if ("0" .equals(consignAmount)) {
                    consignAmount = "";
                }

                return MyArray.from(new SPayment(payment.paymentTypeId, payment.currencyId,
                        vsDealPayment.getPaymentTotalAmount(), consignAmount,
                        vsDealPayment.consignmentDate.getValue(),
                        vsDealPayment.pkoAmount.getQuantity()));
            }
        }
        return MyArray.emptyArray();
    }
}
