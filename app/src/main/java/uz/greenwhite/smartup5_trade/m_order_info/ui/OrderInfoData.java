package uz.greenwhite.smartup5_trade.m_order_info.ui;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.m_order_info.bean.OrderInfo;

public class OrderInfoData implements Parcelable {

    public final String accountId;
    public final OrderInfo orderInfo;

    public OrderInfoData(String accountId, OrderInfo orderInfo) {
        this.accountId = accountId;
        this.orderInfo = orderInfo;
    }

    private OrderInfoData(Parcel source) {
        this(source.readString(),
                Uzum.toValue(source.readString(), OrderInfo.UZUM_ADAPTER));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountId);
        dest.writeString(Uzum.toJson(orderInfo, OrderInfo.UZUM_ADAPTER));
    }

    public static final Creator<OrderInfoData> CREATOR = new Creator<OrderInfoData>() {
        @Override
        public OrderInfoData createFromParcel(Parcel source) {
            return new OrderInfoData(source);
        }

        @Override
        public OrderInfoData[] newArray(int size) {
            return new OrderInfoData[size];
        }
    };
}
