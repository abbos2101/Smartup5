package uz.greenwhite.smartup5_trade.m_outlet.ui.filter;// 15.09.2016

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class OShippedFilterBuilder {

    public final OShippedFilterValue value;

    public OShippedFilterBuilder(OShippedFilterValue value) {
        AppError.checkNull(value);
        this.value = value;
    }

    public OShippedFilter build() {
        return new OShippedFilter(makeDeliveryDate());
    }

    private ValueOption<ValueString> makeDeliveryDate() {
        String title = DS.getString(R.string.filter_outlet_delivery_date);
        ValueOption<ValueString> deliveryDate = new ValueOption<>(title, new ValueString(10));
        deliveryDate.checked.setValue(value.deliveryDateEnable);
        deliveryDate.valueIfChecked.setText(value.deliveryDate);
        return deliveryDate;
    }


    public static OShippedFilter build(OShippedFilterValue value) {
        return new OShippedFilterBuilder(value).build();
    }
}
