package uz.greenwhite.smartup5_trade.m_deal.variable.rpayment;// 07.10.2016

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealRPayment;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealRPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturnForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturnModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealRPaymentModule extends VDealModule {

    public final VDealRPaymentForm form;
    public VDealReturnModule orderModule;

    public VDealRPaymentModule(VisitModule module, VDealRPaymentForm form) {
        super(module);
        this.form = form;
        this.form.paymentModule = this;
    }

    public BigDecimal totalWarehouseSum() {
        for (VDealReturnForm f : orderModule.forms.getItems()) {
            if (f.hasValue()) {
                return f.getTotalSum();
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public DealModule convertToDealModule() {
        ArrayList<DealRPayment> result = new ArrayList<>();
        for (VDealRPayment r : form.payments.getItems()) {
            if (r.hasValue()) {
                String paymentTypeId = r.paymentType.id;
                BigDecimal amount = r.amount.getQuantity();
                result.add(new DealRPayment(paymentTypeId, amount));
            }
        }
        return new DealRPaymentModule(MyArray.from(result));
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
}
