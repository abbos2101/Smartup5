package uz.greenwhite.smartup5_trade.common;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.widget.MultiSelectionSpinner;


public class MyMultiSelectionSpinner extends MultiSelectionSpinner {

    public MyMultiSelectionSpinner(Context context) {
        super(context);
    }

    public MyMultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
        AppError.checkNull(command);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        super.onClick(dialog, which, isChecked);
        if (command != null) command.apply();
    }
}
