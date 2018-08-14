package uz.greenwhite.smartup5_trade.m_outlet.ui.row;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;

public abstract class OutletDealInfo {

    public static final int DEAL = 1;
    public static final int SDEAL = 2;
    public static final int DEBTOR = 3;

    public abstract CharSequence getTitle();

    public abstract CharSequence getDetail();

    public abstract CharSequence getError();

    public abstract boolean hasEdit();

    public abstract int getInfoType();

    public abstract EntryState getEntryState();

    @Nullable
    public abstract Tuple2 getStateIcon();

    @Nullable
    Tuple2 evalStateIconResId(EntryState deal) {
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
