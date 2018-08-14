package uz.greenwhite.smartup5_trade.datasource;// 17.06.2016

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.io.File;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup.anor.datasource.AnorDS;
import uz.greenwhite.smartup.anor.datasource.persist.DatabaseMate;
import uz.greenwhite.smartup.anor.datasource.persist.Pref;
import uz.greenwhite.smartup5_trade.SmartupApp;

public class DS {
    private static final ThreadLocal<Scope> scope = new ThreadLocal<>();

    public static Scope getScope(String accountId, String filialId) {
        Scope s = scope.get();
        if (s == null) {
            initScope(accountId, filialId);
            return scope.get();
        }
        if (!s.accountId.equals(accountId)) {
            throw new AppError("Scope has different accountId.");
        }
        if (!Util.nvl(s.filialId, "").equals(Util.nvl(filialId, ""))) {
            throw new AppError("Scope has different filialId.");
        }
        return s;
    }

    public static void initScope(String accountId, String filialId) {
        scope.set(new Scope(SmartupApp.getContext(), accountId, filialId));
    }

    public static void clearScope() {
        scope.set(null);
    }

    public static void clearScopeCache(String accountId) {
        Scope s = scope.get();
        if (s != null && s.ref != null && s.accountId.equals(accountId)) {
            s.ref.clear();
            s.cache.clear();
        }
    }

    public static Pref getPref() {
        return AnorDS.getPref();
    }

    public static Resources getResources() {
        return SmartupApp.getContext().getResources();
    }

    public static String getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    public static String getString(@StringRes int resId, Object... args) {
        return getResources().getString(resId, args);
    }

    @SuppressWarnings("deprecation")
    public static int getColor(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(@DrawableRes int resId) {
        return getResources().getDrawable(resId);
    }

    public static String getServerPath(String accountId) {
        File dir = new File(Environment.getExternalStorageDirectory(), "smartup5");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + "/" + accountId;
    }

    public static void clear(String accountId) {
        //TODO
        Scope scope = DS.getScope(accountId, null);
        scope.db.close();

        DatabaseMate.delete(SmartupApp.getContext(), accountId);
        File path = new File(getServerPath(accountId));
        SysUtil.deleteFolderRecursively(path);

    }
}