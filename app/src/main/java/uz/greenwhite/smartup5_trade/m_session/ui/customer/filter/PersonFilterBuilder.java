package uz.greenwhite.smartup5_trade.m_session.ui.customer.filter;

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.FilterDateRange;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.filter.GroupFilterBuilder;
import uz.greenwhite.lib.filter.GroupFilterItem;
import uz.greenwhite.lib.filter.GroupFilterRef;
import uz.greenwhite.lib.filter.GroupFilterStrategy;
import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerUtil;

public class PersonFilterBuilder {

    public final PersonFilterValue value;
    public final MyArray<Outlet> items;

    private final MyArray<OutletGroup> groups;
    private final MyArray<OutletType> types;
    private final MyArray<String> visitedPersonIds;
    private final MyArray<Region> regions;
    private final MyArray<PersonLastInfo> lastInfos;

    public PersonFilterBuilder(PersonFilterValue value,
                               MyArray<Outlet> items,
                               MyArray<OutletGroup> groups,
                               MyArray<OutletType> types,
                               MyArray<String> visitedPersonIds,
                               MyArray<Region> regions,
                               MyArray<PersonLastInfo> lastInfos) {
        this.value = value;
        this.items = items;
        this.groups = groups;
        this.types = types;
        this.visitedPersonIds = visitedPersonIds;
        this.regions = regions;
        this.lastInfos = lastInfos;

        AppError.checkNull(value);
    }

    public PersonFilter build() {
        GroupFilter<Outlet> groupFilter = buildGroupFilter();
        FilterBoolean hasDeal = buildHasDeal();
        FilterSpinner region = buildRegion();
        FilterSpinner speciality = buildSpeciality();
        FilterDateRange lastVisitDate = buildLastVisitDate();

        return new PersonFilter(
                groupFilter,
                hasDeal,
                region,
                lastVisitDate,
                speciality);
    }

    private FilterDateRange buildLastVisitDate() {
        return new FilterDateRange(DS.getString(R.string.filter_outlet_last_visit), lastInfos,
                new ValueString(15, value.lastVisitDate.from), new ValueString(15, value.lastVisitDate.to));
    }

    private FilterSpinner buildRegion() {
        Set<String> outletRegionIds = items.filter(new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                return !TextUtils.isEmpty(outlet.regionId);
            }
        }).map(new MyMapper<Outlet, String>() {
            @Override
            public String apply(Outlet outlet) {
                return outlet.regionId;
            }
        }).asSet();

        ArrayList<SpinnerOption> r = new ArrayList<>();
        for (String regionId : outletRegionIds) {
            Region region = regions.find(regionId, Region.KEY_ADAPTER);
            if (region != null) {
                r.add(new SpinnerOption(region.regionId, region.name, region));
            }
        }

        return FilterSpinner.build(R.string.region, value.regionId, MyArray.from(r));

    }

    private FilterSpinner buildSpeciality() {
        Set<String> specialityIds = items.filter(new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                return outlet.isDoctor() && !TextUtils.isEmpty(((OutletDoctor) outlet).specialityId);
            }
        }).map(new MyMapper<Outlet, String>() {
            @Override
            public String apply(Outlet outlet) {
                return ((OutletDoctor) outlet).specialityId;
            }
        }).asSet();

        ArrayList<SpinnerOption> r = new ArrayList<>();
        for (String specialityId : specialityIds) {
            OutletType specialty = types.find(specialityId, OutletType.KEY_ADAPTER);
            if (specialty != null) {
                r.add(new SpinnerOption(specialty.typeId, specialty.name, specialty));
            }
        }

        return FilterSpinner.build(R.string.speciality, value.specialityId, MyArray.from(r));
    }


    private FilterBoolean buildHasDeal() {
        if (visitedPersonIds == null) return null;
        return new FilterBoolean(DS.getString(R.string.filter_outlet_has_visit), visitedPersonIds, new ValueBoolean(value.hasDeal));
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

    public static PersonFilter build(Scope scope, PersonFilterValue value) {

        MyArray<Outlet> outlets = (MyArray<Outlet>) scope.cache.get(Scope.C_ALL_OUTLET);
        if (outlets == null) {
            outlets = DSUtil.getFilialOutlets(scope);
        }
        MyArray<String> visitedPersonIds = CustomerUtil.getVisitedOutletIds(scope);
        MyArray<OutletGroup> groups = scope.ref.getOutletGroups();
        MyArray<OutletType> types = scope.ref.getOutletTypes();
        MyArray<Region> regions = scope.ref.getRegions();
        MyArray<PersonLastInfo> lastInfos = scope.ref.getPersonLastInfo();
        return new PersonFilterBuilder(value, outlets, groups, types, visitedPersonIds, regions, lastInfos).build();
    }
}
