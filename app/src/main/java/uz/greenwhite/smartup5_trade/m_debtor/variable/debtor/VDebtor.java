package uz.greenwhite.smartup5_trade.m_debtor.variable.debtor;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorInfo;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorPayment;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletContract;

public class VDebtor extends VariableLike {


    public final DebtorInfo info;
    public final EntryState entryState;
    public final VDebtorCurrency currency;
    @Nullable
    public final OutletContract contract;

    public VDebtor(DebtorInfo info,
                   EntryState entryState,
                   VDebtorCurrency currency,
                   @Nullable OutletContract contract) {
        this.info = info;
        this.entryState = entryState;
        this.currency = currency;
        this.contract = contract;
    }

    public boolean hasValue() {
        return currency.hasValue();
    }

    public Debtor convert() {
        ArrayList<DebtorPayment> r = new ArrayList<>();
        if (currency.hasValue()) {
            VDebtorPayment debtPayment = currency.debtPayment;
            VDebtorPayment consignPayment = currency.consignPayment;
            r.add(new DebtorPayment(
                    currency.currency.currencyId,
                    debtPayment.paymentType.id,
                    debtPayment.amount.getQuantity(),
                    consignPayment != null ? consignPayment.amount.getQuantity() : BigDecimal.ZERO));
        }

        String contractId = contract != null ? "" + contract.contractId : "";
        return new Debtor(info.localId, info.filialId, info.outletId, MyArray.from(r),
                info.dealId, contractId, info.debtorDate, "");
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(currency).toSuper();
    }
}
