package uz.greenwhite.smartup5_trade.m_session.row;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_debtor.bean.DebtorHolder;
import uz.greenwhite.smartup5_trade.m_near.util.MapUtil;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class DebtorRow {

    public final int image;
    public final Tuple2 icon;
    public final Outlet outlet;
    public final CharSequence title;
    public final CharSequence detail;

    public final boolean hasDebt;
    public final boolean hasPrepayment;

    public final MyArray<String> debtorDates;

    @Nullable
    private PersonBalanceReceivable balanceReceivable;
    private Boolean showPersonDebtAmount;

    public DebtorRow(Outlet outlet, MyArray<DebtorHolder> holders, MyArray<String> expireDate, boolean completeVisit) {
        //TODO  completedVisit
        this.image = outlet.getIconBackground();
        this.icon = evalStateIconResId(completeVisit, holders.map(new MyMapper<DebtorHolder, EntryState>() {
            @Override
            public EntryState apply(DebtorHolder holder) {
                return holder.entryState;
            }
        }));
        this.outlet = outlet;
        this.title = outlet.name;
        this.detail = UI.html().v(DS.getString(R.string.session_outlet_address, outlet.getAddress())).html();

        this.debtorDates = expireDate;

        this.hasDebt = holders.filter(new MyPredicate<DebtorHolder>() {
            @Override
            public boolean apply(DebtorHolder val) {
                return !val.debtor.isPrepayment();
            }
        }).size() > 0;

        this.hasPrepayment = holders.filter(new MyPredicate<DebtorHolder>() {
            @Override
            public boolean apply(DebtorHolder val) {
                return val.debtor.isPrepayment();
            }
        }).size() > 0;
    }

    public CharSequence getBalanceReceivable() {
        if (this.balanceReceivable != null &&
                balanceReceivable.amount.compareTo(BigDecimal.ZERO) != 0) {
            ShortHtml html = UI.html();
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
            return html.v("Баланс: ").v(balanceText).c().html();
        }
        return "";
    }

    public void setBalanceReceivable(@Nullable PersonBalanceReceivable balanceReceivable) {
        this.balanceReceivable = balanceReceivable;
    }

    public void setPersonDebtAmount(boolean showPersonDebtAmount) {
        this.showPersonDebtAmount = showPersonDebtAmount;
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

    public static final MyMapper<DebtorRow, String> KEY_ADAPTER = new MyMapper<DebtorRow, String>() {
        @Override
        public String apply(DebtorRow debtorRow) {
            return debtorRow.outlet.id;
        }
    };
}
