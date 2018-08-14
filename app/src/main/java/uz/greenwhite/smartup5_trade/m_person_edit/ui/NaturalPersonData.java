package uz.greenwhite.smartup5_trade.m_person_edit.ui;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.m_person_edit.PersonUtil;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditAccount;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditGroup;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VNaturalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VNaturalPersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class NaturalPersonData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final VNaturalPersonInfo info;

    private MyArray<Region> regions;
    private MyArray<PersonEditGroup> personGroups;

    public NaturalPersonData(String accountId, NaturalPersonInfo info) {
        this.accountId = accountId;
        this.filialId = info.filialId;
        this.info = PersonUtil.makeNaturalPerson(this, info);
        this.info.readyToChange();
    }

    protected NaturalPersonData(Parcel in) {
        this.accountId = in.readString();
        this.filialId = in.readString();
        this.personGroups = Uzum.toValue(in.readString(), PersonEditGroup.UZUM_ADAPTER.toArray());
        this.info = PersonUtil.makeNaturalPerson(this, Uzum.toValue(in.readString(), NaturalPersonInfo.UZUM_ADAPTER));
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.accountId);
        parcel.writeString(this.filialId);
        parcel.writeString(Uzum.toJson(getPersonGroups(), PersonEditGroup.UZUM_ADAPTER.toArray()));
        parcel.writeString(PersonUtil.stringifyNaturalPerson(info));
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

    public static final Creator<NaturalPersonData> CREATOR = new Creator<NaturalPersonData>() {
        @Override
        public NaturalPersonData createFromParcel(Parcel in) {
            return new NaturalPersonData(in);
        }

        @Override
        public NaturalPersonData[] newArray(int size) {
            return new NaturalPersonData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
