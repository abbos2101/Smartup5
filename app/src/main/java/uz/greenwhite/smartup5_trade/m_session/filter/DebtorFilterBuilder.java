package uz.greenwhite.smartup5_trade.m_session.filter;// 15.09.2016

import android.util.SparseBooleanArray;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
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
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.row.DebtorRow;

public class DebtorFilterBuilder {

    public final DebtorFilterValue value;
    public final MyArray<DebtorRow> sDeals;
    public final MyArray<OutletGroup> groups;
    public final MyArray<OutletType> types;
    public final MyArray<Outlet> outlets;

    public DebtorFilterBuilder(DebtorFilterValue value,
                               MyArray<DebtorRow> sDeals,
                               MyArray<OutletGroup> groups,
                               MyArray<OutletType> types) {
        AppError.checkNull(value);

        this.value = value;
        this.sDeals = sDeals;
        this.groups = groups;
        this.types = types;
        this.outlets = sDeals.map(new MyMapper<DebtorRow, Outlet>() {
            @Override
            public Outlet apply(DebtorRow debtorRow) {
                return debtorRow.outlet;
            }
        });
    }

    public DebtorFilter build() {
        ValueOption<ValueString> deliveryDate = makeDeliveryDate();
        GroupFilter<Outlet> groupFilter = buildGroupFilter();

        return new DebtorFilter(deliveryDate, groupFilter);
    }

    private ValueOption<ValueString> makeDeliveryDate() {
        String title = DS.getString(R.string.filter_outlet_debtor_payment);
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
                getGroupFilterBuilderStrategy(), outlets);
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
                final SparseBooleanArray typeIds = outlets.reduce(new SparseBooleanArray(), new MyReducer<SparseBooleanArray, Outlet>() {
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


    public static DebtorFilter build(DebtorFilterValue value,
                                      MyArray<DebtorRow> sDeals,
                                      MyArray<OutletGroup> groups,
                                      MyArray<OutletType> types) {
        return new DebtorFilterBuilder(value, sDeals, groups, types).build();
    }
}
