package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealGiftModule extends DealModule {

    public final MyArray<DealGift> gifts;

    public DealGiftModule(MyArray<DealGift> gifts) {
        super(VisitModule.M_GIFT);
        this.gifts = gifts;
        this.gifts.checkUniqueness(DealGift.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealGiftModule(in.readArray(DealGift.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealGiftModule v = (DealGiftModule) val;
            out.write(v.gifts, DealGift.UZUM_ADAPTER);
        }
    };
}
