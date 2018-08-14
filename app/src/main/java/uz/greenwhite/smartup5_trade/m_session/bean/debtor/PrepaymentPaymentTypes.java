package uz.greenwhite.smartup5_trade.m_session.bean.debtor;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PrepaymentPaymentTypes {

    public static final String K_CASH = "C";
    public static final String K_CARD = "R";

    public final String paymentId;
    public final String currencyId;
    public final String paymentKind;

    public PrepaymentPaymentTypes(String paymentId, String currencyId, String paymentKind) {
        this.paymentId = paymentId;
        this.currencyId = currencyId;
        this.paymentKind = paymentKind;
    }

    public static final UzumAdapter<PrepaymentPaymentTypes> UZUM_ADAPTER = new UzumAdapter<PrepaymentPaymentTypes>() {
        @Override
        public PrepaymentPaymentTypes read(UzumReader in) {
            return new PrepaymentPaymentTypes(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PrepaymentPaymentTypes val) {
            out.write(val.paymentId);
            out.write(val.currencyId);
            out.write(val.paymentKind);
        }
    };
}
