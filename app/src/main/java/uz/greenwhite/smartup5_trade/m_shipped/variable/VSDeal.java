package uz.greenwhite.smartup5_trade.m_shipped.variable;// 09.09.2016

import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.common.module.VModule;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SAttach;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SOrder;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SPayment;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SNote;
import uz.greenwhite.smartup5_trade.m_shipped.variable.attach.VSAttachModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.error.VSDealErrorForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.error.VSDealErrorModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.info.VSDealInfoForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.info.VSDealInfoModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.note.VSDealNoteForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.note.VSDealNoteModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentCurrency;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.payment.VSDealPaymentModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.reasons.VSReturnReasonModule;

public class VSDeal extends VariableLike {

    public final SDealRef sDealRef;
    public final Outlet outlet;
    public final SDealHolder sDealHolder;
    public final ValueArray<VSDealModule> modules;

    public VSDeal(SDealRef sDealRef, ValueArray<VSDealModule> modules) {
        this.sDealRef = sDealRef;
        this.outlet = sDealRef.outlet;
        this.sDealHolder = sDealRef.holder;
        this.modules = gatherModule(modules);
    }


    private ValueArray<VSDealModule> gatherModule(ValueArray<VSDealModule> modules) {

        modules.prepend(new VSDealInfoModule(new VSDealInfoForm(sDealRef)));

        if (!TextUtils.isEmpty(sDealRef.holder.entryState.serverResult)) {
            modules.prepend(new VSDealErrorModule(new VSDealErrorForm(sDealRef)));
        }

        SNote note = sDealRef.sDeal.note;

        if (note != null && !TextUtils.isEmpty(note.note)) {
            modules.append(new VSDealNoteModule(new VSDealNoteForm(note)));
        }

        MyArray<VModule> superModules = modules.getItems().toSuper();

        VSDealOrderModule orderModule = (VSDealOrderModule) superModules
                .find(VisitModule.M_ORDER, VModule.KEY_ADAPTER);

        VSDealPaymentModule paymentModule = (VSDealPaymentModule) superModules
                .find(VisitModule.M_PAYMENT, VSDealModule.KEY_ADAPTER);

        paymentModule.orderModule = orderModule;

        ValueArray<VSDealModule> result = new ValueArray<>();
        for (VSDealModule module : modules.getItems()) {
            if (module.getModuleForms().nonEmpty()) result.append(module);
        }

        return result;
    }

    public String getFirstModuleFormCode() {
        for (VSDealModule module : modules.getItems()) {
            MyArray<VForm> forms = module.getModuleForms();
            if (forms.nonEmpty()) {
                return forms.get(0).code;
            }
        }
        return null;
    }

    public void prepareOrderForm() {
        MyArray<VModule> items = modules.getItems().toSuper();
        VSDealOrderModule m = (VSDealOrderModule) items.find(VisitModule.M_ORDER, VSDealModule.KEY_ADAPTER);
        if (m != null) {
            for (VSDealOrderForm form : m.forms.getItems()) form.makeOrderQuant();
        }

        VSDealPaymentModule paymentModule = (VSDealPaymentModule) items.find(VisitModule.M_PAYMENT, VSDealModule.KEY_ADAPTER);
        if (paymentModule != null) {
            VSDealPaymentForm form = paymentModule.form;
            form.cacheOrderTotalSum();
            MyArray<VSDealPaymentCurrency> paymentCurrencies = form.payment.getItems();
            if (paymentCurrencies.isEmpty()) return;
            VSDealPaymentCurrency vsDealPaymentCurrency = paymentCurrencies.get(0);
            BigDecimal total = form.getOrderTotalSumCache(vsDealPaymentCurrency.currency.currencyId);
            vsDealPaymentCurrency.payments.getItems().get(0).setPaymentAmount(total);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends VSDealForm> T findForm(String formCode) {
        for (VSDealModule m : modules.getItems()) {
            for (VForm f : m.getModuleForms()) {
                if (f.code.equals(formCode)) {
                    return (T) f;
                }
            }
        }
        return null;
    }

    public SDeal convertToSDeal() {
        MyArray<VModule> superModules = modules.getItems().toSuper();

        VSDealOrderModule order = (VSDealOrderModule) superModules
                .find(VisitModule.M_ORDER, VSDealModule.KEY_ADAPTER);

        VSDealPaymentModule payment = (VSDealPaymentModule) superModules
                .find(VisitModule.M_PAYMENT, VSDealModule.KEY_ADAPTER);

        VSAttachModule attach = (VSAttachModule) superModules
                .find(VisitModule.M_ATTACH, VSDealModule.KEY_ADAPTER);

        VSReturnReasonModule reason = (VSReturnReasonModule) superModules
                .find(VisitModule.M_REASON, VSDealModule.KEY_ADAPTER);

        MyArray<SOrder> sOrders = order == null ? MyArray.<SOrder>emptyArray() : order.convertSOrder();
        MyArray<SPayment> sPayments = payment == null ? MyArray.<SPayment>emptyArray() : payment.convertSPayment();
        String reasonId = reason == null ? "" : reason.checkedReason();
        SAttach sAttach = attach.convertSAttach();

        SDeal d = sDealHolder.deal;
        return new SDeal(d.dealId, d.outletId, d.filialId, d.deliveryDate,
                d.roundModel.model, sOrders, sPayments, sAttach, reasonId, d.service, d.note, d.overload);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return modules.getItems().toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }

        MyArray<VModule> superModules = modules.getItems().toSuper();

        VSDealOrderModule order = (VSDealOrderModule) superModules
                .find(VisitModule.M_ORDER, VSDealModule.KEY_ADAPTER);

        VSReturnReasonModule reason = (VSReturnReasonModule) superModules
                .find(VisitModule.M_REASON, VSDealModule.KEY_ADAPTER);

        if (reason != null &&
                reason.form != null &&
                reason.form.reasones.getItems().nonEmpty()) {

            boolean hasReturn = order.hasReturn();
            boolean hasReason = reason.hasValue();

            if (hasReturn && !hasReason) {
                return ErrorResult.make(DS.getString(R.string.sdeal_select_return_reason));
            }

            if (!hasReturn && hasReason) {
                reason.unCheckAll();
            }
        }

        return ErrorResult.NONE;
    }
}
