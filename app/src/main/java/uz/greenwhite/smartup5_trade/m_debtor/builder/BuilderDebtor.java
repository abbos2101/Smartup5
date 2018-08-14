package uz.greenwhite.smartup5_trade.m_debtor.builder;


import android.text.TextUtils;

import java.util.Comparator;
import java.util.Date;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorHolder;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorInfo;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorPayment;
import uz.greenwhite.smartup5_trade.m_debtor.variable.debtor.VDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.variable.debtor.VDebtorCurrency;
import uz.greenwhite.smartup5_trade.m_debtor.variable.debtor.VDebtorPayment;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletContract;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.PaymentType;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.DebtorDeal;

public class BuilderDebtor {

    public static String stringify(VDebtor debtor) {
        Debtor convert = debtor.convert();
        return Uzum.toJson(convert, Debtor.UZUM_ADAPTER);
    }

    @SuppressWarnings("ConstantConditions")
    public static VDebtor make(final Scope scope, final String outletId, final String dealId, final String debDate, boolean consign) {
        DebtorHolder foundEntryDebtor = scope.entry.getDebtorHolder()
                .findFirst(new MyPredicate<DebtorHolder>() {
                    @Override
                    public boolean apply(DebtorHolder h) {
                        return h.debtor.outletId.equals(outletId) && h.debtor.dealId.equals(dealId);
                    }
                });

        String localId = foundEntryDebtor != null ? foundEntryDebtor.entryId : String.valueOf(AdminApi.nextSequence());
        EntryState entryState = foundEntryDebtor != null ? foundEntryDebtor.entryState : EntryState.NOT_SAVED_ENTRY;

        MyArray<DebtorDeal> foundTapeDebtor = scope.ref.getDebtorOutlet(outletId).deals
                .filter(new MyPredicate<DebtorDeal>() {
                    @Override
                    public boolean apply(DebtorDeal debtorDeal) {
                        return debtorDeal.dealId.equals(dealId);
                    }
                }).sort(new Comparator<DebtorDeal>() {
                    @Override
                    public int compare(DebtorDeal l, DebtorDeal r) {
                        return MyPredicate.compare(
                                Integer.parseInt(DateUtil.convert(l.expiryDate, DateUtil.FORMAT_AS_NUMBER)),
                                Integer.parseInt(DateUtil.convert(r.expiryDate, DateUtil.FORMAT_AS_NUMBER)));
                    }
                });

        DebtorPayment debtorPayment = null;
        if (debtorPayment == null && foundEntryDebtor != null) {
            debtorPayment = foundEntryDebtor.debtor.payments.get(0);
        }

        DebtorDeal currentDebt = foundTapeDebtor.get(0);
        DebtorDeal consignDebt = foundTapeDebtor.size() > 1 ? foundTapeDebtor.get(1) : null;

        Currency currency = scope.ref.getCurrency(currentDebt.currencyId);
        PaymentType paymentType = scope.ref.getPaymentTypes(currentDebt.paymentTypeId);

        //--------------------------- MAKE VARIABLE ----------------------------

        boolean attachConsignment = false;
        if (!consign && consignDebt != null) {
            int consignDate = Integer.parseInt(DateUtil.convert(consignDebt.expiryDate, DateUtil.FORMAT_AS_NUMBER));
            int today = Integer.parseInt(DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER));
            attachConsignment = consignDate <= today;
        }

        VDebtorPayment vDebtPayment = new VDebtorPayment(paymentType, false,
                attachConsignment ? currentDebt.amount.add(consignDebt.amount) : currentDebt.amount,
                debtorPayment != null ? debtorPayment.amount : null);

        VDebtorPayment vConsPayment = consign && consignDebt != null ?
                new VDebtorPayment(paymentType, true, consignDebt.amount,
                        debtorPayment != null ? debtorPayment.consignAmount : null) : null;

        VDebtorCurrency vDebtorCurrency = new VDebtorCurrency(currency, vDebtPayment, vConsPayment);

        OutletContract contract = makeContractNumbers(scope, outletId, currentDebt);
        Room room = scope.ref.getRoom(currentDebt.roomId);

        DebtorInfo info = new DebtorInfo(localId, scope.filialId, outletId,
                room != null ? room.name : "", dealId, debDate, currentDebt.userName, currentDebt.expeditorName,
                consign && consignDebt != null ? consignDebt.expiryDate :
                        (currentDebt.dealDeliveryDate.equals(currentDebt.expiryDate)) ? "" : currentDebt.expiryDate,
                currentDebt.dealDeliveryDate);
        return new VDebtor(info, entryState, vDebtorCurrency, contract);
    }

    @SuppressWarnings("ConstantConditions")
    private static OutletContract makeContractNumbers(Scope scope, final String outletId, DebtorDeal deal) {
        MyArray<OutletContract> contracts = scope.ref.getOutletContracts(outletId);
        if (contracts.nonEmpty() && !TextUtils.isEmpty(deal.contractId)) {
            Tuple2 key = OutletContract.getKey(outletId, deal.contractId);
            return contracts.find(key, OutletContract.KEY_ADAPTER);
        }
        return null;
    }
}
