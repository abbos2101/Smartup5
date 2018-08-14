package uz.greenwhite.smartup5_trade;// 15.11.2016

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.BitmapUtil;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;

public class Utils {

    //----------------------------------------------------------------------------------------------

    public static MyArray<String> intersect(MyArray<String> a, final MyArray<String> b) {
        return a.filter(new MyPredicate<String>() {
            @Override
            public boolean apply(String s) {
                return b.contains(s, MyMapper.<String>identity());
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private static File getCacheFileDir() {
        String accountId = AdminApi.loadAccountCur();
        return new File(DS.getServerPath(accountId), "pcs_images");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveBitmapToDist(String sha, Bitmap bitmap) throws Exception {
        File fileDir = getCacheFileDir();
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File path = new File(fileDir, sha + ".tmp");
        if (!path.exists()) {
            byte[] bytes = BitmapUtil.toBytes(bitmap, 100);
            FileOutputStream out = new FileOutputStream(path);
            out.write(bytes, 0, bytes.length);
            out.flush();
            out.close();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Bitmap loadBitmapToDist(String sha) throws Exception {
        File fileDir = getCacheFileDir();
        if (fileDir.exists()) {
            File path = new File(fileDir, sha + ".tmp");
            if (path.exists()) {
                FileInputStream in = new FileInputStream(path);
                byte[] image = new byte[in.available()];
                in.read(image);
                in.close();
                return BitmapUtil.toBitmap(image);
            }
        }
        return null;
    }

    //----------------------------------------------------------------------------------------------

    public static Set<String> elevateExistingFiles(Set<String> ps, File root) {
        Set<String> result = new HashSet<>();
        for (String sha : ps) {
            File file = new File(root, sha);
            if (!file.exists()) {
                result.add(sha);
            }
        }
        return result;
    }

    //----------------------------------------------------------------------------------------------

    public static Bitmap resizeByMaximum(Bitmap bitmap, int size) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) return resizeByWidth(bitmap, size);
        else return resizeByHeight(bitmap, size);
    }

    private static Bitmap resizeByWidth(Bitmap bitmap, int size) {
        if (size == 0) throw new RuntimeException("size == 0");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width < size) return bitmap;

        double resizePercent = 100 - ((size * 100) / width);
        int h = (int) Math.round(height - ((height / 100) * resizePercent));
        return Bitmap.createScaledBitmap(bitmap, size, h, true);
    }

    private static Bitmap resizeByHeight(Bitmap bitmap, int size) {
        if (size == 0) throw new RuntimeException("size == 0");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height < size) return bitmap;

        double resizePercent = 100 - ((size * 100) / height);
        int w = (int) Math.round(width - ((width / 100) * resizePercent));
        return Bitmap.createScaledBitmap(bitmap, w, size, true);
    }

    public static Integer dateStringAsInteger(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        String convert = DateUtil.convert(s, DateUtil.FORMAT_AS_NUMBER);
        return Integer.parseInt(convert);
    }

    //----------------------------------------------------------------------------------------------

    public static boolean isRole(Scope scope, Integer... roleIds) {
        final MyArray<Integer> rIds = MyArray.from(roleIds);

        return DSUtil.getFilialRoles(scope).filter(new MyPredicate<Role>() {
            @Override
            public boolean apply(Role role) {
                return rIds.contains(Integer.parseInt(role.roleId), MyMapper.<Integer>identity());
            }
        }).nonEmpty();
    }

    public static boolean isRole(Scope scope, String... pCodes) {
        final MyArray<String> pCodeIds = MyArray.from(pCodes);

        return DSUtil.getFilialRoles(scope).filter(new MyPredicate<Role>() {
            @Override
            public boolean apply(final Role role) {
                return pCodeIds.contains(new MyPredicate<String>() {
                    @Override
                    public boolean apply(String pCode) {
                        return pCode.equals(role.pCode);
                    }
                });
            }
        }).nonEmpty();
    }

    //----------------------------------------------------------------------------------------------

    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    //----------------------------------------------------------------------------------------------

    private static final ThreadLocal<DecimalFormat> MONEY_FORMAT = new ThreadLocal<DecimalFormat>() {
        protected DecimalFormat initialValue() {
            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(0);
            df.setMaximumFractionDigits(5);
            df.setGroupingUsed(true);
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            return df;
        }
    };

    public static String formatMoney(BigDecimal amount) {
        if(amount == null) {
            amount = BigDecimal.ZERO;
        }

        return (MONEY_FORMAT.get()).format(amount.setScale(5, 1));
    }

    //----------------------------------------------------------------------------------------------
    public static final MyPredicate<EntryState> HAS_ERROR_DEAL_PREDICATE = new MyPredicate<EntryState>() {
        @Override
        public boolean apply(EntryState state) {
            return !TextUtils.isEmpty(state.serverResult);
        }
    };

    public static final MyPredicate<EntryState> HAS_READY_DEAL_PREDICATE = new MyPredicate<EntryState>() {
        @Override
        public boolean apply(EntryState state) {
            return state.isReady();
        }
    };

    public static final MyPredicate<EntryState> HAS_SAVED_DEAL_PREDICATE = new MyPredicate<EntryState>() {
        @Override
        public boolean apply(EntryState state) {
            return state.isSaved();
        }
    };

    public static final MyPredicate<EntryState> HAS_LOCKED_DEAL_PREDICATE = new MyPredicate<EntryState>() {
        @Override
        public boolean apply(EntryState state) {
            return state.isLocked();
        }
    };
}
