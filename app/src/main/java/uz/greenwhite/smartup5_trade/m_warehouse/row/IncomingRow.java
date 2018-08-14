package uz.greenwhite.smartup5_trade.m_warehouse.row;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;

public class IncomingRow {

    public static Drawable EDIT_DRAWABLE = UI.changeDrawableColor(SmartupApp.getContext(),
            R.drawable.ic_edit_black_24dp, R.color.colorAccent);

    private Tuple2 icon;
    public final IncomingHolder holder;

    public IncomingRow(IncomingHolder holder) {
        this.holder = holder;
    }

    @Nullable
    public Tuple2 getStateIcon() {
        if (icon == null) {
            icon = evalStateIconResId(holder.state);
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
}
