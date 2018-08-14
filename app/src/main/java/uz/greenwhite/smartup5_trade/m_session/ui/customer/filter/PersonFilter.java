package uz.greenwhite.smartup5_trade.m_session.ui.customer.filter;

import android.text.TextUtils;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.FilterDateRange;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.row.ChipRow;

public class PersonFilter {


    public final GroupFilter<Outlet> groupFilter;
    public final FilterBoolean hasDeal;
    public final FilterSpinner region;
    public final FilterDateRange lastVisitDate;
    public final FilterSpinner speciality;

    public PersonFilter(GroupFilter<Outlet> groupFilter,
                        FilterBoolean hasDeal,
                        FilterSpinner region,
                        FilterDateRange lastVisitDate,
                        FilterSpinner speciality) {
        this.groupFilter = groupFilter;
        this.hasDeal = hasDeal;
        this.region = region;
        this.lastVisitDate = lastVisitDate;
        this.speciality = speciality;
    }

    public PersonFilterValue toValue() {
        return new PersonFilterValue(
                GroupFilter.getValue(groupFilter),
                FilterBoolean.getValue(hasDeal),
                FilterSpinner.getValue(region),
                FilterDateRange.getValue(lastVisitDate),
                FilterSpinner.getValue(speciality)
        );
    }

    public MyPredicate<Outlet> getPredicate() {
        MyPredicate<Outlet> r = MyPredicate.True();

        r = r.and(getGroupFilterPredicate());
        r = r.and(getHasDeal());
        r = r.and(getRegion());
        r = r.and(getSpeciality());
        r = r.and(getLastVisitDate());

        return r;
    }

    private MyPredicate<Outlet> getLastVisitDate() {
        if (lastVisitDate == null || lastVisitDate.isEmpty()) {
            return null;
        }

        final MyArray<PersonLastInfo> outletDatas = (MyArray<PersonLastInfo>) lastVisitDate.tag;

        final int from = Util.nvl(Utils.dateStringAsInteger(lastVisitDate.from.getValue()), 0);
        final int to = Util.nvl(Utils.dateStringAsInteger(lastVisitDate.to.getValue()), Integer.MAX_VALUE);
        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                PersonLastInfo lastInfo = outletDatas.find(outlet.id, PersonLastInfo.KEY_ADAPTER);
                if (lastInfo == null || TextUtils.isEmpty(lastInfo.lastVisit)) {
                    return false;
                }
                String convert = DateUtil.convert(lastInfo.lastVisit, DateUtil.FORMAT_AS_NUMBER);
                int date = Integer.parseInt(convert);
                return from <= date && date <= to;
            }
        };
    }

    private MyPredicate<Outlet> getRegion() {
        if (region == null || region.isNotSelected()) {
            return null;
        }
        if (region.isOthers()) {
            return new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return TextUtils.isEmpty(outlet.regionId);
                }
            };
        } else {
            final String regionCode = region.value.getText();
            return new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return regionCode.equals(outlet.regionId);
                }
            };
        }
    }

    private MyPredicate<Outlet> getSpeciality() {
        if (speciality == null || speciality.isNotSelected()) {
            return null;
        }
        if (speciality.isOthers()) {
            return new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return outlet.isDoctor() && TextUtils.isEmpty(((OutletDoctor) outlet).specialityId);
                }
            };
        } else {
            final String specialityId = speciality.value.getText();
            return new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return outlet.isDoctor() && specialityId.equals(((OutletDoctor) outlet).specialityId);
                }
            };
        }
    }

    private MyPredicate<Outlet> getHasDeal() {
        if (hasDeal == null || !hasDeal.value.getValue()) {
            return null;
        }
        final MyArray<String> visitedPersonIds = (MyArray<String>) hasDeal.tag;
        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                return visitedPersonIds.contains(outlet.id, MyMapper.<String>identity());
            }
        };
    }

    private MyPredicate<Outlet> getGroupFilterPredicate() {
        if (groupFilter == null) {
            return null;
        }
        return groupFilter.getPredicate();
    }

    public MyArray<ChipRow> getFilterChip() {
        MyArray<ChipRow> result = groupFilter.groups.map(new MyMapper<FilterSpinner, ChipRow>() {
            @Override
            public ChipRow apply(final FilterSpinner filterSpinner) {
                if (!filterSpinner.isNotSelected()) {
                    SpinnerOption option = filterSpinner.value.getValue();
                    return new ChipRow(option.name, new Command() {
                        @Override
                        public void apply() {
                            filterSpinner.value.setValue(filterSpinner.value.options.get(0));
                        }
                    }, filterSpinner);
                }
                return null;
            }
        }).filterNotNull();

        return result;
    }
}
