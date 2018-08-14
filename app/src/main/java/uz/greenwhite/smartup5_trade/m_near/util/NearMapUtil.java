package uz.greenwhite.smartup5_trade.m_near.util;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.m_near.arg.ArgNearOutlet;
import uz.greenwhite.smartup5_trade.m_near.bean.MapItem;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_tracking.LocationUtil;

public class NearMapUtil {

    public static int dpToPixel(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int distanceBetweenInMeter(Location start, Location end) {
        if (start == null || end == null) return 0;
        return distanceBetweenInMeter(new LatLng(start.getLatitude(), start.getLongitude()),
                new LatLng(end.getLatitude(), end.getLongitude()));
    }

    public static int distanceBetweenInMeter(LatLng start, LatLng end) {
        if (start == null || end == null) {
            return 0;
        }

        float[] m = new float[1];
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, m);
        return (int) m[0];
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<MapItem> nearOutletList(ArgNearOutlet arg) {
        final LatLng nearLatLng = arg.getLatLng();
        MyArray<Outlet> outlets = DSUtil.getFilialOutlets(arg.getScope())
                .filter(new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(Outlet outlet) {
                        return outlet.isOutlet();
                    }
                });

        MyPredicate<Outlet> predicate = MyPredicate.True();
        predicate = predicate.and(getNotEmptyLocationPredicate());
        predicate = predicate.and(getFilterPredicate(arg.outletIds));

        return outlets.filter(predicate)
                .map(new MyMapper<Outlet, MapItem>() {
                    @Override
                    public MapItem apply(Outlet o) {
                        if (!TextUtils.isEmpty(o.latLng)) {
                            try {
                                LocationUtil.convertStringToLatLng(o.latLng);
                                return new MapItem(o, nearLatLng);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                }).filterNotNull()
                .sort(new Comparator<MapItem>() {
                    @Override
                    public int compare(MapItem l, MapItem r) {
                        return MyPredicate.compare(l.distance, r.distance);
                    }
                });
    }

    private static MyPredicate<Outlet> getNotEmptyLocationPredicate() {
        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                return !TextUtils.isEmpty(outlet.latLng);
            }
        };
    }

    private static MyPredicate<Outlet> getFilterPredicate(final MyArray<String> outletIds) {
        if (outletIds == null || outletIds.isEmpty()) {
            return null;
        }
        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                return outletIds.contains(outlet.id, MyMapper.<String>string());
            }
        };
    }

}
