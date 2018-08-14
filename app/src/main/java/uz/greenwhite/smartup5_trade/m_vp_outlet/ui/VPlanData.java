package uz.greenwhite.smartup5_trade.m_vp_outlet.ui;// 12.12.2016

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.m_vp_outlet.bean.OutletVisitPlan;
import uz.greenwhite.smartup5_trade.m_vp_outlet.variable.VPlan;
import uz.greenwhite.smartup5_trade.m_vp_outlet.variable.VPlanDay;

public class VPlanData implements Parcelable {

    public final VPlan vPlan;

    public VPlanData(OutletVisitPlan visitPlan) {
        this.vPlan = makeVisitPlan(visitPlan);
    }

    protected VPlanData(Parcel in) {
        this(Uzum.toValue(in.readString(), OutletVisitPlan.UZUM_ADAPTER));
    }

    private VPlan makeVisitPlan(OutletVisitPlan visitPlan) {
        VPlan vPlan = new VPlan(visitPlan);
        vPlan.startDate.setValue(visitPlan.firstWeekDate);
        String[] weeks = visitPlan.weekPlan.split(";");
        for (int i = 0; i < weeks.length; i++) {
            String val = weeks[i];
            switch (i) {
                case 0:
                    initDays(vPlan.week1.days, val);
                    break;
                case 1:
                    initDays(vPlan.week2.days, val);
                    break;
                case 2:
                    initDays(vPlan.week3.days, val);
                    break;
                case 3:
                    initDays(vPlan.week4.days, val);
                    break;
            }
        }
        initDays(vPlan.month.days, visitPlan.mothPlan);
        return vPlan;
    }

    private void initDays(MyArray<VPlanDay> days, String val) {
        for (String i : val.split(",")) {
            if (!TextUtils.isEmpty(i)) {
                int position = Integer.parseInt(i);
                days.get(--position).checked.setValue(true);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        OutletVisitPlan visitPlan = vPlan.toValue();
        parcel.writeString(Uzum.toJson(visitPlan, OutletVisitPlan.UZUM_ADAPTER));
    }

    public static final Creator<VPlanData> CREATOR = new Creator<VPlanData>() {
        @Override
        public VPlanData createFromParcel(Parcel in) {
            return new VPlanData(in);
        }

        @Override
        public VPlanData[] newArray(int size) {
            return new VPlanData[size];
        }
    };
}
