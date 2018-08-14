package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomOutletIds;

public class ShippedFilter {

    public final ValueOption<ValueString> deliveryDate;
    public final GroupFilter<Outlet> groupFilter;
    public final FilterSpinner rooms;
    public final FilterSpinner regions;

    public ShippedFilter(ValueOption<ValueString> deliveryDate,
                         GroupFilter<Outlet> groupFilter,
                         FilterSpinner rooms,
                         FilterSpinner regions) {
        this.deliveryDate = deliveryDate;
        this.groupFilter = groupFilter;
        this.rooms = rooms;
        this.regions = regions;
    }

    public ShippedFilterValue toValue() {
        boolean deliveryCheck = deliveryDate.checked.getValue();
        String date = deliveryDate.valueIfChecked.getValue();

        String roomCode = "";
        String regionCode = "";

        if (rooms != null && rooms.value != null) {
            SpinnerOption value = rooms.value.getValue();
            if (value != null) {
                roomCode = value.code;
            }
        }

        if (regions != null && regions.value != null) {
            SpinnerOption value = regions.value.getValue();
            if (value != null) {
                regionCode = value.code;
            }
        }


        return new ShippedFilterValue(deliveryCheck, date,
                GroupFilter.getValue(groupFilter),
                roomCode,
                regionCode
        );
    }

    public MyPredicate<Outlet> getPredicate() {
        MyPredicate<Outlet> result = MyPredicate.True();
        result = result.and(getDeliveryDatePredicate());
        result = result.and(getGroupFilterPredicate());
        result = result.and(getOutletRoomPredicate());
        result = result.and(getOutletRegionPredicate());
        return result;
    }

    private MyPredicate<Outlet> getOutletRoomPredicate() {
        if (rooms != null) {
            final SpinnerOption value = rooms.value.getValue();
            if (rooms.isNotSelected()) {
                return MyPredicate.True();
            }
            if (!TextUtils.isEmpty(value.code)) {
                final RoomOutletIds roomOutletIds = (RoomOutletIds) value.tag;
                return new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(Outlet outlet) {
                        return roomOutletIds != null && roomOutletIds.outletIds.contains(outlet.id, MyMapper.<String>identity());
                    }
                };
            }
        }
        return null;
    }

    private MyPredicate<Outlet> getOutletRegionPredicate() {
        if (regions != null) {
            final SpinnerOption value = regions.value.getValue();
            if (regions.isNotSelected()) {
                return MyPredicate.True();
            }
            if (!TextUtils.isEmpty(value.code)) {
                return new MyPredicate<Outlet>() {
                    @Override
                    public boolean apply(Outlet outlet) {
                        return value.code.equals(outlet.regionId);
                    }
                };
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private MyPredicate<Outlet> getDeliveryDatePredicate() {
        ValueString value = deliveryDate.getValue();
        if (value == null || TextUtils.isEmpty(value.getText())) {
            return MyPredicate.True();
        }
        final int dateNumber = Integer.parseInt(DateUtil.convert(value.getText(), DateUtil.FORMAT_AS_NUMBER));
        final MyArray<SDeal> sDeals = (MyArray<SDeal>) deliveryDate.tag;

        final Set<String> deals = new HashSet<>();
        for (SDeal val : sDeals) {
            if (dateNumber >= Integer.parseInt(DateUtil.convert(val.deliveryDate, DateUtil.FORMAT_AS_NUMBER))) {
                deals.add(val.outletId);
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
