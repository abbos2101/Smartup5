package uz.greenwhite.smartup5_trade.m_session.ui.customer.row;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_near.util.MapUtil;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

@SuppressWarnings("WeakerAccess")
public abstract class CustomerRow {

    @Nullable
    public Tuple2 icon;

    public final int image;

    public final CharSequence title;
    public final CharSequence subTitle;
    public final CharSequence infoTitle;
    public final CharSequence detail;
    public final CharSequence subDetail;
    public final CharSequence balanceReceivable;

    public final Outlet outlet;

    @Nullable
    protected Boolean showPersonDebtAmount;
    @Nullable
    protected Boolean visited;

    public CustomerRow(Outlet outlet,
                       @Nullable PersonBalanceReceivable balanceReceivable,
                       @Nullable Boolean showPersonDebtAmount,
                       @Nullable Boolean completedVisit,
                       @Nullable MyArray<EntryState> entryStates) {
        this.outlet = outlet;
        this.showPersonDebtAmount = showPersonDebtAmount;


        this.title = getTitle();
        this.subTitle = getSubTitle();
        this.infoTitle = getInfoTitle();
        this.detail = getDetail();
        this.subDetail = getSubDetail();
        this.balanceReceivable = getBalanceReceivable(balanceReceivable);

        this.image = getIconBackground(outlet.id);

        if (completedVisit != null && entryStates != null) {
            populateState(completedVisit, entryStates);
        }
        this.visited = completedVisit;
    }

    public CustomerRow(Outlet outlet, @Nullable Boolean completedVisit, @Nullable MyArray<EntryState> entryStates) {
        this(outlet, null, null, completedVisit, entryStates);
    }

    public CustomerRow(Outlet outlet,
                       @Nullable PersonBalanceReceivable balanceReceivable,
                       @Nullable Boolean showPersonDebtAmount) {
        this(outlet, balanceReceivable, showPersonDebtAmount, null, null);
    }

    public CustomerRow(Outlet outlet) {
        this(outlet, null, null, null, null);
    }

    public void populateState(boolean completedVisit, MyArray<EntryState> entryStates) {
        this.icon = evalStateIconResId(completedVisit, entryStates);
        this.visited = entryStates.nonEmpty();
    }

    public CharSequence getOutletDistance(Location location) {
        if (outlet == null || TextUtils.isEmpty(outlet.latLng) || location == null) return "";
        try {
            LatLng foundLocation = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng latLng = MapUtil.convertStringToLatLng(outlet.latLng);
            if (latLng == null) {
                return "";
            }
            int distance = NearMapUtil.distanceBetweenInMeter(foundLocation, latLng);

            StringBuilder sb = new StringBuilder();
            if (distance >= 1000) {
                int km = distance / 1000;
                int m = distance % 1000;
                sb.append(km).append(".").append(m).append(" km");
            } else {
                sb.append(distance).append(" м");
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Nullable
    protected Tuple2 evalStateIconResId(boolean completedVisit, MyArray<EntryState> deals) {
        Context context = SmartupApp.getContext();
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

        } else if (deals.size() > 0) {
            Drawable drawable = EntryState.getIconResId(EntryState.SAVED_ENTRY, R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.app_color_7);
            return new Tuple2(drawable, bgr);
        }
        if (completedVisit) {
            Drawable drawable = EntryState.completedIcon(R.color.white);
            Drawable bgr = UI.changeDrawableColor(context, R.drawable.bg_1, R.color.green);
            return new Tuple2(drawable, bgr);

        } else {
            return null;
        }
    }

    protected int getIconBackground(String id) {
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

    //----------------------------------------------------------------------------------------------

    protected CharSequence getTitle() {
        return outlet.name;
    }

    protected CharSequence getSubTitle() {
        return "";
    }

    protected CharSequence getInfoTitle() {
        return "";
    }

    protected CharSequence getDetail() {
        return outlet.getAddress();
    }

    protected CharSequence getSubDetail() {
        return "";
    }

    protected CharSequence getBalanceReceivable(@Nullable PersonBalanceReceivable balanceReceivable) {
        ShortHtml html = UI.html();

        if (balanceReceivable != null &&
                balanceReceivable.amount.compareTo(BigDecimal.ZERO) != 0) {
            if (balanceReceivable.amount.compareTo(BigDecimal.ZERO) < 0) {
                html.c("#990000");
            } else {
                html.c("#09a667");
            }

            String balanceText = "";
            if (showPersonDebtAmount == null) {
                showPersonDebtAmount = true;
            }

            if (showPersonDebtAmount) {
                balanceText = NumberUtil.formatMoney(balanceReceivable.amount);
            } else {
                if (balanceReceivable.amount.compareTo(BigDecimal.ZERO) < 0) {
                    html.v(DS.getString(R.string.person_indebted));
                } else {
                    html.v(DS.getString(R.string.person_prepayment));
                }
                return html.c().html();

            }
            return html.v(DS.getString(R.string.session_person_balance_info)).v(balanceText).c().html();
        }
        return html.html();
    }

    public static final ThreadLocal<SimpleDateFormat> FORMAT_AS_TIME = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm", Locale.US);
        }
    };

    public static final MyMapper<CustomerRow, String> KEY_ADAPTER = new MyMapper<CustomerRow, String>() {
        @Override
        public String apply(CustomerRow outletRow) {
            return outletRow.outlet.id;
        }
    };
}
