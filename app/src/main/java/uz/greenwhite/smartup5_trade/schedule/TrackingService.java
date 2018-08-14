package uz.greenwhite.smartup5_trade.schedule;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;
import java.util.Map;

import uz.greenwhite.lib.GWSLOG;
import uz.greenwhite.lib.Tuple4;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.location.LocationHelper;
import uz.greenwhite.lib.location.LocationResult;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.bean.user.UserFilial;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.NotificationUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.RootUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;

@SuppressWarnings("MissingPermission")
public class TrackingService extends Service {

    public static final int K_GPS_TRACKING = 1;
    public static final int ENABLE_GPS = 100 + GPSUtil.GPS_ENABLE;
    public static final int GRAND_GPS = 100 + GPSUtil.GPS_SETTING_GRAND;
    public static final int MOCK_GPS = 100 + GPSUtil.MOCK_MODE;

    @Nullable
    private LocationHelper helper;

    private final Handler handler = new Handler();
    private LocationManager locationManager;
    private Location lastLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.gps_tracking))
                .setContentText(getString(R.string.track_your_location))
                .setSmallIcon(R.drawable.smartup_logo_black)
                .build();

        startForeground(K_GPS_TRACKING, notification);

        this.helper = LocationHelper.getTrackingLocation(this, new LocationResult() {
            @Override
            public void onLocationChanged(Location location) {
                changeLocation(location);
            }
        }, (5 * 60) * 1000); // 5 minute
    }

    private void runPermissionAndEnableCheck() {
        if (helper == null) return;

        if (locationManager == null) {
            this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPSUtil.startEnableLocationNotify(this, ENABLE_GPS);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper != null) {
                    runPermissionAndEnableCheck();
                    helper.startListener();
                } else {
                    handler.removeCallbacks(this);
                }
            }
        }, (5 * 60) * 1000); //5 minute
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (helper == null) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (SysUtil.checkSelfPermissionGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                SysUtil.checkSelfPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            helper.startListener();
            runPermissionAndEnableCheck();
        } else {
            GPSUtil.startGrandLocationNotify(this, GRAND_GPS);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.stopListener();
            helper = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //----------------------------------------------------------------------------------------------

    private void changeLocation(final Location location) {
        if (helper == null) return;

        GWSLOG.log("TrackingService.changeLocation: " + location);
        if (location == null || location.getAccuracy() > 80) {
            GWSLOG.log("TrackingService.changeLocation Location.getAccuracy() > 80");
            return;

        } else if (RootUtil.isMockLocationMode(location)) {
            GPSUtil.startMockModeNotify(TrackingService.this, MOCK_GPS);
            GWSLOG.log("TrackingService.changeLocation Location Mock");
            return;
        }

        if (lastLocation != null) {
            if (lastLocation.getLatitude() == location.getLatitude() &&
                    lastLocation.getLongitude() == location.getLongitude()) {
                GWSLOG.log("TrackingService.changeLocation Location Duplicate");
                return;
            }

            if (NearMapUtil.distanceBetweenInMeter(lastLocation, location) <= 10) {
                GWSLOG.log("distanceBetweenInMeter <= 10");
                return;
            }

            long lastLocationTime = lastLocation.getTime();
            long locationTime = location.getTime();
            long intervalLocationTimeInSecond = (locationTime - lastLocationTime) / 1000;
            GWSLOG.log("intervalLocationTimeInSecond:----------" + intervalLocationTimeInSecond);
            if (intervalLocationTimeInSecond <= 10) {
                return;
            }
        }

        lastLocation = location;

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveTracking(TrackingService.this, location);
                }
            }).start();
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            e.printStackTrace();
        }
    }

    private void saveTracking(TrackingService service, Location location) {
        try {
            // Tuple(boolean, boolean, Integer) -> gpsEnable, syncEnable, syncInterval
            Map<String, Tuple4> working = ScheduleUtil.isWorking();
            if (!working.isEmpty()) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(location.getTime());

                String latLng = "" + location.getLatitude() + "," + location.getLongitude();
                String time = DateUtil.format(c.getTime(), DateUtil.FORMAT_AS_DATETIME);
                String accuracy = "" + location.getAccuracy();
                String speed = "" + ((int) location.getSpeed());

                MyArray<String> gt = MyArray.from(latLng, accuracy, speed,
                        "gps".equals(location.getProvider()) ? "G" : "O", time);

                for (String accountId : working.keySet()) {
                    Tuple4 sc = working.get(accountId);
                    Boolean gpsEnable = (Boolean) sc.first;
                    if (!gpsEnable) continue;

                    DS.initScope(accountId, null);
                    Scope scope = DS.getScope(accountId, null);

                    String entryId = String.valueOf(AdminApi.nextSequence());
                    scope.ds.db.entrySave(entryId, "0", RT.GT, Uzum.toBytes(gt, UzumAdapter.STRING_ARRAY));
                    scope.ds.db.tryMakeStateReady(entryId);

                    try {
                        DS.initScope(accountId, Filial.FILIAL_HEAD);
                        scope = DS.getScope(accountId, Filial.FILIAL_HEAD);

                        for (UserFilial filial : scope.ref.getUser().filials) {
                            DS.initScope(accountId, filial.filialId);
                            scope = DS.getScope(accountId, filial.filialId);
                            NotificationUtil.notifyUser(scope.context, scope, location);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (working.isEmpty()) service.stopSelf();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }
}
