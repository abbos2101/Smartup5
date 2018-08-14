package uz.greenwhite.smartup5_trade;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.location.FusedLocationProviderApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;

public class RootUtil {

    public static boolean isDeviceRooted(@NonNull Setting setting) {
        return !setting.common.workWithRoot && isDeviceRooted();
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess"})
    public static boolean isDeviceRooted(@NonNull Scope scope) {
        if (scope != null && scope.ref != null) {
            Setting setting = scope.ref.getSettingWithDefault();
            return isDeviceRooted(setting);
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isDeviceRooted(@NonNull Activity activity, @NonNull Scope scope) {
        if (isDeviceRooted(scope)) {
            UI.alertError(activity, activity.getString(R.string.root_error_msg));
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------

    public static boolean isMockLocationMode(@NonNull Activity activity, @NonNull Location location) {
        try {
            if (isMockLocationMode(location)) {
                UI.alertError(activity, activity.getString(R.string.mock_error_msg));
                return true;
            }
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean isMockLocationMode(@NonNull Location location) {
        boolean mockLocation = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (location.isFromMockProvider()) {
                    mockLocation = true;
                }
            }
            if (!mockLocation) {
                Bundle extras = location.getExtras();
                if (extras != null && extras.getBoolean(FusedLocationProviderApi.KEY_MOCK_LOCATION, false)) {
                    mockLocation = true;
                }
            }
            return mockLocation;
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------

    private static boolean isDeviceRooted() {
        try {
            boolean deviceRooted = checkRootMethod3() || checkRootMethod2();
            if (deviceRooted) {
                ErrorUtil.saveThrowable(new AppError("device is rooted and open SuperUser"));
            }
            return deviceRooted;
            //return /*checkRootMethod1() ||*/  checkRootMethod3() || checkRootMethod2();
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            return false;
        }
    }

//    private static boolean checkRootMethod1() {
//        String buildTags = android.os.Build.TAGS;
//        return buildTags != null && buildTags.contains("test-keys");
//    }

    private static boolean checkRootMethod2() {
        String[] paths = {/*"/system/app/Superuser.apk", "/system/app/SuperSu",*/
                "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su",
                "/system/sd/xbin/su", "/system/bin/failsafe/su",
                "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = in.readLine();
            if (!TextUtils.isEmpty(result)) ErrorUtil.saveThrowable(new Exception(result));
            return result != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}
