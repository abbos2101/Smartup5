package uz.greenwhite.smartup5_trade.common;

import android.support.annotation.StringRes;

import uz.greenwhite.smartup5_trade.datasource.DS;

public class Error {

    public static Error makeInstance(Throwable error) {
        String message = error.getMessage();
        return new Error(message, message);
    }

    public static Error makeInstance(CharSequence message, Throwable error) {
        return new Error(message, error.getMessage());
    }

    public static Error makeInstance(@StringRes int resId, Throwable error) {
        return new Error(DS.getString(resId), error.getMessage());
    }

    public final CharSequence message;
    public final String exceptionMessage;

    public Error(CharSequence message, String exceptionMessage) {
        this.message = message;
        this.exceptionMessage = exceptionMessage;
    }

    public static final Error DEFAULT = new Error("", "");
}
