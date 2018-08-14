package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import android.util.SparseBooleanArray;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.filter.GroupFilterBuilder;
import uz.greenwhite.lib.filter.GroupFilterItem;
import uz.greenwhite.lib.filter.GroupFilterRef;
import uz.greenwhite.lib.filter.GroupFilterStrategy;
import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomOutletIds;

public class ShippedFilterBuilder {

    public final ShippedFilterValue value;
    public final MyArray<SDeal> sDeals;
    public final MyArray<Outlet> items;
    public final MyArray<OutletGroup> groups;
    public final MyArray<OutletType> types;
    public final MyArray<Room> rooms;
    public final MyArray<RoomOutletIds> roomOutletIds;
    public final MyArray<Region> regions;

    public ShippedFilterBuilder(ShippedFilterValue value,
                                MyArray<SDeal> sDeals,
                                MyArray<Outlet> items,
                                MyArray<OutletGroup> groups,
                                MyArray<OutletType> types,
                                MyArray<Room> rooms,
                                MyArray<RoomOutletIds> roomOutletIds,
                                MyArray<Region> regions) {
        AppError.checkNull(value);

        this.value = value;
        this.sDeals = sDeals;
        this.items = items;
        this.groups = groups;
        this.types = types;
        this.rooms = rooms;
        this.roomOutletIds = roomOutletIds;
        this.regions = regions;
    }

    public ShippedFilter build() {
        ValueOption<ValueString> deliveryDate = makeDeliveryDate();
        GroupFilter<Outlet> groupFilter = buildGroupFilter();
        FilterSpinner rooms = makePersonRooms();
        FilterSpinner regions = makePersonRegion();

        return new ShippedFilter(deliveryDate, groupFilter, rooms, regions);
    }

    private FilterSpinner makePersonRooms() {
        MyArray<SpinnerOption> options = rooms.map(new MyMapper<Room, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Room room) {
                RoomOutletIds roomOutletIds = ShippedFilterBuilder.this.roomOutletIds.find(room.id, RoomOutletIds.KEY_ADAPTER);
                if (roomOutletIds == null) return null;
                boolean contains = roomOutletIds.outletIds.contains(new MyPredicate<String>() {
                    @Override
                    public boolean apply(String outletId) {
                        return items.contains(outletId, Outlet.KEY_ADAPTER);
                    }
                });
                if (!contains) return null;
                return new SpinnerOption(room.id, room.name, roomOutletIds);
            }
        }).filterNotNull();
        return FilterSpinner.build(DS.getString(R.string.select_room), value.roomId, options);
    }

    private FilterSpinner makePersonRegion() {
        MyArray<SpinnerOption> options = MyArray.from(items.map(new MyMapper<Outlet, String>() {
            @Override
            public String apply(Outlet outlet) {
                return outlet.regionId;
            }
        }).filterNotNull().asSet())
                .map(new MyMapper<String, SpinnerOption>() {
                    @Override
                    public SpinnerOption apply(String regionId) {
                        Region region = regions.find(regionId, Region.KEY_ADAPTER);
                        if (region == null) return null;
                        return new SpinnerOption(regionId, region.name, region);
                    }
                }).filterNotNull();
        return FilterSpinner.build(DS.getString(R.string.person_region), value.regionId, options);
    }

    private ValueOption<ValueString> makeDeliveryDate() {
        String title = DS.getString(R.string.filter_outlet_delivery_date);
        ValueOption<ValueString> deliveryDate = new ValueOption<>(
                title, new ValueString(10), sDeals);

        deliveryDate.checked.setValue(value.deliveryDateEnable);
        deliveryDate.valueIfChecked.setText(value.deliveryDate);
        return deliveryDate;
    }

    private GroupFilter<Outlet> buildGroupFilter() {
        GroupFilterValue groupFilter = value.groupFilter;
        if (groupFilter == null) {
            groupFilter = new GroupFilterValue(MyArray.<GroupFilterItem>emptyArray());
        }
        GroupFilterBuilder<Outlet> builder = new GroupFilterBuilder<>(groupFilter,
                getGroupFilterBuilderStrategy(), items);
        return builder.build();
    }

    private GroupFilterStrategy<Outlet> getGroupFilterBuilderStrategy() {
        return new GroupFilterStrategy<Outlet>() {
            @Override
            public boolean contains(Outlet item, int groupId, Integer typeId) {
                OutletGroupValue ogv = item.groupValues.find(String.valueOf(groupId), OutletGroupValue.KEY_ADAPTER);
                if (typeId != null) {
                    return ogv != null && ogv.typeId.equals(String.valueOf(typeId));
                } else {
                    return ogv == null;
                }
            }

            @Override
            public MyArray<GroupFilterRef> getGroups() {
                return groups.map(new MyMapper<OutletGroup, GroupFilterRef>() {
                    @Override
                    public GroupFilterRef apply(OutletGroup o) {
                        return new GroupFilterRef(Integer.parseInt(o.groupId), o.name);
                    }
                });
            }

            @Override
            public MyArray<SpinnerOption> makeOptions(final int groupId) {
                final SparseBooleanArray typeIds = items.reduce(new SparseBooleanArray(), new MyReducer<SparseBooleanArray, Outlet>() {
                    @Override
                    public SparseBooleanArray apply(SparseBooleanArray acc, Outlet outlet) {
                        for (OutletGroupValue v : outlet.groupValues) {
                            if (v.groupId.equals(String.valueOf(groupId))) {
                                acc.put(Integer.parseInt(v.typeId), true);
                                break;
                            }
                        }
                        return acc;
                    }
                });

                return types.filter(new MyPredicate<OutletType>() {
                    @Override
                    public boolean apply(OutletType ot) {
                        return ot.groupId.equals(String.valueOf(groupId)) &&
                                typeIds.get(Integer.parseInt(ot.typeId), false);
                    }
                }).map(new MyMapper<OutletType, SpinnerOption>() {
                    @Override
                    public SpinnerOption apply(OutletType ot) {
                        return new SpinnerOption(ot.typeId, ot.name);
                    }
                });
            }
        };
    }


    public static ShippedFilter build(ShippedFilterValue value,
                                      MyArray<SDeal> sDeals,
                                      MyArray<Outlet> items,
                                      MyArray<OutletGroup> groups,
                                      MyArray<OutletType> types,
                                      MyArray<Room> rooms,
                                      MyArray<RoomOutletIds> roomOutletIds,
                                      MyArray<Region> regions) {
        return new ShippedFilterBuilder(value, sDeals, items, groups, types, rooms, roomOutletIds, regions).build();
    }
}
