package uz.greenwhite.smartup5_trade.m_deal_history.bean.service;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealProduct;

public class HServiceModule extends HDealModule {

    public final MyArray<HDealProduct> services;

    public HServiceModule(MyArray<HDealProduct> services) {
        super(K_SERVICE);
        this.services = services;
    }

    public static final UzumAdapter<HDealModule> UZUM_ADAPTER = new UzumAdapter<HDealModule>() {
        @Override
        public HDealModule read(UzumReader in) {
            return new HServiceModule(in.readArray(HDealProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, HDealModule val) {
            out.write(((HServiceModule) val).services, HDealProduct.UZUM_ADAPTER);
        }
    };
}
