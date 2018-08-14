package uz.greenwhite.smartup5_trade.m_deal_history.bean.gift;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealProduct;

public class HGiftModule extends HDealModule {

    public final MyArray<HDealProduct> gifts;

    public HGiftModule(MyArray<HDealProduct> gifts) {
        super(K_GIFT);
        this.gifts = gifts;
    }

    public static final UzumAdapter<HDealModule> UZUM_ADAPTER = new UzumAdapter<HDealModule>() {
        @Override
        public HDealModule read(UzumReader in) {
            return new HGiftModule(in.readArray(HDealProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, HDealModule val) {
            out.write(((HGiftModule) val).gifts, HDealProduct.UZUM_ADAPTER);
        }
    };
}
