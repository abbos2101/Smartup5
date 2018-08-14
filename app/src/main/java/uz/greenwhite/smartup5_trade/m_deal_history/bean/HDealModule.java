package uz.greenwhite.smartup5_trade.m_deal_history.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.action.HActionModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.gift.HGiftModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.order.HOrderModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.overload.HOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.payment.HPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.service.HServiceModule;

public class HDealModule {

    public static final String K_ORDER = "2";
    public static final String K_GIFT = "3";
    public static final String K_ACTION = "4";
    public static final String K_OVERLOAD = "5";
    public static final String K_PAYMENT = "6";
    public static final String K_SERVICE = "7";

    public final String moduleId;

    public HDealModule(String moduleId) {
        this.moduleId = moduleId;
    }

    public static final MyMapper<HDealModule, String> KEY_ADAPTER = new MyMapper<HDealModule, String>() {
        @Override
        public String apply(HDealModule hDealModule) {
            return hDealModule.moduleId;
        }
    };

    public static final UzumAdapter<HDealModule> UZUM_ADAPTER = new UzumAdapter<HDealModule>() {

        public UzumAdapter<HDealModule> getModuleAdapter(String moduleId) {
            switch (moduleId) {
                case K_ORDER:
                    return HOrderModule.UZUM_ADAPTER;
                case K_GIFT:
                    return HGiftModule.UZUM_ADAPTER;
                case K_ACTION:
                    return HActionModule.UZUM_ADAPTER;
                case K_OVERLOAD:
                    return HOverloadModule.UZUM_ADAPTER;
                case K_PAYMENT:
                    return HPaymentModule.UZUM_ADAPTER;
                case K_SERVICE:
                    return HServiceModule.UZUM_ADAPTER;
                default:
                    return null;
            }
        }

        @Override
        public HDealModule read(UzumReader in) {
            UzumAdapter<HDealModule> adapter = getModuleAdapter(in.readString());
            return adapter == null ? null : adapter.read(in);
        }

        @Override
        public void write(UzumWriter out, HDealModule val) {
            out.write(val.moduleId);
            UzumAdapter<HDealModule> adapter = getModuleAdapter(val.moduleId);
            if (adapter != null) {
                adapter.write(out, val);
            }
        }
    };
}
