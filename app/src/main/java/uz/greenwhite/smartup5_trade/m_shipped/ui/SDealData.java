package uz.greenwhite.smartup5_trade.m_shipped.ui;// 30.06.2016

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;
import uz.greenwhite.smartup5_trade.m_shipped.builder.BuilderSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.filter.SDealFilter;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDeal;

public class SDealData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final String dealId;
    public final VSDeal vDeal;
    public String formCode;

    public final SDealFilter filter;

    public SDealData(Scope scope, SDealHolder holder, String dealId) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.dealId = dealId;
        this.vDeal = BuilderSDeal.make(scope, holder, dealId);
        this.filter = SDealFilter.Builder.parse(vDeal, null);
    }

    protected SDealData(Parcel in) {
        this.accountId = in.readString();
        this.filialId = in.readString();
        this.dealId = in.readString();
        this.vDeal = BuilderSDeal.make(DS.getScope(accountId, filialId),
                Uzum.toValue(in.readString(), SDealHolder.UZUM_ADAPTER), dealId);
        this.formCode = in.readString();
        this.filter = SDealFilter.Builder.parse(vDeal, in.readString());
    }

    public boolean hasEdit() {
        int state = vDeal.sDealRef.holder.entryState.state;
        return state == EntryState.NOT_SAVED || state == EntryState.SAVED;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountId);
        parcel.writeString(filialId);
        parcel.writeString(dealId);
        parcel.writeString(BuilderSDeal.stringify(vDeal));
        parcel.writeString(formCode);
        parcel.writeString(SDealFilter.Builder.stringify(filter));
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<SDealData> CREATOR = new Creator<SDealData>() {
        @Override
        public SDealData createFromParcel(Parcel in) {
            return new SDealData(in);
        }

        @Override
        public SDealData[] newArray(int size) {
            return new SDealData[size];
        }
    };
}
