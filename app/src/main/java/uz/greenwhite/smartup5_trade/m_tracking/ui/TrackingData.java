package uz.greenwhite.smartup5_trade.m_tracking.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.VariableUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TrackingHeader;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TrackingUser;

import static uz.greenwhite.lib.mold.MoldApi.getString;

public class TrackingData implements Parcelable {

    public final ValueString date;
    public final ValueSpinner agent, mapType;
    public final ValueBoolean userTracking, outletLocation;

    public final MyArray<TrackingUser> users;

    private TrackingHeader tracking;

    public TrackingData(String agentId,
                        String date,
                        MyArray<TrackingUser> users,
                        boolean userTracking,
                        boolean outletLocation) {
        this.users = users;
        this.agent = getAgentSpinner(agentId, users);
        this.mapType = getMapType();
        this.date = new ValueString(10, date);
        this.userTracking = new ValueBoolean(userTracking);
        this.outletLocation = new ValueBoolean(outletLocation);

        VariableUtil.readyToChange(this.date, agent, mapType, this.userTracking, this.outletLocation);
    }

    public TrackingData(String agentId, String date, MyArray<TrackingUser> tRows) {
        this(agentId, date, tRows, false, true);
    }

    private TrackingData(Parcel source) {
        this(source.readString(),
                source.readString(),
                Uzum.toValue(source.readString(), TrackingUser.UZUM_ADAPTER.toArray()),
                "1".equals(source.readString()),
                "1".equals(source.readString()));
    }

    //----------------------------------------------------------------------------------------------

    private static ValueSpinner getAgentSpinner(String agentId, MyArray<TrackingUser> users) {
        MyArray<SpinnerOption> map = users.map(new MyMapper<TrackingUser, SpinnerOption>() {
            @Override
            public SpinnerOption apply(TrackingUser r) {
                String roles = getString(R.string.tracking_role, r.role);
                return new SpinnerOption(r.id, UI.html().v(r.name).v(" | ").br().v(roles).html());
            }
        });
        SpinnerOption spinnerOption = map.find(agentId, SpinnerOption.KEY_ADAPTER);
        return new ValueSpinner(map, spinnerOption);
    }

    public static ValueSpinner getMapType() {
        MyArray<SpinnerOption> mapType = MyArray.from(
                new SpinnerOption(GoogleMap.MAP_TYPE_NORMAL, DS.getString(R.string.default_type)),
                new SpinnerOption(GoogleMap.MAP_TYPE_SATELLITE, DS.getString(R.string.satellite_type)),
                new SpinnerOption(GoogleMap.MAP_TYPE_TERRAIN, DS.getString(R.string.terrain_type)),
                new SpinnerOption(GoogleMap.MAP_TYPE_HYBRID, DS.getString(R.string.hybrid_type))
        );
        return new ValueSpinner(mapType);
    }

    //----------------------------------------------------------------------------------------------

    @Nullable
    public TrackingHeader getTracking() {
        return tracking;
    }

    public void setTracking(TrackingHeader tracking) {
        this.tracking = tracking;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(agent.getText());
        dest.writeString(date.getText());
        dest.writeString(Uzum.toJson(users, TrackingUser.UZUM_ADAPTER.toArray()));
        dest.writeString((userTracking.getValue() ? "1" : "0"));
        dest.writeString((outletLocation.getValue() ? "1" : "0"));
    }

    public static final Creator<TrackingData> CREATOR = new Creator<TrackingData>() {
        @Override
        public TrackingData createFromParcel(Parcel source) {
            return new TrackingData(source);
        }

        @Override
        public TrackingData[] newArray(int size) {
            return new TrackingData[size];
        }
    };
}
