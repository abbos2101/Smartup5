package uz.greenwhite.smartup5_trade.m_near.bean;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.smartup5_trade.m_near.util.MapUtil;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class MapItem {

    public final LatLng latLng;
    public final Outlet outlet;

    public final CharSequence title;
    public final CharSequence detail;

    public final int distance;

    public Marker marker;

    @SuppressWarnings("ConstantConditions")
    public MapItem(Outlet outlet, LatLng nearLatLng) {
        String location = outlet.latLng;
        this.latLng = MapUtil.convertStringToLatLng(location);
        if (latLng == null) {
            throw new AppError("LatLng is null \"" + location + "\"");
        }

        this.outlet = outlet;
        this.title = outlet.name;
        this.distance = NearMapUtil.distanceBetweenInMeter(nearLatLng, latLng);
        this.detail = getDetail();
    }

    private CharSequence getDetail() {
        StringBuilder sb = new StringBuilder();
        if (distance >= 1000) {
            int km = distance / 1000;
            int m = distance % 1000;
            sb.append(km).append(".").append(m).append(" km");
        } else {
            sb.append(distance).append(" м");
        }
        return sb.toString();
    }
}
