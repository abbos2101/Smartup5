package uz.greenwhite.smartup5_trade.m_deal.bean.recom;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealRecomModule extends DealModule {

    public final MyArray<DealRecom> items;

    public DealRecomModule(MyArray<DealRecom> items) {
        super(VisitModule.M_RECOM);
        this.items = items;
        this.items.checkUniqueness(DealRecom.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealRecomModule(in.readArray(DealRecom.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealRecomModule v = (DealRecomModule) val;
            out.write(v.items, DealRecom.UZUM_ADAPTER);
        }
    };
}
