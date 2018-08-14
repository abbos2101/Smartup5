package uz.greenwhite.smartup5_trade.m_outlet.ui.filter;// 15.09.2016

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletDebtor;

public class DebtorFilter {

    public final ValueOption<ValueString> debtorDate;

    public DebtorFilter(ValueOption<ValueString> debtorDate) {
        this.debtorDate = debtorDate;
    }

    public DebtorFilterValue toValue() {
        boolean deliveryCheck = debtorDate.checked.getValue();
        String date = debtorDate.valueIfChecked.getValue();

        return new DebtorFilterValue(deliveryCheck, date);
    }

    public MyPredicate<OutletDebtor> getPredicate() {
        MyPredicate<OutletDebtor> result = MyPredicate.True();
        result = result.and(getDebtorDatePredicate());
        return result;
    }

    @SuppressWarnings("unchecked")
    private MyPredicate<OutletDebtor> getDebtorDatePredicate() {
        ValueString value;
        final String date;
        if ((value = debtorDate.getValue()) == null ||
                TextUtils.isEmpty((date = value.getText()))) {
            return null;
        }

        final int mDateNumber = Integer.parseInt(DateUtil.convert(date, DateUtil.FORMAT_AS_NUMBER));
        return new MyPredicate<OutletDebtor>() {
            @Override
            public boolean apply(final OutletDebtor val) {
                return Integer.parseInt(DateUtil.convert(val.date, DateUtil.FORMAT_AS_NUMBER)) <= mDateNumber;
            }
        };
    }
}
