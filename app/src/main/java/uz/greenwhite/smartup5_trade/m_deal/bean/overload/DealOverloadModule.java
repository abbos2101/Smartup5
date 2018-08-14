package uz.greenwhite.smartup5_trade.m_deal.bean.overload;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealOverloadModule extends DealModule {

    public final MyArray<DealOverload> overloads;

    public DealOverloadModule(MyArray<DealOverload> overloads) {
        super(VisitModule.M_OVERLOAD);
        this.overloads = overloads;
        this.overloads.checkUniqueness(DealOverload.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealOverloadModule(in.readArray(DealOverload.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealOverloadModule v = (DealOverloadModule) val;
            out.write(v.overloads, DealOverload.UZUM_ADAPTER);
        }
    };
}
