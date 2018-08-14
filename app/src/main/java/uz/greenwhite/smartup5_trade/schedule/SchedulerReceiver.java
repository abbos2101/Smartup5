package uz.greenwhite.smartup5_trade.schedule;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;

import java.util.Map;

import uz.greenwhite.lib.GWSLOG;
import uz.greenwhite.lib.Tuple4;
import uz.greenwhite.lib.job.JobApi;
import uz.greenwhite.lib.job.internal.Manager;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;

public class SchedulerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context ctx, Intent intent) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GWSLOG.log("SchedulerReceiver.onReceive");
                    try {
                        Map<String, Tuple4> result = ScheduleUtil.isWorking();

                        boolean hasBackgroundWork = false;
                        boolean hasTracking = false;
                        // Tuple(boolean, boolean, Integer) -> gpsEnable, syncEnable, syncInterval
                        for (final Map.Entry<String, Tuple4> val : result.entrySet()) {
                            final String accountId = val.getKey();
                            Tuple4 setting = val.getValue();
                            final Boolean gpsEnable = (Boolean) setting.first;
                            final Boolean syncEnable = (Boolean) setting.second;
                            final Integer syncInterval = (Integer) setting.third;

                            Manager.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        trackingEnable(ctx, accountId, gpsEnable);
                                        syncEnable(ctx, accountId, syncEnable, syncInterval);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            if (gpsEnable || syncEnable) {
                                hasBackgroundWork = true;
                            }
                            if (gpsEnable) {
                                hasTracking = true;
                            }

                            if (!GPSPref.calledNotifyCareWork(accountId) && (Boolean) setting.fourth) {
                                GPSPref.setNotifyCareWorkTime(accountId);

                                GPSUtil.startNotify(ctx, ctx.getString(R.string.notify_care_work_title),
                                        ctx.getString(R.string.notify_care_work), GPSUtil.CARE_WORK);
                            }
                        }
                        if (hasBackgroundWork) {
                            isIgnoringBatteryOptimizations(ctx);
                        }

                        if (!hasTracking) {
                            ctx.stopService(new Intent(ctx, TrackingService.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ErrorUtil.saveThrowable(e);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e);
        }
    }

    private void trackingEnable(Context ctx, String accountId, Boolean gpsEnable) {
        if (ctx == null) return;

        if (gpsEnable && !ScheduleUtil.isServiceRunning(ctx, TrackingService.class.getName())) {
            ctx.startService(new Intent(ctx, TrackingService.class));
        }

        if (gpsEnable && SysUtil.isNetworkConnected(ctx)) {
            String lastSyncMillis = AdminApi.getLastGPSScheduleMillis(accountId);

            long lastSyncTime = TextUtils.isEmpty(lastSyncMillis) ? 0 : Long.parseLong(lastSyncMillis);
            long current = System.currentTimeMillis();
            long last = ((2 * 60) * 1000) + lastSyncTime;
            if (current > last && !JobApi.isRunning(TrackingJob.key(accountId))) {
                AdminApi.saveLastGPSScheduleMillis(accountId, String.valueOf(System.currentTimeMillis()));
                JobApi.execute(new TrackingJob(AdminApi.getAccount(accountId)));
            }
        }
    }

    private void syncEnable(Context context, String accountId, Boolean syncEnable, Integer syncInterval) {
        if (context == null) return;
        try {
            if (syncEnable && SysUtil.isNetworkConnected(context)) {
                String lastSyncMillis = AdminApi.getLastSyncScheduleMillis(accountId);

                long lastSyncTime = TextUtils.isEmpty(lastSyncMillis) ? 0 : Long.parseLong(lastSyncMillis);
                long current = System.currentTimeMillis();
                long last = ((Math.max(syncInterval, 15) * 60) * 1000) + lastSyncTime;
                if (current > last && !JobApi.isRunning(TapeSyncJob.key(accountId))) {
                    AdminApi.saveLastSyncScheduleMillis(accountId, String.valueOf(System.currentTimeMillis()));
                    JobApi.execute(new TapeSyncJob(AdminApi.getAccount(accountId)));
                }
            }
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            e.printStackTrace();
        }
    }

    private void isIgnoringBatteryOptimizations(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
                GPSUtil.startDozeModeNotify(ctx, GPSUtil.DOZEMODE_ENABLE);
            }
        }
    }
}
