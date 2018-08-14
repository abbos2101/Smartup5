package uz.greenwhite.smartup5_trade.m_deal.ui;// 30.06.2016

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.builder.BuilderDeal;
import uz.greenwhite.smartup5_trade.m_deal.filter.DealFilter;
import uz.greenwhite.smartup5_trade.m_deal.filter.DealFilterBuilder;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;

public class DealData implements Parcelable {

    public final String accountId;
    public final String filialId;

    public final VDeal vDeal;
    public String formCode;
    public String photoTypeId;
    public final DealFilter filter;

    public DealData(Scope scope, DealHolder dealHolder) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.vDeal = BuilderDeal.make(scope, dealHolder);
        this.filter = DealFilterBuilder.parse(vDeal, null);
    }

    protected DealData(Parcel in) {
        this.accountId = in.readString();
        this.filialId = in.readString();
        this.vDeal = BuilderDeal.make(DS.getScope(accountId, filialId), Uzum.toValue(in.readString(), DealHolder.UZUM_ADAPTER));
        this.formCode = in.readString();
        this.photoTypeId = in.readString();
        this.filter = DealFilterBuilder.parse(vDeal, in.readString());
    }

    public boolean hasEdit() {
        int state = vDeal.dealRef.dealHolder.entryState.state;
        return state == EntryState.NOT_SAVED || state == EntryState.SAVED;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.accountId);
        parcel.writeString(this.filialId);
        parcel.writeString(BuilderDeal.stringify(vDeal));
        parcel.writeString(formCode);
        parcel.writeString(photoTypeId);
        parcel.writeString(DealFilterBuilder.stringify(filter));

        vDeal.reset();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<DealData> CREATOR = new Creator<DealData>() {
        @Override
        public DealData createFromParcel(Parcel in) {
            return new DealData(in);
        }

        @Override
        public DealData[] newArray(int size) {
            return new DealData[size];
        }
    };
}
