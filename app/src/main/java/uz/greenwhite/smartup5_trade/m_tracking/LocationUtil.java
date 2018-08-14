package uz.greenwhite.smartup5_trade.m_tracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;

public class LocationUtil {

    public static CharSequence getDistance(String fromLocation, String toLocation) {
        if (TextUtils.isEmpty(fromLocation) || TextUtils.isEmpty(toLocation)) {
            return "";
        }
        return getDetail(NearMapUtil.distanceBetweenInMeter(
                LocationUtil.convertStringToLatLng(fromLocation),
                LocationUtil.convertStringToLatLng(toLocation)));
    }

    private static CharSequence getDetail(int distance) {
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

    public static LatLng convertStringToLatLng(String latLng) {
        try {
            String[] code = latLng.split(",");
            double lat = Double.parseDouble(code[0]);
            double lng = Double.parseDouble(code[1]);
            return new LatLng(lat, lng);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkGoogleMapsLocation(final MoldContentFragment fragment) {
        if (!LocationUtil.isGoogleMapsInstalled(fragment)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
            builder.setMessage(DS.getString(R.string.tracking_install_google_map));
            builder.setCancelable(true);
            builder.setPositiveButton(DS.getString(R.string.tracking_install), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.google.android.apps.maps"));
                    fragment.startActivity(intent);
                }
            });
            builder.create().show();
        }
    }

    public static boolean isGoogleMapsInstalled(MoldContentFragment fragment) {
        return isGoogleMapsInstalled(fragment.getActivity());
    }

    public static boolean isGoogleMapsInstalled(Activity activity) {
        try {
            PackageManager pm = activity.getPackageManager();
            pm.getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException ex) {
            return false;
        }
    }

}
