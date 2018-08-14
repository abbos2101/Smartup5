package uz.greenwhite.smartup5_trade.m_deal_history.bean.order;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealProduct;

public class HOrderModule extends HDealModule {

    public final MyArray<HDealProduct> orders;

    public HOrderModule(MyArray<HDealProduct> orders) {
        super(K_ORDER);
        this.orders = orders;
    }

    public static final UzumAdapter<HDealModule> UZUM_ADAPTER = new UzumAdapter<HDealModule>() {
        @Override
        public HDealModule read(UzumReader in) {
            return new HOrderModule(in.readArray(HDealProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, HDealModule val) {
            out.write(((HOrderModule) val).orders, HDealProduct.UZUM_ADAPTER);
        }
    };
}
