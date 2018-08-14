package uz.greenwhite.smartup5_trade.m_session.row;// 27.06.2016

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_near.util.MapUtil;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;

public class OutletRow {

    public final int image;
    public final CharSequence title;
    public final CharSequence detail;

    public final Outlet outlet;
    public final PersonLastInfo lastInfo;
    @Nullable
    private final OutletType speciality;

    @Nullable
    private PersonBalanceReceivable balanceReceivable;
    private Boolean showPersonDebtAmount;

    @Nullable
    private Tuple2 icon;
    private boolean visited;

    public OutletRow(Outlet outlet, PersonLastInfo lastInfo, @Nullable OutletType speciality) {
        this.outlet = outlet;
        this.lastInfo = lastInfo;
        this.speciality = speciality;
        this.title = outlet.name;
        this.detail = outlet.getAddress();
        this.image = getIconBackground(outlet.id);
    }

    public CharSequence getOutletDistance(Location location) {
        try {
            if (outlet == null || TextUtils.isEmpty(outlet.latLng) || location == null) return "";
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

    public CharSequence getBalanceReceivable() {
        ShortHtml html = UI.html();

        if (speciality != null) {
            html.v(DS.getString(R.string.speciality)).v(": ").v(speciality.name).br();
        }

        if (this.balanceReceivable != null &&
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

    public void setBalanceReceivable(@Nullable PersonBalanceReceivable balanceReceivable) {
        this.balanceReceivable = balanceReceivable;
    }

    public void setPersonDebtAmount(boolean showPersonDebtAmount) {
        this.showPersonDebtAmount = showPersonDebtAmount;
    }

    @Nullable
    public Tuple2 getIcon() {
        return icon;
    }

    public boolean isVisited() {
        return visited;
    }

    public void populateState(boolean completedVisit, MyArray<DealHolder> deals) {
        this.icon = evalStateIconResId(completedVisit, deals.map(new MyMapper<DealHolder, EntryState>() {
            @Override
            public EntryState apply(DealHolder holder) {
                return holder.entryState;
            }
        }));
        this.visited = deals.nonEmpty();
    }

    public String getLstVisitDate() {
        if (lastInfo == null || !lastInfo.hasLastDate()) return "";
        try {
            String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
            String lastVisit = DateUtil.convert(lastInfo.lastVisit, DateUtil.FORMAT_AS_DATE);
            if (today.equals(lastVisit)) {
                return DS.getString(R.string.session_last_visit_inf,
                        DateUtil.convert(lastInfo.lastVisit, FORMAT_AS_TIME));
            }
            return lastVisit;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            return "";
        }
    }

    @Nullable
    private Tuple2 evalStateIconResId(boolean completedVisit, MyArray<EntryState> deals) {
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

    private static final ThreadLocal<SimpleDateFormat> FORMAT_AS_TIME = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm", Locale.US);
        }
    };

    public static final MyMapper<OutletRow, String> KEY_ADAPTER = new MyMapper<OutletRow, String>() {
        @Override
        public String apply(OutletRow outletRow) {
            return outletRow.outlet.id;
        }
    };
}
