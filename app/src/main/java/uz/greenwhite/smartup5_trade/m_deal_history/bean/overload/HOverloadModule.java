package uz.greenwhite.smartup5_trade.m_deal_history.bean.overload;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealProduct;

public class HOverloadModule extends HDealModule {

    public final MyArray<HDealProduct> overloads;

    public HOverloadModule(MyArray<HDealProduct> overloads) {
        super(K_OVERLOAD);
        this.overloads = overloads;
    }

    public static final UzumAdapter<HDealModule> UZUM_ADAPTER = new UzumAdapter<HDealModule>() {
        @Override
        public HDealModule read(UzumReader in) {
            return new HOverloadModule(in.readArray(HDealProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, HDealModule val) {
            out.write(((HOverloadModule) val).overloads, HDealProduct.UZUM_ADAPTER);
        }
    };
}
