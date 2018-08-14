package uz.greenwhite.smartup5_trade.m_deal.bean.agree;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealAgreeModule extends DealModule {

    public final MyArray<DealAgree> agrees;

    public DealAgreeModule(MyArray<DealAgree> agrees) {
        super(VisitModule.M_AGREE);
        this.agrees = agrees;
        this.agrees.checkUniqueness(DealAgree.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealAgreeModule(in.readArray(DealAgree.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealAgreeModule v = (DealAgreeModule) val;
            out.write(v.agrees, DealAgree.UZUM_ADAPTER);
        }
    };
}
