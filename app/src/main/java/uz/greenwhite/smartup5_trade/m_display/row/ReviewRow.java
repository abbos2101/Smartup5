package uz.greenwhite.smartup5_trade.m_display.row;


import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayRequest;
import uz.greenwhite.smartup5_trade.m_display.variable.VReview;

public class ReviewRow {

    public final String displayName;
    public final String displayCode;
    public final String displayPhoto;
    public final String inventNumber;

    public final String barcode, displayInventId;
    public final boolean photo, note;

    public final int state;

    public final VReview item;

    public boolean last = false, firstItem = false;

    public ReviewRow(String displayName,
                     String displayCode,
                     String displayPhoto,
                     String inventNumber,
                     String barcode,
                     String displayInventId,
                     boolean photo,
                     boolean note,
                     int state,
                     VReview item) {
        this.displayName = displayName;
        this.displayCode = displayCode;
        this.displayPhoto = displayPhoto;
        this.inventNumber = inventNumber;
        this.barcode = barcode;
        this.displayInventId = displayInventId;
        this.photo = photo;
        this.note = note;
        this.state = state;
        this.item = item;
    }

    public CharSequence getTitle() {
        String title = item.isNew() ? DS.getString(R.string.outlet_display_missing) : displayName;
        return UI.html().i().v(title).i().html();
    }

    public CharSequence getDetail() {
        ShortHtml html = UI.html();
        boolean hasValue = false;
        if (!TextUtils.isEmpty(displayCode)) {
            hasValue = true;
            html.v(DS.getString(R.string.outlet_barcode_code, displayCode));
        }
        if (!TextUtils.isEmpty(inventNumber)) {
            if (hasValue) {
                html.br();
            }
            html.v(DS.getString(R.string.outlet_barcode_invent_number, inventNumber));
            hasValue = true;
        }

        if (hasValue) {
            html.br();
        }

        if (TextUtils.isEmpty(barcode)) {
            html.v(DS.getString(R.string.outlet_barcode_not_specified));
        } else {
            html.v(DS.getString(R.string.outlet_barcode_detail, barcode));
        }
        return html.html();
    }

    @StringRes
    public int getHeaderTextResId() {
        return !item.isNew() ? R.string.outlet_display_header_invent : R.string.outlet_display_header_barcode;
    }

    public Drawable getIconState() {
        switch (state) {
            case DisplayRequest.NEW:
                return changeIconColor(R.drawable.ic_state_draft, R.color.app_color_7);
            case DisplayRequest.FOUND:
                return changeIconColor(R.drawable.ic_state_complete, R.color.green);
            case DisplayRequest.LINKED:
                return changeIconColor(R.drawable.ic_state_ready, R.color.green);
            case DisplayRequest.NOT_FOUND:
                return DS.getDrawable(R.drawable.data_state_clear);
            default:
                throw AppError.Unsupported();
        }
    }

    public static final MyMapper<ReviewRow, String> KEY_ADAPTER = new MyMapper<ReviewRow, String>() {
        @Override
        public String apply(ReviewRow reviewRow) {
            return reviewRow.barcode;
        }
    };

    private static Drawable changeIconColor(int iconResId, int colorResId) {
        return UI.changeDrawableColor(SmartupApp.getContext(), iconResId, colorResId);
    }
}
