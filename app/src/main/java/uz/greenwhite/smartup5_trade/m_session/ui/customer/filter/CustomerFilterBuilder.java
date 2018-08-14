package uz.greenwhite.smartup5_trade.m_session.ui.customer.filter;

import android.text.TextUtils;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.Scope;

public class CustomerFilterBuilder {

    public final CustomerFilterValue value;
    public final Scope scope;

    public CustomerFilterBuilder(CustomerFilterValue value, Scope scope) {
        this.value = value;
        this.scope = scope;
    }

    public CustomerFilter build() {
        return new CustomerFilter(
                PersonFilterBuilder.build(scope, value.person)
        );
    }

    public static CustomerFilter parse(Scope scope, String source) {
        CustomerFilterValue value = CustomerFilterValue.DEFAULT;
        if (!TextUtils.isEmpty(source)) {
            value = Uzum.toValue(source, CustomerFilterValue.UZUM_ADAPTER);
        }
        CustomerFilterBuilder builder = new CustomerFilterBuilder(value, scope);
        return builder.build();
    }

    public static String stringify(CustomerFilter filter) {
        String json = "";
        if (filter != null) {
            CustomerFilterValue value = filter.toValue();
            json = Uzum.toJson(value, CustomerFilterValue.UZUM_ADAPTER);
        }
        return json;
    }
}
