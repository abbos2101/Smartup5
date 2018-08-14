package uz.greenwhite.smartup5_trade.common.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.format.DateFormat;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import uz.greenwhite.lib.mold.MoldDialogFragment;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;

public class NumberDateDialog extends MoldDialogFragment {

    public static void show(FragmentActivity activity, OnNumberSelect onNumberSelect) {
        NumberDateDialog d = new NumberDateDialog();
        d.onNumberSelect = onNumberSelect;
        d.show(activity.getSupportFragmentManager(), "number_piker_dialog");
    }

    @Nullable
    private OnNumberSelect onNumberSelect;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewSetup vs = new ViewSetup(getActivity(), R.layout.z_number_date);
        final NumberPicker numberPicker = vs.id(R.id.np_number);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(30);

        final TextView date = vs.textView(R.id.tv_date);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker np, int i, int i1) {
                date.setText(getDateToString(np.getValue()));

            }
        });

        if (onNumberSelect != null) {
            int value = onNumberSelect.getValue();
            date.setText(getDateToString(value));
            numberPicker.setValue(value);
        } else {
            date.setText(getDateToString(1));
            numberPicker.setValue(1);
        }

        return new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.Dialog))
                .setView(vs.view)
                .setPositiveButton(getString(R.string.choose), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onNumberSelect != null)
                            onNumberSelect.onSelectNumber(numberPicker.getValue());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create();
    }

    private String getDateToString(int value) {
        long dayOfMilis = value * 86400000L;
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + dayOfMilis);
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            CharSequence week = DateFormat.format("EEEE", date);
            CharSequence month = DateFormat.format("MMMM", date);
            String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            return String.format("%s-%s, %s", day, month, week);
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            e.printStackTrace();
        }
        return DateUtil.format(date, DateUtil.FORMAT_AS_DATE);
    }

    public interface OnNumberSelect {
        int getValue();

        void onSelectNumber(int number);
    }
}
