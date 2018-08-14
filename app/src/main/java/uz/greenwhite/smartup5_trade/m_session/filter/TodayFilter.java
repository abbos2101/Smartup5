package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import java.text.ParseException;
import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.filter.FilterSpinner;
import uz.greenwhite.lib.filter.GroupFilter;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ValueOption;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletDoctor;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;

public class TodayFilter {

    public final ValueOption<ValueString> visitDate;
    public final GroupFilter<Outlet> groupFilter;
    public final FilterBoolean hasDeal;
    public final FilterSpinner region;
    public final FilterSpinner personKind;
    public final FilterSpinner speciality;

    public TodayFilter(ValueOption<ValueString> visitDate,
                       GroupFilter<Outlet> groupFilter,
                       FilterBoolean hasDeal,
                       FilterSpinner region,
                       FilterSpinner personKind,
                       FilterSpinner speciality) {
        this.visitDate = visitDate;
        this.groupFilter = groupFilter;
        this.hasDeal = hasDeal;
        this.region = region;
        this.personKind = personKind;
        this.speciality = speciality;
    }

    public TodayFilterValue toValue() {
        boolean visitCheck = visitDate.checked.getValue();
        String date = visitDate.valueIfChecked.getValue();

        return new TodayFilterValue(visitCheck, date,
                GroupFilter.getValue(groupFilter),
                FilterBoolean.getValue(hasDeal),
                FilterSpinner.getValue(region),
                FilterSpinner.getValue(personKind),
                FilterSpinner.getValue(speciality)
        );
    }

    public MyPredicate<Outlet> getPredicate() {
        MyPredicate<Outlet> result = MyPredicate.True();
        result = result.and(getVisitDatePredicate());
        result = result.and(getGroupFilterPredicate());
        result = result.and(getHasDeal());
        result = result.and(getRegion());
        result = result.and(getPersonKind());
        result = result.and(getSpeciality());
        return result;
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

    private MyPredicate<Outlet> getPersonKind() {
        if (personKind == null || personKind.isNotSelected() || personKind.isOthers()) {
            return null;
        }
        final String kind = personKind.value.getText();
        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet outlet) {
                return kind.equals(outlet.personKind);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private MyPredicate<Outlet> getVisitDatePredicate() {
        final SparseBooleanArray outletDeal = (SparseBooleanArray) hasDeal.tag;
        final MyArray<OutletPlan> outletVisits = (MyArray<OutletPlan>) visitDate.tag;

        final String today = DateUtil.FORMAT_AS_NUMBER.get().format(new Date());
        final boolean visitCheck = visitDate.checked.getValue();

        MyPredicate<Outlet> DEFAULT = new MyPredicate<Outlet>() {
            @Override
            public boolean apply(final Outlet outlet) {
                return outletDeal.get(Integer.parseInt(outlet.id), false) ||
                        outletVisits.contains(new MyPredicate<OutletPlan>() {
                            @Override
                            public boolean apply(OutletPlan outletPlan) {
                                return outletPlan.outletId.equals(outlet.id) &&
                                        (!visitCheck || outletPlan.date.equals(today));
                            }
                        });
            }
        };

        ValueString value;
        String date;
        if ((value = visitDate.getValue()) == null ||
                TextUtils.isEmpty((date = value.getText()))) {
            return DEFAULT;
        }
        Date parsed;
        try {
            parsed = DateUtil.FORMAT_AS_DATE.get().parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            return DEFAULT;
        }
        final String s = DateUtil.FORMAT_AS_NUMBER.get().format(parsed);

        return new MyPredicate<Outlet>() {
            @Override
            public boolean apply(final Outlet outlet) {
                return outletVisits.contains(new MyPredicate<OutletPlan>() {
                    @Override
                    public boolean apply(OutletPlan outletPlan) {
                        return outletPlan.outletId.equals(outlet.id) &&
                                outletPlan.date.equals(s);
                    }
                });
            }
        };
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
