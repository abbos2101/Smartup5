package uz.greenwhite.smartup5_trade.m_near.util;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

public class MapUtil {

    public static LatLng convertStringToLatLng(String latLng) {
        try {
            String[] code = latLng.split(",");
            double lat = Double.parseDouble(code[0]);
            double lng = Double.parseDouble(code[1]);
            return new LatLng(lat, lng);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String convertLatLngToString(LatLng latLng) {
        StringBuilder sb = new StringBuilder();
        sb.append(latLng.latitude);
        sb.append(",");
        sb.append(latLng.longitude);
        return sb.toString();
    }

    public static void checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                    9000).show();
        }
    }
}
