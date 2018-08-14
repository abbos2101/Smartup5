package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealMemoModule extends DealModule {

    public final DealMemo memo;

    public DealMemoModule(DealMemo memo) {
        super(VisitModule.M_MEMO);
        this.memo = memo;
        AppError.checkNull(memo);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealMemoModule(in.readValue(DealMemo.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            out.write(((DealMemoModule) val).memo, DealMemo.UZUM_ADAPTER);
        }
    };

}
