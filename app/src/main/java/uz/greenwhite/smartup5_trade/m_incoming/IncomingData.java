package uz.greenwhite.smartup5_trade.m_incoming;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;
import uz.greenwhite.smartup5_trade.m_incoming.builder.BuilderIncoming;
import uz.greenwhite.smartup5_trade.m_incoming.filter.IncomingFilter;
import uz.greenwhite.smartup5_trade.m_incoming.filter.IncomingFilterBuilder;
import uz.greenwhite.smartup5_trade.m_incoming.filter.IncomingFilterValue;
import uz.greenwhite.smartup5_trade.m_incoming.variable.VIncoming;

public class IncomingData implements Parcelable {


    public final String accountId;
    public final String filialId;
    public final VIncoming vIncoming;
    public final IncomingFilter filter;


    public IncomingData(Scope scope, IncomingHolder holder) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.vIncoming = BuilderIncoming.make(scope, holder);
        this.filter = IncomingFilterBuilder.build(scope, vIncoming, IncomingFilterValue.makeDefault());
    }

    public IncomingData(Parcel parcel) {
        this.accountId = parcel.readString();
        this.filialId = parcel.readString();
        this.vIncoming = BuilderIncoming.make(DS.getScope(accountId, filialId), Uzum.toValue(parcel.readString(), IncomingHolder.UZUM_ADAPTER));
        this.filter = IncomingFilterBuilder.build(DS.getScope(accountId, filialId), vIncoming, Uzum.toValue(parcel.readString(), IncomingFilterValue.UZUM_ADAPTER));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountId);
        parcel.writeString(filialId);
        parcel.writeString(BuilderIncoming.stringify(vIncoming));
        parcel.writeString(Uzum.toJson(filter.toValue(), IncomingFilterValue.UZUM_ADAPTER));
    }

    public static final Creator<IncomingData> CREATOR = new Creator<IncomingData>() {
        @Override
        public IncomingData createFromParcel(Parcel parcel) {
            return new IncomingData(parcel);
        }

        @Override
        public IncomingData[] newArray(int i) {
            return new IncomingData[i];
        }
    };
}
