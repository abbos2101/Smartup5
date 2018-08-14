package uz.greenwhite.smartup5_trade.m_deal.bean.action;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealActionModule extends DealModule {

    public final MyArray<DealAction> actions;

    public DealActionModule(MyArray<DealAction> actions) {
        super(VisitModule.M_ACTION);
        this.actions = actions;
        this.actions.checkUniqueness(DealAction.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealActionModule(in.readArray(DealAction.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealActionModule v = (DealActionModule) val;
            out.write(v.actions, DealAction.UZUM_ADAPTER);
        }
    };
}
