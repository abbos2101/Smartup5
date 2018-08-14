package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

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
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.filter.GroupFilterBuilder;
import uz.greenwhite.lib.filter.GroupFilterItem;
import uz.greenwhite.lib.filter.GroupFilterRef;
import uz.greenwhite.lib.filter.GroupFilterStrategy;
import uz.greenwhite.lib.filter.GroupFilterValue;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.Specialty;

public class TodayFilterBuilder {

    public final MyArray<OutletPlan> visits;
    public final TodayFilterValue values;

    public final MyArray<Outlet> items;
    public final MyArray<OutletGroup> groups;
    public final MyArray<OutletType> types;
    public final SparseBooleanArray deals;
    public final MyArray<Region> regions;

    public TodayFilterBuilder(TodayFilterValue values,
                              MyArray<OutletPlan> visits,
                              MyArray<Outlet> items,
                              MyArray<OutletGroup> groups,
                              MyArray<OutletType> types,
                              SparseBooleanArray deals,
                              MyArray<Region> regions) {
        AppError.checkNull(values);

        this.values = values;
        this.visits = visits;
        this.items = items;
        this.groups = groups;
        this.types = types;
        this.deals = deals;
        this.regions = regions;
    }

    public TodayFilter build() {
        ValueOption<ValueString> visitDate = makeVisitDate();
        GroupFilter<Outlet> groupFilter = buildGroupFilter();
        FilterBoolean hasDeal = buildHasDeal();
        FilterSpinner region = buildRegion();
        FilterSpinner personKind = buildPersonKind();
        FilterSpinner speciality = buildSpeciality();

        return new TodayFilter(visitDate, groupFilter, hasDeal, region, personKind, speciality);
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

        return FilterSpinner.build(R.string.region, values.regionId, MyArray.from(r));

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

        return FilterSpinner.build(R.string.speciality, values.specialityId, MyArray.from(r));
    }


    private FilterSpinner buildPersonKind() {
        Set<String> personKinds = items.map(new MyMapper<Outlet, String>() {
            @Override
            public String apply(Outlet outlet) {
                return outlet.personKind;
            }
        }).asSet();

        ArrayList<SpinnerOption> r = new ArrayList<>();
        for (String kind : personKinds) {
            String personKindName = Outlet.getPersonKindName(kind);
            if (!TextUtils.isEmpty(personKindName)) {
                r.add(new SpinnerOption(kind, personKindName));
            }
        }
        return FilterSpinner.build(R.string.person_kind_filter, values.personKind, MyArray.from(r));

    }


    private ValueOption<ValueString> makeVisitDate() {
        String title = DS.getString(R.string.filter_outlet_visit);
        ValueOption<ValueString> visitDate = new ValueOption<>(
                title, new ValueString(10), visits);

        visitDate.checked.setValue(values.visitDateEnable);
        visitDate.valueIfChecked.setText(values.visitDate);
        return visitDate;
    }

    private FilterBoolean buildHasDeal() {
        String title = DS.getString(R.string.filter_outlet_has_visit);
        return new FilterBoolean(title, deals, new ValueBoolean(values.hasDeal));
    }

    private GroupFilter<Outlet> buildGroupFilter() {
        GroupFilterValue groupFilter = values.groupFilter;
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


    public static TodayFilter build(TodayFilterValue value,
                                    MyArray<OutletPlan> visits,
                                    MyArray<Outlet> items,
                                    MyArray<OutletGroup> groups,
                                    MyArray<OutletType> types,
                                    SparseBooleanArray deals,
                                    MyArray<Region> regions) {
        return new TodayFilterBuilder(value, visits, items, groups, types, deals, regions).build();
    }
}
