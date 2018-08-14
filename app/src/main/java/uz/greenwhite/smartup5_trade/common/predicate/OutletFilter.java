package uz.greenwhite.smartup5_trade.common.predicate;

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.FilterDateRange;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;

public class OutletFilter {

    public final GroupFilter<Outlet> groupFilter;
    public final FilterBoolean hasDeal;
    public final FilterSpinner region;
    public final FilterDateRange lastVisitDate;
    public final FilterSpinner speciality;
    public final FilterSpinner legalPerson;                                                         //LPU

    public OutletFilter(GroupFilter<Outlet> groupFilter,
                        FilterBoolean hasDeal,
                        FilterSpinner region,
                        FilterDateRange lastVisitDate,
                        FilterSpinner speciality,
                        FilterSpinner legalPerson) {
        this.groupFilter = groupFilter;
        this.hasDeal = hasDeal;
        this.region = region;
        this.lastVisitDate = lastVisitDate;
        this.speciality = speciality;
        this.legalPerson = legalPerson;
    }

    public OutletFilterValue toValue() {
        return new OutletFilterValue(
                GroupFilter.getValue(groupFilter),
                FilterBoolean.getValue(hasDeal),
                FilterSpinner.getValue(region),
                FilterDateRange.getValue(lastVisitDate),
                FilterSpinner.getValue(speciality),
                FilterSpinner.getValue(legalPerson)
        );
    }

    public MyPredicate<Outlet> getPredicate() {
        MyPredicate<Outlet> r = MyPredicate.True();

        r = r.and(getGroupFilterPredicate());
        r = r.and(getHasDeal());
        r = r.and(getRegion());
        r = r.and(getSpeciality());
        r = r.and(getLastVisitDate());
        r = r.and(getLegalPerson());

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

    private MyPredicate<Outlet> getLegalPerson() {
        if (legalPerson == null || legalPerson.isNotSelected()) {
            return null;
        }

        if (legalPerson.isOthers()) {
            return new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return outlet.isDoctor() && TextUtils.isEmpty(((OutletDoctor) outlet).legalPersonId);
                }
            };
        } else {
            final String lpId = legalPerson.value.getText();
            return new MyPredicate<Outlet>() {
                @Override
                public boolean apply(Outlet outlet) {
                    return outlet.isDoctor() && lpId.equals(((OutletDoctor) outlet).legalPersonId);
                }
            };
        }
    }

    private MyPredicate<Outlet> getHasDeal() {
        if (hasDeal == null || !hasDeal.value.getValue()) {
            return null;
        }
        final SparseBooleanArray outletDeal = (SparseBooleanArray) hasDeal.tag;
        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                return outletDeal.get(Integer.parseInt(outlet.id), false);
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
