package uz.greenwhite.smartup5_trade.common.dialog;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.view.ContextThemeWrapper;

import java.util.Calendar;

public class MyDatePickerDialog {

    public static DatePickerDialog show(Context ctx, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        Context context = ctx;
        if (isBrokenSamsungDevice()) {
            context = new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog);
        }
        return new DatePickerDialog(context, onDateSetListener, year, month, day);
    }

    private static boolean isBrokenSamsungDevice() {
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP_MR1));
    }

    private static boolean isBetweenAndroidVersions(int min, int max) {
        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }
}
