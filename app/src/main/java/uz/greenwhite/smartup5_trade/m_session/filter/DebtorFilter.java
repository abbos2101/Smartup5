package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.row.DebtorRow;

public class DebtorFilter {

    public final ValueOption<ValueString> deliveryDate;
    public final GroupFilter<Outlet> groupFilter;

    public DebtorFilter(ValueOption<ValueString> deliveryDate,
                        GroupFilter<Outlet> groupFilter) {
        this.deliveryDate = deliveryDate;
        this.groupFilter = groupFilter;
    }

    public DebtorFilterValue toValue() {
        boolean deliveryCheck = deliveryDate.checked.getValue();
        String date = deliveryDate.valueIfChecked.getValue();

        return new DebtorFilterValue(deliveryCheck, date,
                GroupFilter.getValue(groupFilter)
        );
    }

    public MyPredicate<Outlet> getPredicate() {
        MyPredicate<Outlet> result = MyPredicate.True();
        result = result.and(getDeliveryDatePredicate());
        result = result.and(getGroupFilterPredicate());
        return result;
    }

    @SuppressWarnings("unchecked")
    private MyPredicate<Outlet> getDeliveryDatePredicate() {
        ValueString value = deliveryDate.getValue();
        if (value == null || TextUtils.isEmpty(value.getText())) {
            return MyPredicate.True();
        }
        final int dateNumber = Integer.parseInt(DateUtil.convert(value.getText(), DateUtil.FORMAT_AS_NUMBER));
        final MyArray<DebtorRow> debtors = (MyArray<DebtorRow>) deliveryDate.tag;

        final Set<String> deals = new HashSet<>();
        for (final DebtorRow val : debtors) {
            boolean contains = val.debtorDates.contains(new MyPredicate<String>() {
                @Override
                public boolean apply(String s) {
                    return dateNumber >= Integer.parseInt(DateUtil.convert(s, DateUtil.FORMAT_AS_NUMBER));
                }
            });
            if (contains) {
                deals.add(val.outlet.id);
            }
        }
        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(final Outlet outlet) {
                return deals.contains(outlet.id);
            }
        };
    }

    private MyPredicate<Outlet> getGroupFilterPredicate() {
        if (groupFilter == null) {
            return null;
        }
        return groupFilter.getPredicate();
    }
}
