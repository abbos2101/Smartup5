package uz.greenwhite.smartup5_trade.m_deal.bean.retail_audit;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealRetailAuditModule extends DealModule {

    public final MyArray<DealRetailAudit> items;

    public DealRetailAuditModule(MyArray<DealRetailAudit> items) {
        super(VisitModule.M_RETAIL_AUDIT);
        this.items = items;
        this.items.checkUniqueness(DealRetailAudit.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealRetailAuditModule(in.readArray(DealRetailAudit.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            DealRetailAuditModule v = (DealRetailAuditModule) val;
            out.write(v.items, DealRetailAudit.UZUM_ADAPTER);
        }
    };
}
