package uz.greenwhite.smartup5_trade.common.predicate;

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
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.DoctorHospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class OutletFilterBuilder {

    public final OutletFilterValue value;
    public final MyArray<Outlet> items;

    private final MyArray<OutletGroup> groups;
    private final MyArray<OutletType> types;
    private final SparseBooleanArray outletDeal;
    private final MyArray<Region> regions;
    private final MyArray<PersonLastInfo> lastInfos;
    private final MyArray<DoctorHospital> hospitals;

    public OutletFilterBuilder(OutletFilterValue value,
                               MyArray<Outlet> items,
                               MyArray<OutletGroup> groups,
                               MyArray<OutletType> types,
                               SparseBooleanArray outletDeal,
                               MyArray<Region> regions,
                               MyArray<PersonLastInfo> lastInfos,
                               MyArray<DoctorHospital> hospitals) {
        this.value = value;
        this.items = items;
        this.groups = groups;
        this.types = types;
        this.outletDeal = outletDeal;
        this.regions = regions;
        this.lastInfos = lastInfos;
        this.hospitals = hospitals;

        AppError.checkNull(value);
    }

    public OutletFilter build() {
        GroupFilter<Outlet> groupFilter = buildGroupFilter();
        FilterBoolean hasDeal = buildHasDeal();
        FilterSpinner region = buildRegion();
        FilterSpinner speciality = buildSpeciality();
        FilterDateRange lastVisitDate = buildLastVisitDate();
        FilterSpinner legalPerson = buildLegalPerson();


        return new OutletFilter(
                groupFilter,
                hasDeal,
                region,
                lastVisitDate,
                speciality,
                legalPerson);
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

    private FilterSpinner buildLegalPerson() {
        if (hospitals != null && hospitals.nonEmpty()) {
            Set<String> lpIds = items.filter(new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return outlet.isDoctor() && !TextUtils.isEmpty(((OutletDoctor) outlet).legalPersonId);
                }
            }).map(new MyMapper<Outlet, String>() {
                @Override
                public String apply(Outlet outlet) {
                    OutletDoctor doctor = (OutletDoctor) outlet;
                    return doctor.legalPersonId;
                }
            }).asSet();

            ArrayList<SpinnerOption> r = new ArrayList<>();
            for (String lpId : lpIds) {
                DoctorHospital hospital = hospitals.find(lpId, DoctorHospital.KEY_ADAPTER);
                if (hospital != null) {
                    r.add(new SpinnerOption(hospital.id, hospital.shortName, hospital));
                }
            }
            r.add(FilterSpinner.OPTION_OTHERS.get());

            return FilterSpinner.build(R.string.lpu, value.legalPersonId, MyArray.from(r));
        } else return null;

    }


    private FilterBoolean buildHasDeal() {
        if (outletDeal == null) return null;
        return new FilterBoolean(DS.getString(R.string.filter_outlet_has_visit), outletDeal, new ValueBoolean(value.hasDeal));
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

    public static OutletFilter build(OutletFilterValue value,
                                     MyArray<Outlet> items,
                                     MyArray<OutletGroup> groups,
                                     MyArray<OutletType> types,
                                     SparseBooleanArray outletDeal,
                                     MyArray<Region> regions,
                                     MyArray<PersonLastInfo> lastInfos,
                                     MyArray<DoctorHospital> hospitals) {
        return new OutletFilterBuilder(value, items, groups, types, outletDeal, regions, lastInfos, hospitals).build();
    }
}
