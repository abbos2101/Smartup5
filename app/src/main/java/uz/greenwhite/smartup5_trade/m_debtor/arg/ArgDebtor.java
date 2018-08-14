package uz.greenwhite.smartup5_trade.m_debtor.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;

public class ArgDebtor extends ArgOutlet {

    public final String entryId;
    public final String dealId;
    public final String debtorDate;
    public final String paymentKind;
    public final boolean consign;


    public ArgDebtor(ArgOutlet arg, String entryId, String dealId, String debtorDate, String paymentKind, boolean consign) {
        super(arg, arg.outletId);
        this.entryId = entryId;
        this.dealId = dealId;
        this.debtorDate = debtorDate;
        this.paymentKind = paymentKind;
        this.consign = consign;
    }

    public ArgDebtor(ArgOutlet arg, String entryId, String dealId, String debtorDate, String paymentKind) {
        this(arg, entryId, dealId, debtorDate, paymentKind, false);
    }

    public ArgDebtor(ArgOutlet arg, String entryId, String dealId, String debtorDate, boolean consign) {
        this(arg, entryId, dealId, debtorDate, "", consign);
    }

    public ArgDebtor(ArgOutlet arg, String entryId, String dealId, String debtorDate) {
        this(arg, entryId, dealId, debtorDate, "");
    }

    public ArgDebtor(ArgOutlet arg, String entryId, String paymentKind) {
        this(arg, entryId, "", "", paymentKind);
    }

    public ArgDebtor(ArgOutlet arg, String paymentKind) {
        this(arg, "", "", "", paymentKind);
    }

    public ArgDebtor(UzumReader in) {
        super(in);
        this.entryId = in.readString();
        this.dealId = in.readString();
        this.debtorDate = in.readString();
        this.paymentKind = in.readString();
        this.consign = in.readBoolean();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.entryId);
        w.write(this.dealId);
        w.write(this.debtorDate);
        w.write(this.paymentKind);
        w.write(this.consign);
    }

    public static final UzumAdapter<ArgDebtor> UZUM_ADAPTER = new UzumAdapter<ArgDebtor>() {
        @Override
        public ArgDebtor read(UzumReader in) {
            return new ArgDebtor(in);
        }

        @Override
        public void write(UzumWriter out, ArgDebtor val) {
            val.write(out);
        }
    };
}
