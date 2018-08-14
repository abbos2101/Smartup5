package uz.greenwhite.smartup5_trade.m_person_edit.ui;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_person_edit.PersonUtil;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditAccount;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditGroup;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class PersonData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final VPersonInfo info;

    private MyArray<Region> regions;
    private MyArray<PersonEditGroup> personGroups;
    private PersonEditAccount account;
    private Scope scope;

    public PersonData(String accountId, PersonInfo info) {
        this.accountId = accountId;
        this.filialId = info.filialId;
        this.scope = DS.getScope(accountId, filialId);
        this.info = PersonUtil.make(this, info);
        this.info.readyToChange();
    }

    protected PersonData(Parcel in) {
        this.accountId = in.readString();
        this.filialId = in.readString();
        this.scope = DS.getScope(accountId, filialId);
        this.personGroups = Uzum.toValue(in.readString(), PersonEditGroup.UZUM_ADAPTER.toArray());
        this.account = Uzum.toValue(in.readString(), PersonEditAccount.UZUM_ADAPTER);
        this.info = PersonUtil.make(this, Uzum.toValue(in.readString(), PersonInfo.UZUM_ADAPTER));
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.accountId);
        parcel.writeString(this.filialId);
        parcel.writeString(Uzum.toJson(getPersonGroups(), PersonEditGroup.UZUM_ADAPTER.toArray()));
        parcel.writeString(Uzum.toJson(getAccount(), PersonEditAccount.UZUM_ADAPTER));
        parcel.writeString(PersonUtil.stringify(info));
    }

    public void setRegions(MyArray<Region> regions) {
        this.regions = regions;
    }

    public MyArray<Region> getRegions() {
        return MyArray.nvl(regions);
    }


    public MyArray<PersonEditGroup> getPersonGroups() {
        return MyArray.nvl(personGroups);
    }

    public void setPersonGroups(MyArray<PersonEditGroup> personGroups) {
        this.personGroups = personGroups;
    }

    public PersonEditAccount getAccount() {
        return Util.nvl(account, PersonEditAccount.DEFAULT);
    }

    public void setAccount(PersonEditAccount account) {
        this.account = account;
    }

    public Scope getScope() {
        return scope;
    }

    public static final Creator<PersonData> CREATOR = new Creator<PersonData>() {
        @Override
        public PersonData createFromParcel(Parcel in) {
            return new PersonData(in);
        }

        @Override
        public PersonData[] newArray(int size) {
            return new PersonData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
