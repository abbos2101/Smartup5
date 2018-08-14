package uz.greenwhite.smartup5_trade.m_outlet.ui.filter;// 15.09.2016

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.m_outlet.ui.row.OutletSDeal;

public class OShippedFilter {

    public final ValueOption<ValueString> deliveryDate;

    public OShippedFilter(ValueOption<ValueString> deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OShippedFilterValue toValue() {
        boolean deliveryCheck = deliveryDate.checked.getValue();
        String date = deliveryDate.valueIfChecked.getValue();

        return new OShippedFilterValue(deliveryCheck, date);
    }

    public MyPredicate<OutletSDeal> getPredicate() {
        MyPredicate<OutletSDeal> result = MyPredicate.True();
        result = result.and(getDeliveryDatePredicate());
        return result;
    }

    @SuppressWarnings("unchecked")
    private MyPredicate<OutletSDeal> getDeliveryDatePredicate() {
        ValueString value;
        final String date;
        if ((value = deliveryDate.getValue()) == null ||
                TextUtils.isEmpty((date = value.getText()))) {
            return null;
        }

        final int mDateNumber = Integer.parseInt(DateUtil.convert(date, DateUtil.FORMAT_AS_NUMBER));
        return new MyPredicate<OutletSDeal>() {
            @Override
            public boolean apply(final OutletSDeal sdeal) {
                int sDateNumber = Integer.parseInt(DateUtil.convert(sdeal.holder.deal.deliveryDate, DateUtil.FORMAT_AS_NUMBER));
                return sDateNumber <= mDateNumber;
            }
        };
    }
}
