package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealReturnModule extends DealModule {

    public final MyArray<DealReturn> returns;

    public DealReturnModule(MyArray<DealReturn> returns) {
        super(VisitModule.M_RETURN);
        this.returns = returns;
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealReturnModule(in.readArray(DealReturn.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealReturnModule v = (DealReturnModule) val;
            out.write(v.returns, DealReturn.UZUM_ADAPTER);
        }
    };
}
