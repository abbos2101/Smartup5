package uz.greenwhite.smartup5_trade.m_movement.ui;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingHolder;
import uz.greenwhite.smartup5_trade.m_movement.variable.VMovementIncoming;

public class MovementData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final VMovementIncoming vMovementIncoming;


    public MovementData(Scope scope, MovementIncomingHolder holder) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.vMovementIncoming = VMovementIncoming.build(scope, holder);
    }

    public MovementData(Parcel parcel) {
        this.accountId = parcel.readString();
        this.filialId = parcel.readString();
        this.vMovementIncoming = VMovementIncoming.build(DS.getScope(accountId, filialId),
                Uzum.toValue(parcel.readString(), MovementIncomingHolder.UZUM_ADAPTER));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountId);
        parcel.writeString(filialId);
        parcel.writeString(Uzum.toJson(VMovementIncoming.toValue(vMovementIncoming), MovementIncomingHolder.UZUM_ADAPTER));
    }

    public static final Creator<MovementData> CREATOR = new Creator<MovementData>() {
        @Override
        public MovementData createFromParcel(Parcel parcel) {
            return new MovementData(parcel);
        }

        @Override
        public MovementData[] newArray(int i) {
            return new MovementData[i];
        }
    };
}
