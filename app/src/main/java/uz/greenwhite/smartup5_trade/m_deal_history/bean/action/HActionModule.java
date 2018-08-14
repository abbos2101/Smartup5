package uz.greenwhite.smartup5_trade.m_deal_history.bean.action;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealProduct;

public class HActionModule extends HDealModule {

    public final MyArray<HDealProduct> actions;

    public HActionModule(MyArray<HDealProduct> actions) {
        super(K_ACTION);
        this.actions = actions;
    }

    public static final UzumAdapter<HDealModule> UZUM_ADAPTER = new UzumAdapter<HDealModule>() {
        @Override
        public HDealModule read(UzumReader in) {
            return new HActionModule(in.readArray(HDealProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, HDealModule val) {
            out.write(((HActionModule) val).actions, HDealProduct.UZUM_ADAPTER);
        }
    };
}
