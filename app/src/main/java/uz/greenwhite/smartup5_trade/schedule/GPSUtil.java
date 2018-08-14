package uz.greenwhite.smartup5_trade.schedule;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;

import uz.greenwhite.lib.BuildConfig;
import uz.greenwhite.smartup5_trade.R;

class GPSUtil {

    static final int MOCK_MODE = 2;
    static final int GPS_ENABLE = 3;
    static final int GPS_SETTING_GRAND = 4;
    static final int DOZEMODE_ENABLE = 5;
    static final int CARE_WORK = 6;

    //----------------------------------------------------------------------------------------------

    private static Notification.Builder createNotification(Context ctx, CharSequence contentText) {
        return createNotification(ctx, ctx.getString(R.string.gps_tracking), contentText);
    }

    private static Notification.Builder createNotification(Context ctx, CharSequence contentTitle, CharSequence contentText) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new Notification.Builder(ctx)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.smartup_logo_black)
                .setSound(alarmSound)
                .setAutoCancel(true);
    }

    private static Notification.Builder createNotification(Context ctx, @StringRes int contentText) {
        return createNotification(ctx, ctx.getString(contentText));
    }

    private static void showNotify(Context ctx, Notification.Builder notify, int id) {
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.notify(id, notify.build());
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    static void startMockModeNotify(Context ctx, int notifyId) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        showNotify(ctx, createNotification(ctx, R.string.mock_error_msg)
                .addAction(R.drawable.ic_settings_black_24dp, ctx.getString(R.string.admin_menu_setting),
                        PendingIntent.getActivity(ctx, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT)), notifyId);
    }
    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    static void startEnableLocationNotify(Context context, int notifyId) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        showNotify(context, createNotification(context, R.string.enable_gps_provider)
                .addAction(R.drawable.ic_settings_black_24dp, context.getString(R.string.admin_menu_setting),
                        PendingIntent.getActivity(context, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT)), notifyId);
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    static void startGrandLocationNotify(Context context, int notifyId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            showNotify(context, createNotification(context, R.string.enable_gps_grand)
                    .addAction(R.drawable.ic_settings_black_24dp, context.getString(R.string.admin_menu_setting),
                            PendingIntent.getActivity(context, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT)), notifyId);
        }
    }

    //----------------------------------------------------------------------------------------------


    @SuppressWarnings("deprecation")
    static void startDozeModeNotify(Context context, int notifyId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            showNotify(context, createNotification(context, R.string.enable_doze_mode)
                    .addAction(R.drawable.ic_settings_black_24dp, context.getString(R.string.admin_menu_setting),
                            PendingIntent.getActivity(context, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT)), notifyId);
        }
    }

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    static void startNotify(Context context, CharSequence contentText, int notifyId) {
        showNotify(context, createNotification(context, contentText), notifyId);
    }

    static void startNotify(Context context, CharSequence title, CharSequence contentText, int notifyId) {
        showNotify(context, createNotification(context, title, contentText), notifyId);
    }
}
