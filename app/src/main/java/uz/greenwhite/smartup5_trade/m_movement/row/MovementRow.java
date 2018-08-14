package uz.greenwhite.smartup5_trade.m_movement.row;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncoming;

public class MovementRow {

    public final int image;
    public final CharSequence title;
    public final CharSequence detail;
    public final Tuple2 icon;

    public final MovementIncoming incoming;
    public final EntryState entryStates;

    public MovementRow(MovementIncoming incoming, EntryState entryStates) {
        this.incoming = incoming;
        this.image = getIconBackground(incoming.movementId);
        this.title = UI.html().v(incoming.fromFilial).br().v(incoming.fromWarehouse).html();
        this.detail = incoming.getStateName();
        this.icon = evalStateIconResId(entryStates);
        this.entryStates = entryStates;
    }


    @Nullable
    private Tuple2 evalStateIconResId(EntryState entryStates) {
        Context context = SmartupApp.getContext();
        if (!TextUtils.isEmpty(entryStates.serverResult)) {
            Drawable drawable = EntryState.getIconResId(EntryState.ERROR_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.red);
            return new Tuple2(drawable, bgr);

        } else if (entryStates.isLocked()) {
            Drawable drawable = EntryState.getIconResId(EntryState.LOCKED_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.red);
            return new Tuple2(drawable, bgr);

        } else if (entryStates.isReady()) {
            Drawable drawable = EntryState.getIconResId(EntryState.READY_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.green);
            return new Tuple2(drawable, bgr);

        } else if (entryStates.isSaved()) {
            Drawable drawable = EntryState.getIconResId(EntryState.SAVED_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.app_color_7);
            return new Tuple2(drawable, bgr);
        }
        return null;
    }

    private int getIconBackground(String id) {
        String lastNumber = id.substring(id.length() - 1, id.length());
        switch (lastNumber) {
            case "1":
                return R.drawable.bg_1;
            case "2":
                return R.drawable.bg_2;
            case "3":
                return R.drawable.bg_3;
            case "4":
                return R.drawable.bg_4;
            case "5":
                return R.drawable.bg_5;
            case "6":
                return R.drawable.bg_6;
            case "7":
                return R.drawable.bg_7;
            case "8":
                return R.drawable.bg_2;
            case "9":
                return R.drawable.bg_6;
            case "0":
                return R.drawable.bg_7;
            default:
                return R.drawable.bg_3;
        }
    }
}
