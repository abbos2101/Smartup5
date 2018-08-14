package uz.greenwhite.smartup5_trade.m_session.ui.customer.filter;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CustomerFilterValue {

    public final PersonFilterValue person;

    public CustomerFilterValue(PersonFilterValue person) {
        this.person = Util.nvl(person, PersonFilterValue.DEFAULT);
    }

    public static final CustomerFilterValue DEFAULT = new CustomerFilterValue(null);

    public static final UzumAdapter<CustomerFilterValue> UZUM_ADAPTER = new UzumAdapter<CustomerFilterValue>() {
        @Override
        public CustomerFilterValue read(UzumReader in) {
            return new CustomerFilterValue(in.readValue(PersonFilterValue.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, CustomerFilterValue val) {
            out.write(val.person, PersonFilterValue.UZUM_ADAPTER);
        }
    };
}
