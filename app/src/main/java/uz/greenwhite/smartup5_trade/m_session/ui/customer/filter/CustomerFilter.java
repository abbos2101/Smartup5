package uz.greenwhite.smartup5_trade.m_session.ui.customer.filter;

public class CustomerFilter {

    public final PersonFilter personFilter;

    public CustomerFilter(PersonFilter personFilter) {
        this.personFilter = personFilter;
    }

    public CustomerFilterValue toValue() {
        return new CustomerFilterValue(personFilter.toValue());
    }
}
