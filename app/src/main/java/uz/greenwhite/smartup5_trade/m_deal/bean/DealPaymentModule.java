package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealPaymentModule extends DealModule {

    public final MyArray<DealPayment> payments;

    public DealPaymentModule(MyArray<DealPayment> payments) {
        super(VisitModule.M_PAYMENT);
        this.payments = payments;
        this.payments.checkUniqueness(DealPayment.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealPaymentModule(in.readArray(DealPayment.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealPaymentModule v = (DealPaymentModule) val;
            out.write(v.payments, DealPayment.UZUM_ADAPTER);
        }
    };
}
