package uz.greenwhite.smartup5_trade.m_stocktaking.arg;

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
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.Stocktaking;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHeader;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHolder;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingProduct;
import uz.greenwhite.smartup5_trade.m_warehouse.arg.ArgWarehouse;

public class ArgStocktaking extends ArgWarehouse {

    public final String entryId;

    public ArgStocktaking(ArgWarehouse arg, String entryId) {
        super(arg, arg.warehouseId);
        this.entryId = entryId;
    }

    protected ArgStocktaking(UzumReader in) {
        super(in);
        this.entryId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(entryId);
    }

    public StocktakingHolder getStocktakingHolder() {
        Scope scope = getScope();
        if (TextUtils.isEmpty(entryId)) {
            String today = DateUtil.format(new Date(),DateUtil.FORMAT_AS_DATE);
            String id = String.valueOf(AdminApi.nextSequence());
            Stocktaking stocktaking = new Stocktaking(id, filialId, warehouseId, StocktakingHeader.makeDefault(today), MyArray.<StocktakingProduct>emptyArray());
            return new StocktakingHolder(stocktaking, EntryState.NOT_SAVED_ENTRY);
        } else {
            return scope.entry.getStocktaking(entryId);
        }
    }

    public static final UzumAdapter<ArgStocktaking> UZUM_ADAPTER = new UzumAdapter<ArgStocktaking>() {
        @Override
        public ArgStocktaking read(UzumReader in) {
            return new ArgStocktaking(in);
        }

        @Override
        public void write(UzumWriter out, ArgStocktaking val) {
            val.write(out);
        }
    };
}
