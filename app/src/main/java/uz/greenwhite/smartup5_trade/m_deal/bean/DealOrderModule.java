package uz.greenwhite.smartup5_trade.m_deal.bean;// 30.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealOrderModule extends DealModule {

    public final MyArray<DealOrder> orders;

    public DealOrderModule(MyArray<DealOrder> orders) {
        super(VisitModule.M_ORDER);
        this.orders = orders;
        this.orders.checkUniqueness(DealOrder.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealOrderModule(in.readArray(DealOrder.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealOrderModule v = (DealOrderModule) val;
            out.write(v.orders, DealOrder.UZUM_ADAPTER);
        }
    };
}
