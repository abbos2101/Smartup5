package uz.greenwhite.smartup5_trade.schedule;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uz.greenwhite.lib.Tuple4;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.bean.user.User;
import uz.greenwhite.smartup.anor.bean.user.UserFilial;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.SettingCommon;

class ScheduleUtil {

    //----------------------------------------------------------------------------------------------

    private static Map<String, MyArray<SettingCommon>> settings = new HashMap<>();
    private static Map<String, Long> lastSyncTime = new HashMap<>();

    private static MyArray<SettingCommon> getAccountSettings(String accountId) {
        DS.initScope(accountId, Filial.FILIAL_HEAD);
        Scope scope = DS.getScope(accountId, Filial.FILIAL_HEAD);
        if (!scope.ds.hasDatabaseName()) return MyArray.emptyArray();

        User user = scope.ref.getUser();
        ArrayList<SettingCommon> settings = new ArrayList<>();
        if (user != null && user.filials.nonEmpty()) {
            for (UserFilial filial : user.filials) {
                DS.initScope(accountId, filial.filialId);
                scope = DS.getScope(accountId, filial.filialId);
                Setting setting = scope.ref.getSettingWithDefault();
                SettingCommon common = setting.common;
                scope.ref.clear();
                settings.add(common);
            }
        }
        return MyArray.from(settings);
    }

    // Tuple(boolean, boolean, Integer) -> gpsEnable, syncEnable, syncInterval
    static synchronized Map<String, Tuple4> isWorking() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int time = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);

        c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis() + ((30 * 60) * 1000));
        int timeLeave = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);

        MyArray<Account> accounts = AdminApi.getAccounts();
        Map<String, Tuple4> result = new HashMap<>();
        for (Account account : accounts) {
            String lastSyncScheduleMillis = AdminApi.getLastSyncScheduleMillis(account.accountId);
            if (TextUtils.isEmpty(lastSyncScheduleMillis)) continue;

            long syncTimeMillis = Long.parseLong(lastSyncScheduleMillis);
            long lastTakeInDatabase = Util.nvl(lastSyncTime.get(account.accountId), 0L);
            lastSyncTime.put(account.accountId, syncTimeMillis);

            MyArray<SettingCommon> settingCommons = MyArray.nvl(settings.get(account.accountId));

            if (settingCommons.isEmpty() || syncTimeMillis != lastTakeInDatabase) {
                settingCommons = getAccountSettings(account.accountId);
                settings.put(account.accountId, settingCommons);
            }


            for (SettingCommon common : settingCommons) {
                if (common.workTimeBegin < time && time < common.workTimeEnd &&
                        (common.gpsEnable || common.syncEnable)) {
                    boolean careWork = timeLeave > common.workTimeEnd;

                    Tuple4 tuple = result.get(account.accountId);
                    if (tuple == null) {
                        tuple = new Tuple4(common.gpsEnable, common.syncEnable, common.syncInterval, careWork);
                    } else {
                        Boolean oGpsEnable = (Boolean) tuple.first;
                        Boolean oSyncEnable = (Boolean) tuple.second;
                        Integer oSyncInterval = (Integer) tuple.third;
                        Boolean oCareWork = (Boolean) tuple.fourth;
                        tuple = new Tuple4(oGpsEnable || common.gpsEnable,
                                oSyncEnable || common.syncEnable,
                                Math.min(oSyncInterval, common.syncInterval),
                                careWork || oCareWork);
                    }
                    result.put(account.accountId, tuple);
                }
            }
        }

        return result;
    }

    //----------------------------------------------------------------------------------------------

    static boolean isServiceRunning(Context ctx, String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);

        final List<ActivityManager.RunningServiceInfo> services = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo rsi : services) {
            if (rsi.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
}
