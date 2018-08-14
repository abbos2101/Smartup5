package uz.greenwhite.smartup5_trade.common.calculator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;

public abstract class CalcProductDialog<T extends Variable> extends CalcDialog {

    @Nullable
    protected abstract ValueBigDecimal getEnableValue();

    protected T oldValue;
    protected T newValue;

    @Override
    protected void onKeyListener(@NonNull CalcKey key) {
        ValueBigDecimal val = getEnableValue();
        if (val == null || val.isZero() && key.isZero()) return;

        String vText = val.getText();

        switch (key) {
            case KEY_1:
            case KEY_2:
            case KEY_3:
            case KEY_4:
            case KEY_5:
            case KEY_6:
            case KEY_7:
            case KEY_8:
            case KEY_9:
                val.setText((TextUtils.isEmpty(vText) || "0".equals(vText) ? "" : vText) + key.param);
                break;

            case KEY_0:
            case KEY_00:
                val.setText(vText + key.param);
                break;

            case KEY_DOT:
                if (!TextUtils.isEmpty(vText) && CharSequenceUtil.containsIgnoreCase(vText, "."))
                    return;
                else val.setText(val.getQuantity().toPlainString() + key.param);
                break;

            case KEY_REMOVE:
                if (val.nonZero()) {
                    if (vText.length() == 1) val.setText("0");
                    else val.setText(vText.substring(0, vText.length() - 1));

                }
                break;
            case KEY_PLUS_ONE:
                val.setValue(val.getQuantity().add(BigDecimal.ONE));
                break;
            case KEY_SUBTRACT_ONE:
                if (val.nonZero()) val.setValue(val.getQuantity().subtract(BigDecimal.ONE));
                break;
        }
    }
}
