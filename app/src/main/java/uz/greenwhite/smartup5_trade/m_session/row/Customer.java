package uz.greenwhite.smartup5_trade.m_session.row;// 25.11.2016

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Locale;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_near.util.MapUtil;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DOutlet;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class Customer {

    public static Customer newCustomerShipped(Outlet outlet, boolean completedVisit, MyArray<SDealHolder> deals) {
        MyArray<EntryState> entryStates = deals.map(new MyMapper<SDealHolder, EntryState>() {
            @Override
            public EntryState apply(SDealHolder sDealHolder) {
                return sDealHolder.entryState;
            }
        });
        return new Customer(outlet, null, completedVisit, entryStates, entryStates.nonEmpty());
    }

    @Nullable
    public final Tuple2 icon;

    public final int image;

    public final CharSequence title;
    public final CharSequence detail;

    public final CustomerParam param;

    @Nullable
    public final Outlet outlet;
    @Nullable
    public final PersonLastInfo lastInfo;

    public Customer(Outlet outlet, PersonLastInfo lastInfo, boolean completedVisit, MyArray<EntryState> sdeals, boolean visited) {
        this.outlet = outlet;
        this.lastInfo = lastInfo;
        this.icon = evalStateIconResId(completedVisit, sdeals);
        this.title = outlet.name;
        this.detail = outlet.getAddress();
        this.param = CustomerParam.newInstance(outlet, visited, completedVisit);
        this.image = getIconBackground(outlet.id);
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
}
