package uz.greenwhite.smartup5_trade.m_deal_history.bean.payment;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealModule;

public class HPaymentModule extends HDealModule {

    public final MyArray<HPayment> payments;

    public HPaymentModule(MyArray<HPayment> payments) {
        super(K_PAYMENT);
        this.payments = payments;
    }

    public static final UzumAdapter<HDealModule> UZUM_ADAPTER = new UzumAdapter<HDealModule>() {
        @Override
        public HDealModule read(UzumReader in) {
            return new HPaymentModule(in.readArray(HPayment.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, HDealModule val) {
            out.write(((HPaymentModule) val).payments, HPayment.UZUM_ADAPTER);
        }
    };
}
