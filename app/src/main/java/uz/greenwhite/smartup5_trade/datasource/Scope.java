package uz.greenwhite.smartup5_trade.datasource;// 29.10.2016

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.datasource.persist.DatabaseMate;
import uz.greenwhite.smartup5_trade.BuildConfig;

public class Scope {

    public static final int C_OUTLET_ROW = 1;
    public static final int C_OUTLET_DOCTOR_ROW = 2;
    public static final int C_OUTLET_PHARM_ROW = 3;
    public static final int C_SV_PERSON_ROW = 4;

    public static final int C_ALL_OUTLET = 5;
    public static final int C_CUSTOMER_PERSON_VISIT_IDS = 6;
    public static final int C_CUSTOMER_PERSON_ROW = 7;

    public static final int C_KPI_1 = 8;
    public static final int C_KPI_2 = 9;
    public static final int C_KPI_3 = 10;

    public final Context context;
    public final String accountId;
    public final String filialId;

    public final DatabaseMate db;
    public final DataSource ds;
    public final TapeMate ref;
    public final EntryBase entry;
    public final HashMap<Integer, Object> cache;

    Scope(@NonNull Context context, @NonNull String accountId, @Nullable String filialId) {
        if (TextUtils.isEmpty(accountId)) {
            throw AppError.NullPointer();
        }
        this.context = context;
        this.accountId = accountId;
        this.filialId = filialId;

        this.db = new DatabaseMate(context, accountId);
        this.ds = new DataSource(this.db);

        if (filialId != null) {
            if (TextUtils.isEmpty(filialId)) {
                throw AppError.NullPointer();
            }
            this.ref = new TapeMate(this);
            this.entry = new EntryBase(this.db, filialId);
        } else {
            this.ref = null;
            this.entry = null;
        }

        this.cache = new HashMap<>();
    }

    public void deleteOldTapeDatabase(String name) {
        try {
            context.deleteFile(name);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            ErrorUtil.saveThrowable(e);
        }
    }
}
