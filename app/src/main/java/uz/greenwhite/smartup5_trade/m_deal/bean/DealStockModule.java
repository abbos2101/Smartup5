package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealStockModule extends DealModule {

    public final MyArray<DealStock> stocks;

    public DealStockModule(MyArray<DealStock> stocks) {
        super(VisitModule.M_STOCK);
        this.stocks = stocks;
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealStockModule(in.readArray(DealStock.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealStockModule v = (DealStockModule) val;
            out.write(v.stocks, DealStock.UZUM_ADAPTER);
        }
    };
}
