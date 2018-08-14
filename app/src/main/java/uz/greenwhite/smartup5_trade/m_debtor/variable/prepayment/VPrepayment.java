package uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorPayment;

public class VPrepayment extends VariableLike {

    public final String entryId;
    public final String filialId;
    public final String outletId;
    public final String paymentKind;
    public final EntryState entryState;
    public final ValueArray<VPrepaymentCurrency> currencies;

    public VPrepayment(String entryId,
                       String filialId,
                       String outletId,
                       String paymentKind,
                       EntryState entryState,
                       ValueArray<VPrepaymentCurrency> currencies) {
        this.entryId = entryId;
        this.filialId = filialId;
        this.outletId = outletId;
        this.paymentKind = paymentKind;
        this.entryState = entryState;
        this.currencies = currencies;
    }

    public Debtor convert() {
        ArrayList<DebtorPayment> r = new ArrayList<>();
        for (VPrepaymentCurrency c : currencies.getItems()) {
            for (VPrepaymentPayment p : c.payments.getItems()) {
                if (p.hasValue()) {
                    r.add(new DebtorPayment(
                            c.currency.currencyId,
                            p.paymentType.id,
                            p.amount.getQuantity(),
                            BigDecimal.ZERO));
                }
            }
        }
        String debtorDate = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
        return new Debtor(entryId, filialId, outletId, MyArray.from(r), "", "", debtorDate, paymentKind);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return currencies.getItems().toSuper();
    }
}
