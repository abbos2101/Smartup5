package uz.greenwhite.smartup5_trade.m_session.bean.debtor;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DebtorOutlet {

    public final String outletId;
    public final MyArray<DebtorDeal> deals;

    public DebtorOutlet(String outletId, MyArray<DebtorDeal> deals) {
        this.outletId = outletId;
        this.deals = deals;
    }

    public static final MyMapper<DebtorOutlet, String> KEY_ADAPTER = new MyMapper<DebtorOutlet, String>() {
        @Override
        public String apply(DebtorOutlet val) {
            return val.outletId;
        }
    };

    public static final UzumAdapter<DebtorOutlet> UZUM_ADAPTER = new UzumAdapter<DebtorOutlet>() {
        @Override
        public DebtorOutlet read(UzumReader in) {
            return new DebtorOutlet(in.readString(), in.readArray(DebtorDeal.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DebtorOutlet val) {
            out.write(val.outletId);
            out.write(val.deals, DebtorDeal.UZUM_ADAPTER);
        }
    };
}
