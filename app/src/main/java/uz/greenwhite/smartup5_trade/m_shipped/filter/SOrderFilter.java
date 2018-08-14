package uz.greenwhite.smartup5_trade.m_shipped.filter;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.filter.FilterBoolean;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.module.VModule;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrder;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderForm;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderModule;

public class SOrderFilter {

    public final String formCode;
    public final FilterBoolean hasValue;

    public SOrderFilter(String formCode, FilterBoolean hasValue) {
        this.formCode = formCode;
        this.hasValue = hasValue;
    }

    public SOrderFilterValue toValue() {
        return new SOrderFilterValue(formCode, hasValue.value.getValue());
    }

    public MyPredicate<VSDealOrder> getPredicate() {
        MyPredicate<VSDealOrder> result = MyPredicate.True();
        result = result.and(getHasValuePredicate());
        return result;
    }

    private MyPredicate<VSDealOrder> getHasValuePredicate() {
        if (!hasValue.value.getValue()) return null;
        return new MyPredicate<VSDealOrder>() {
            @Override
            public boolean apply(VSDealOrder vsDealOrder) {
                return vsDealOrder.hasValue();
            }
        };
    }

    public static MyMapper<SOrderFilter, String> KEY_ADAPTER = new MyMapper<SOrderFilter, String>() {
        @Override
        public String apply(SOrderFilter val) {
            return val.formCode;
        }
    };

    static class Builder {

        public final SOrderFilterValue value;

        public Builder(SOrderFilterValue value) {
            this.value = value;
        }

        public SOrderFilter build() {
            FilterBoolean hasValue = new FilterBoolean(DS.getString(R.string.deal_filter_has_value), new ValueBoolean(value.hasValue));
            return new SOrderFilter(value.formCode, hasValue);
        }

        public static MyArray<SOrderFilter> build(final VSDeal vDeal, final MyArray<SOrderFilterValue> values) {
            MyArray<VModule> modules = vDeal.modules.getItems().toSuper();
            VSDealOrderModule module = (VSDealOrderModule) modules.find(VisitModule.M_ORDER, VSDealOrderModule.KEY_ADAPTER);
            if (module == null) {
                return MyArray.emptyArray();
            }
            MyArray<VSDealOrderForm> forms = module.forms.getItems();
            return forms.map(new MyMapper<VSDealOrderForm, SOrderFilter>() {
                @Override
                public SOrderFilter apply(VSDealOrderForm form) {
                    SOrderFilterValue value = values.find(form.code, SOrderFilterValue.KEY_ADAPTER);
                    if (value == null) {
                        value = SOrderFilterValue.makeDefault(form.code);
                    }
                    return new Builder(value).build();
                }
            });
        }

    }
}
