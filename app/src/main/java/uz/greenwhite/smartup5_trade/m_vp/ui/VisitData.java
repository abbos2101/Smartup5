package uz.greenwhite.smartup5_trade.m_vp.ui;// 23.09.2016

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.m_vp.ArgVisitPlan;
import uz.greenwhite.smartup5_trade.m_vp.builder.BuilderVisit;
import uz.greenwhite.smartup5_trade.m_vp.variable.VVisit;

public class VisitData implements Parcelable {

    public final ArgVisitPlan arg;
    public final VVisit vVisit;

    public VisitData(ArgVisitPlan arg) {
        this.arg = arg;
        this.vVisit = BuilderVisit.make(arg);
    }

    protected VisitData(Parcel in) {
        this.arg = Uzum.toValue(in.readString(), ArgVisitPlan.UZUM_ADAPTER);
        this.vVisit = BuilderVisit.make(arg);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Uzum.toJson(this.arg, ArgVisitPlan.UZUM_ADAPTER));
        parcel.writeString(BuilderVisit.stringify(this.vVisit));
    }

    public static final Creator<VisitData> CREATOR = new Creator<VisitData>() {
        @Override
        public VisitData createFromParcel(Parcel in) {
            return new VisitData(in);
        }

        @Override
        public VisitData[] newArray(int size) {
            return new VisitData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
