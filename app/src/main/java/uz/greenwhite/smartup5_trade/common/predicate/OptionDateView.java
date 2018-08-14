package uz.greenwhite.smartup5_trade.common.predicate;


import android.app.Activity;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.util.Date;

import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;

public class OptionDateView {

    private final LinearLayout layout;
    private final EditText et;
    private final ValueOption<ValueString> date;

    public OptionDateView(Activity activity, ValueOption<ValueString> curDate) {
        CheckBox cb = new CheckBox(activity);
        this.et = new EditText(activity);
        this.layout = new LinearLayout(activity);
        this.date = curDate;
        this.layout.addView(cb);
        this.layout.addView(et);
        cb.setText(curDate.title);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                et.setEnabled(isChecked);
                date.checked.setValue(isChecked);
            }
        });
        cb.setChecked(date.checked.getValue());
        et.setEnabled(cb.isChecked());
        UI.makeDatePicker(et);
        UI.bind(et, date.valueIfChecked);
    }

    public LinearLayout getView() {
        return layout;
    }

    public Date getDate() {
        ValueString value = date.getValue();
        if (value == null) {
            return null;
        }
        String s = value.getText();
        if (!TextUtils.isEmpty(s)) {
            try {
                return DateUtil.FORMAT_AS_DATE.get().parse(s);
            } catch (ParseException e) {
                ErrorUtil.saveThrowable(e);
                return null;
            }
        }
        return null;
    }


}
