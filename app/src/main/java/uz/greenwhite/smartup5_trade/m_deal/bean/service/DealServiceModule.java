package uz.greenwhite.smartup5_trade.m_deal.bean.service;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealServiceModule extends DealModule {

    public final MyArray<DealService> items;

    public DealServiceModule(MyArray<DealService> items) {
        super(VisitModule.M_SERVICE);
        this.items = items;
        this.items.checkUniqueness(DealService.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealServiceModule(in.readArray(DealService.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealServiceModule v = (DealServiceModule) val;
            out.write(v.items, DealService.UZUM_ADAPTER);
        }
    };
}
