package uz.greenwhite.smartup5_trade.m_warehouse.row;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.util.Comparator;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHolder;

public class WarehouseRow {

    public static Drawable EDIT_DRAWABLE = UI.changeDrawableColor(SmartupApp.getContext(),
            R.drawable.ic_edit_black_24dp, R.color.colorAccent);

    public static final String T_INCOMING = "incoming";
    public static final String T_STOCKTAKING = "stocktaking";

    public final String entryId;
    public final String type;
    private Tuple2 icon;
    public final EntryState state;

    public final CharSequence title;
    public final CharSequence detail;

    public WarehouseRow(IncomingHolder holder) {
        this.type = T_INCOMING;
        this.entryId = holder.incoming.localId;
        this.state = holder.state;
        this.title = DS.getString(R.string.warehouse_incoming) + " №" + holder.incoming.localId;
        this.detail = UI.html().v(DS.getString(R.string.incoming_number)).v(": ").v(holder.incoming.header.incomingNumber).br()
                .v(DS.getString(R.string.incoming_date)).v(": ").v(holder.incoming.header.incomingDate).html();
    }

    public WarehouseRow(StocktakingHolder holder) {
        this.type = T_STOCKTAKING;
        this.entryId = holder.stocktaking.localId;
        this.state = holder.state;
        this.title = DS.getString(R.string.warehouse_stocktaking) + " №" + holder.stocktaking.localId;
        this.detail = UI.html().v(DS.getString(R.string.incoming_number)).v(": ").v(holder.stocktaking.header.number).br()
                .v(DS.getString(R.string.incoming_date)).v(": ").v(holder.stocktaking.header.date).html();
    }

    @Nullable
    public Tuple2 getStateIcon() {
        if (icon == null) {
            icon = evalStateIconResId(state);
        }
        return icon;
    }

    @Nullable
    private Tuple2 evalStateIconResId(EntryState deal) {
        Context context = SmartupApp.getContext();
        MyArray<EntryState> deals = MyArray.from(deal);
        if (deals.findFirst(Utils.HAS_ERROR_DEAL_PREDICATE) != null) {
            Drawable drawable = EntryState.getIconResId(EntryState.ERROR_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.red);
            return new Tuple2(drawable, bgr);

        } else if (deals.findFirst(Utils.HAS_LOCKED_DEAL_PREDICATE) != null) {
            Drawable drawable = EntryState.getIconResId(EntryState.LOCKED_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.red);
            return new Tuple2(drawable, bgr);

        } else if (deals.findFirst(Utils.HAS_READY_DEAL_PREDICATE) != null) {
            Drawable drawable = EntryState.getIconResId(EntryState.READY_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.green);
            return new Tuple2(drawable, bgr);

        } else if (deals.findFirst(Utils.HAS_SAVED_DEAL_PREDICATE) != null) {
            Drawable drawable = EntryState.getIconResId(EntryState.SAVED_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.app_color_7);
            return new Tuple2(drawable, bgr);
        }
        return null;
    }

    public static final Comparator<WarehouseRow> SORT = new Comparator<WarehouseRow>() {
        @Override
        public int compare(WarehouseRow l, WarehouseRow r) {
            return CharSequenceUtil.compareToIgnoreCase(l.entryId, r.entryId);
        }
    };
}
