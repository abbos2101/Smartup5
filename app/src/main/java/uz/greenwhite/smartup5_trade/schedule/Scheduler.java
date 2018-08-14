package uz.greenwhite.smartup5_trade.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class Scheduler {

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, SchedulerReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static void start(Context context) {
        PendingIntent broadcast = getPendingIntent(context);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // just hack not to start immediately
        long triggerAtMillis = SystemClock.elapsedRealtime() + 60000;
        mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, 1000 * 60, broadcast);
    }
}
