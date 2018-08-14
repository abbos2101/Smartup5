package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;// 28.12.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Dashboard {

    public final MyArray<DOutlet> outlets;
    public final MyArray<DAccount> accounts;

    public Dashboard(MyArray<DOutlet> outlets,
                     MyArray<DAccount> accounts) {
        this.outlets = MyArray.nvl(outlets);
        this.accounts = MyArray.nvl(accounts);
    }

    public static final Dashboard DEFAULT = new Dashboard(null, null);

    public static final UzumAdapter<Dashboard> UZUM_ADAPTER = new UzumAdapter<Dashboard>() {
        @Override
        public Dashboard read(UzumReader in) {
            return new Dashboard(in.readArray(DOutlet.UZUM_ADAPTER),
                    in.readArray(DAccount.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, Dashboard val) {
            out.write(val.outlets, DOutlet.UZUM_ADAPTER);
            out.write(val.accounts, DAccount.UZUM_ADAPTER);
        }
    };
}
