package uz.greenwhite.smartup5_trade.m_session.ui.customer;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.filter.CustomerFilter;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.filter.CustomerFilterBuilder;

public class CustomerData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final CustomerFilter filter;

    public CustomerData(Scope scope) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.filter = CustomerFilterBuilder.parse(scope, null);
    }

    public CustomerData(Parcel parcel) {
        this(DS.getScope(parcel.readString(), parcel.readString()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountId);
        parcel.writeString(filialId);
    }

    public static final Creator<CustomerData> CREATOR = new Creator<CustomerData>() {
        @Override
        public CustomerData createFromParcel(Parcel parcel) {
            return new CustomerData(parcel);
        }

        @Override
        public CustomerData[] newArray(int i) {
            return new CustomerData[i];
        }
    };
}
