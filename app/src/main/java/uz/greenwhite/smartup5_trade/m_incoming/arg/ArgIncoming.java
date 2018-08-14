package uz.greenwhite.smartup5_trade.m_incoming.arg;

import android.text.TextUtils;

import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_incoming.bean.Incoming;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHeader;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingProduct;
import uz.greenwhite.smartup5_trade.m_warehouse.arg.ArgWarehouse;

public class ArgIncoming extends ArgWarehouse {

    public final String entryId;

    public ArgIncoming(ArgWarehouse arg, String entryId) {
        super(arg, arg.warehouseId);
        this.entryId = entryId;
    }

    protected ArgIncoming(UzumReader in) {
        super(in);
        entryId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(entryId);
    }

    public IncomingHolder getIncomingHolder() {
        Scope scope = getScope();
        if (TextUtils.isEmpty(entryId)) {
            String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
            String id = String.valueOf(AdminApi.nextSequence());
            Incoming incoming = new Incoming(id, filialId, warehouseId, IncomingHeader.makeDefault(today), MyArray.<IncomingProduct>emptyArray());
            return new IncomingHolder(incoming, EntryState.NOT_SAVED_ENTRY);
        } else {
            return scope.entry.getIncoming(entryId);
        }
    }

    public static final UzumAdapter<ArgIncoming> UZUM_ADAPTER = new UzumAdapter<ArgIncoming>() {
        @Override
        public ArgIncoming read(UzumReader in) {
            return new ArgIncoming(in);
        }

        @Override
        public void write(UzumWriter out, ArgIncoming val) {
            val.write(out);
        }
    };
}
