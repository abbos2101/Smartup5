package uz.greenwhite.smartup5_trade.m_file_manager;


import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.SharedFile;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.UserFile;

public class FileManagerApi {

    private static final String SHARED_FILES = "shared_files";
    private static final String USER_FILES = "user_files";

    public static void saveSharedFiles(Scope scope, String json) {
        scope.ds.db.valueSave(SHARED_FILES, json);
    }

    public static MyArray<SharedFile> getSharedFiles(Scope scope) {
        String s = scope.ds.db.valueLoad(SHARED_FILES);
        if (TextUtils.isEmpty(s)) {
            return MyArray.emptyArray();
        }
        return Uzum.toValue(s, SharedFile.UZUM_ADAPTER.toArray());
    }

    public static void saveUserFiles(Scope scope, String json) {
        scope.ds.db.valueSave(USER_FILES, json);
    }

    public static MyArray<UserFile> getUserFiles(Scope scope) {
        String s = scope.ds.db.valueLoad(USER_FILES);
        if (TextUtils.isEmpty(s)) {
            return MyArray.emptyArray();
        }
        return Uzum.toValue(s, UserFile.UZUM_ADAPTER.toArray());
    }
}
