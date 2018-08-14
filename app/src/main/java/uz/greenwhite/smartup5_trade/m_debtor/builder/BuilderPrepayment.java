package uz.greenwhite.smartup5_trade.m_debtor.builder;


import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorHolder;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorPayment;
import uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment.VPrepayment;
import uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment.VPrepaymentCurrency;
import uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment.VPrepaymentPayment;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.PrepaymentPaymentTypes;

public class BuilderPrepayment {

    public static String stringify(VPrepayment debtor) {
        Debtor convert = debtor.convert();
        return Uzum.toJson(convert, Debtor.UZUM_ADAPTER);
    }

    @SuppressWarnings("ConstantConditions")
    public static VPrepayment make(Scope scope,
                                   String filialId,
                                   String outletId,
                                   String entryId,
                                   String paymentKind,
                                   Debtor debtor) {
        MyArray<PrepaymentPaymentTypes> payments = scope.ref.filterByPaymentKind(paymentKind);

        MyArray<DebtorHolder> entries = scope.entry.getDebtorHolder();
        DebtorHolder holder = null;
        if (!TextUtils.isEmpty(entryId)) {
            holder = entries.find(entryId, DebtorHolder.KEY_ADAPTER);
        }

        Set<String> currencyIds = new HashSet<>();
        Set<String> paymentIds = new HashSet<>();

        for (PrepaymentPaymentTypes val : payments) {
            paymentIds.add(val.paymentId);
            currencyIds.add(val.currencyId);
        }

        if (debtor != null) {
            for (DebtorPayment val : debtor.payments) {
                paymentIds.add(val.paymentTypeId);
                currencyIds.add(val.currencyId);
            }
        }

        if (holder != null) {
            for (DebtorPayment p : holder.debtor.payments) {
                currencyIds.add(p.currencyId);
                paymentIds.add(p.paymentTypeId);
            }
        }

        entryId = !TextUtils.isEmpty(entryId) ? entryId : String.valueOf(AdminApi.nextSequence());
        EntryState entryState = holder != null ? holder.entryState : EntryState.NOT_SAVED_ENTRY;

        return new VPrepayment(entryId, filialId, outletId, paymentKind, entryState,
                makeCurrency(scope, holder, debtor, currencyIds, paymentIds));
    }

    @SuppressWarnings("ConstantConditions")
    private static ValueArray<VPrepaymentCurrency> makeCurrency(Scope scope,
                                                                DebtorHolder holder,
                                                                Debtor debtor,
                                                                Set<String> currencyIds,
                                                                Set<String> paymentIds) {
        ArrayList<VPrepaymentCurrency> r = new ArrayList<>();
        for (String currencyId : currencyIds) {
            Currency currency = scope.ref.getCurrency(currencyId);
            if (currency == null) continue;
            ValueArray<VPrepaymentPayment> payments = makePayment(scope, holder, debtor, currency, paymentIds);
            if (payments.getItems().isEmpty()) continue;
            r.add(new VPrepaymentCurrency(currency, payments));
        }

        return new ValueArray<>(MyArray.from(r));
    }

    @SuppressWarnings("ConstantConditions")
    private static ValueArray<VPrepaymentPayment> makePayment(Scope scope,
                                                              DebtorHolder holder,
                                                              Debtor debtor,
                                                              Currency currency,
                                                              Set<String> paymentIds) {
        ArrayList<VPrepaymentPayment> r = new ArrayList<>();
        for (final String paymentId : paymentIds) {
            PaymentType paymentType = scope.ref.getPaymentTypes(paymentId);
            if (paymentType == null || !paymentType.currencyId.equals(currency.currencyId))
                continue;

            BigDecimal amount = null;
            if (debtor != null) {
                Tuple2 key = DebtorPayment.getKey(currency.currencyId, paymentId);
                DebtorPayment found = debtor.payments.find(key, DebtorPayment.KEY_ADAPTER);
                if (found != null) {
                    amount = found.amount;
                }

            } else if (holder != null) {
                Tuple2 key = DebtorPayment.getKey(currency.currencyId, paymentId);
                DebtorPayment found = holder.debtor.payments.find(key, DebtorPayment.KEY_ADAPTER);
                if (found != null) {
                    amount = found.amount;
                }
            }

            r.add(new VPrepaymentPayment(paymentType, amount));
        }
        return new ValueArray<>(MyArray.from(r));
    }

}
