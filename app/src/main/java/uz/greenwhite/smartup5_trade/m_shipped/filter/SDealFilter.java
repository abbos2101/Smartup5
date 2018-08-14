package uz.greenwhite.smartup5_trade.m_shipped.filter;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDeal;

public class SDealFilter {

    public final MyArray<SOrderFilter> orders;

    public SDealFilter(MyArray<SOrderFilter> orders) {
        this.orders = orders;
    }

    public SDealFilterValue toValue() {
        MyArray<SOrderFilterValue> orderValues = orders.map(new MyMapper<SOrderFilter, SOrderFilterValue>() {
            @Override
            public SOrderFilterValue apply(SOrderFilter filter) {
                return filter.toValue();
            }
        });

        return new SDealFilterValue(orderValues);
    }

    public SOrderFilter findOrder(String formCode) {
        return orders.find(formCode, SOrderFilter.KEY_ADAPTER);
    }

    public static class Builder {

        private final VSDeal vsDeal;
        private final SDealFilterValue value;

        public Builder(VSDeal vsDeal, SDealFilterValue value) {
            this.vsDeal = vsDeal;
            this.value = value;
        }

        public SDealFilter build() {
            return new SDealFilter(
                    SOrderFilter.Builder.build(vsDeal, value.orders)
            );
        }

        public static SDealFilter parse(VSDeal vDeal, String source) {
            SDealFilterValue value = SDealFilterValue.DEFAULT;
            if (!TextUtils.isEmpty(source)) {
                value = Uzum.toValue(source, SDealFilterValue.UZUM_ADAPTER);
            }
            return new Builder(vDeal, value).build();
        }

        public static String stringify(SDealFilter dealFilter) {
            String json = "";
            if (dealFilter != null) {
                SDealFilterValue value = dealFilter.toValue();
                json = Uzum.toJson(value, SDealFilterValue.UZUM_ADAPTER);
            }
            return json;
        }
    }
}
