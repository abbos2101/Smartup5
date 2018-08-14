package uz.greenwhite.smartup5_trade.m_outlet.ui.filter;// 15.09.2016

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class DebtorFilterBuilder {

    public final DebtorFilterValue value;

    public DebtorFilterBuilder(DebtorFilterValue value) {
        AppError.checkNull(value);
        this.value = value;
    }

    public DebtorFilter build() {
        return new DebtorFilter(makeDebtorDate());
    }

    private ValueOption<ValueString> makeDebtorDate() {
        String title = DS.getString(R.string.filter_outlet_debtor_payment);
        ValueOption<ValueString> debtorDate = new ValueOption<>(title, new ValueString(10));
        debtorDate.checked.setValue(value.debtorDateEnable);
        debtorDate.valueIfChecked.setText(value.debtorDate);
        return debtorDate;
    }


    public static DebtorFilter build(DebtorFilterValue value) {
        return new DebtorFilterBuilder(value).build();
    }
}
