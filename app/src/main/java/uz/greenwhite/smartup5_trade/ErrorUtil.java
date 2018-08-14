package uz.greenwhite.smartup5_trade;// 20.06.2016

import uz.greenwhite.lib.error.SystemError;
import uz.greenwhite.lib.error.UserError;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.smartup.anor.common.OnTryCatchCallback;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.common.Error;
import uz.greenwhite.smartup5_trade.datasource.RT;

public class ErrorUtil {

    public static Error getErrorMessage(String message) {
        return getErrorMessage(new Exception(message));
    }

    public static Error getErrorMessage(Throwable error) {
        if (error == null) return Error.DEFAULT;
        String message = error.getMessage();

        if (CharSequenceUtil.containsIgnoreCase(message, "archiver error") ||
                CharSequenceUtil.containsIgnoreCase(message, "resolve host") ||
                CharSequenceUtil.containsIgnoreCase(message, "connection timed out") ||
                CharSequenceUtil.containsIgnoreCase(message, "ssl=") ||
                CharSequenceUtil.containsIgnoreCase(message, "handshake aborted") ||
                CharSequenceUtil.containsIgnoreCase(message, "error during system call") ||
                CharSequenceUtil.containsIgnoreCase(message, "failed to connect") ||
                CharSequenceUtil.containsIgnoreCase(message, "unexpected end of stream") ||
                CharSequenceUtil.containsIgnoreCase(message, "reset by peer")) {
            return Error.makeInstance(R.string.cant_connect_to_server, error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, "route to host") ||
                CharSequenceUtil.containsIgnoreCase(message, "Network is unreachable")) {
            return Error.makeInstance(R.string.no_internet, error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, RT.UNAUTHENTICATED)) {
            return Error.makeInstance(R.string.unauthenticated, error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, "entryId is locked") ||
                CharSequenceUtil.containsIgnoreCase(message, "entry is locked") ||
                CharSequenceUtil.containsIgnoreCase(message, "is locked")) {
            return Error.makeInstance(R.string.entry_is_locked, error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, "Currency.currencyId")) {
            return Error.makeInstance("Валюта не найдено, возможно удалён", error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, "is not draft")) {
            return Error.makeInstance("Вы не можете сохранить завершенную сделку", error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, "to change locale") ||
                CharSequenceUtil.containsIgnoreCase(message, "Failed to change locale for db")) {
            return Error.makeInstance("Ошибка при подключении в базу данных", error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, "sync is blocked")) {
            return Error.makeInstance("Синхронизации заблокирован администратором", error);

        } else if (CharSequenceUtil.containsIgnoreCase(message, "Query:") ||
                CharSequenceUtil.containsIgnoreCase(message, "NULL pointer") ||
                CharSequenceUtil.containsIgnoreCase(message, "ArgSession.getScope")) {
            return Error.makeInstance("Ошибка при чтение данных", error);
        } else if (CharSequenceUtil.containsIgnoreCase(message, "mf_files dup val on index")) {
            return Error.makeInstance(R.string.f_m_duplicate_name_error, error);
        } else if (CharSequenceUtil.containsIgnoreCase(message, "FileNotFoundException")) {
            return Error.makeInstance(R.string.f_m_file_not_found_error, error);
        } else if (CharSequenceUtil.containsIgnoreCase(message, "No Activity found to handle Intent")) {
            return Error.makeInstance(R.string.f_m_application_not_found_error, error);
        }
        return Error.makeInstance(error);
    }

    public static void saveThrowable(Throwable ex, final String accountId, boolean uncaught) {
        if (!uncaught && (ex instanceof UserError || ex instanceof SystemError)) {
            return;
        }
    }

    public static void saveThrowable(Throwable ex, boolean uncaught) {
        saveThrowable(ex, AdminApi.loadAccountCur(), uncaught);
    }

    public static void saveThrowable(Throwable ex) {
        saveThrowable(ex, false);
    }

    public static void saveThrowable(Throwable ex, String accountId) {
        saveThrowable(ex, accountId, false);
    }

    public static boolean tryCatch(OnTryCatchCallback callback) {
        try {
            callback.onTry();
            return true;
        } catch (Exception e) {
            saveThrowable(e);
            try {
                callback.onCatch(e);
            } catch (Exception ex) {
                saveThrowable(ex);
            }
            return false;
        }
    }
}
