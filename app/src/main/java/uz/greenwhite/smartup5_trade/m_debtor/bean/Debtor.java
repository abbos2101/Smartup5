package uz.greenwhite.smartup5_trade.m_debtor.bean;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Debtor {

    public final String localId;                   // 1
    public final String filialId;                  // 2
    public final String outletId;                  // 3
    public final MyArray<DebtorPayment> payments;  // 4
    public final String dealId;                    // 5
    public final String contractId;                // 6
    public final String debtorDate;                // 7
    public final String paymentKind;               // 8

    public Debtor(String localId,
                  String filialId,
                  String outletId,
                  MyArray<DebtorPayment> payments,
                  String dealId,
                  String contractId,
                  String debtorDate,
                  String paymentKind) {
        this.localId = localId;
        this.filialId = filialId;
        this.outletId = outletId;
        this.payments = payments;
        this.dealId = dealId;
        this.contractId = Util.nvl(contractId);
        this.debtorDate = debtorDate;
        this.paymentKind = Util.nvl(paymentKind);
    }

    public boolean isPrepayment() {
        return TextUtils.isEmpty(dealId) && !TextUtils.isEmpty(paymentKind);
    }

    public static final UzumAdapter<Debtor> UZUM_ADAPTER = new UzumAdapter<Debtor>() {
        @Override
        public Debtor read(UzumReader in) {
            return new Debtor(in.readString(), in.readString(),
                    in.readString(), in.readArray(DebtorPayment.UZUM_ADAPTER),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, Debtor val) {
            out.write(val.localId);                               // 1
            out.write(val.filialId);                              // 2
            out.write(val.outletId);                              // 3
            out.write(val.payments, DebtorPayment.UZUM_ADAPTER);  // 4
            out.write(val.dealId);                                // 5
            out.write(val.contractId);                            // 6
            out.write(val.debtorDate);                            // 7
            out.write(val.paymentKind);                           // 8
        }
    };
}
