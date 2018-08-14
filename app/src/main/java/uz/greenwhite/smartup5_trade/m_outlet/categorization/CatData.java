package uz.greenwhite.smartup5_trade.m_outlet.categorization;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.builder.BuilderCategorization;
import uz.greenwhite.smartup5_trade.m_outlet.variable.VCategorization;

public class CatData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final String outletId;
    public final VCategorization vCategorization;

    public CatData(Scope scope, String outletId) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.outletId = outletId;
        this.vCategorization = BuilderCategorization.make(scope, outletId);
    }

    public CatData(Parcel parcel) {
        this(DS.getScope(parcel.readString(), parcel.readString()),
                parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountId);
        parcel.writeString(filialId);
        parcel.writeString(outletId);
        parcel.writeString(BuilderCategorization.stringify(vCategorization));
    }

    public static final Creator<CatData> CREATOR = new Creator<CatData>() {
        @Override
        public CatData createFromParcel(Parcel parcel) {
            return new CatData(parcel);
        }

        @Override
        public CatData[] newArray(int size) {
            return new CatData[size];
        }
    };

    public boolean hasEdit() {
        return vCategorization.entryState.isSaved() || vCategorization.entryState.isNotSaved();
    }
}
