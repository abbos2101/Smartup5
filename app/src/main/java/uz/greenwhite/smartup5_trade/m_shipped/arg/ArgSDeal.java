package uz.greenwhite.smartup5_trade.m_shipped.arg;// 08.09.2016

import android.text.TextUtils;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class ArgSDeal extends ArgOutlet {

    public final String entryId;
    public final String dealId;

    public ArgSDeal(ArgOutlet arg, String entryId, String dealId) {
        super(arg, arg.outletId);
        this.entryId = entryId;
        this.dealId = dealId;
    }

    public ArgSDeal(UzumReader in) {
        super(in);
        this.entryId = in.readString();
        this.dealId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.entryId);
        w.write(this.dealId);
    }

    public SDealHolder getSDealHolder(Scope scope) {
        if (TextUtils.isEmpty(entryId)) {
            SDeal find = scope.ref.getSDeal(dealId);
            return new SDealHolder("", find, EntryState.NOT_SAVED_ENTRY);
        }

        return scope.entry.getSDealHolder(entryId);
    }

    public static final UzumAdapter<ArgSDeal> UZUM_ADAPTER = new UzumAdapter<ArgSDeal>() {
        @Override
        public ArgSDeal read(UzumReader in) {
            return new ArgSDeal(in);
        }

        @Override
        public void write(UzumWriter out, ArgSDeal val) {
            val.write(out);
        }
    };
}
