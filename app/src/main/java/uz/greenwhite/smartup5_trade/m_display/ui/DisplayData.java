package uz.greenwhite.smartup5_trade.m_display.ui;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_display.DisplayApi;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayHolder;
import uz.greenwhite.smartup5_trade.m_display.builder.BuilderDisplay;
import uz.greenwhite.smartup5_trade.m_display.row.ReviewRow;
import uz.greenwhite.smartup5_trade.m_display.variable.VDisplay;

public class DisplayData implements Parcelable {

    private final String accountId;
    private final String filialId;
    public final VDisplay vDisplay;

    private DisplayData(Scope scope, DisplayHolder holder) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.vDisplay = BuilderDisplay.make(scope, holder);
        this.vDisplay.readyToChange();
    }

    public DisplayData(Scope scope, String outletId) {
        this(scope, DisplayApi.getDisplayHolder(scope, outletId));
    }


    public ReviewRow getReview(final String inventoryId, final String barcode) {
        return vDisplay.getReviews().findFirst(new MyPredicate<ReviewRow>() {
            @Override
            public boolean apply(ReviewRow val) {
                return inventoryId.equals(val.displayInventId) &&
                        barcode.equals(val.barcode);
            }
        });
    }

    public boolean hasEdit() {
        return vDisplay.holder.entryState.isNotSaved() ||
                vDisplay.holder.entryState.isSaved();
    }

    public static final Creator<DisplayData> CREATOR = new Creator<DisplayData>() {
        @Override
        public DisplayData createFromParcel(Parcel in) {
            return new DisplayData(in);
        }

        @Override
        public DisplayData[] newArray(int size) {
            return new DisplayData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountId);
        parcel.writeString(filialId);
        parcel.writeString(BuilderDisplay.stringify(vDisplay));
    }

    private DisplayData(Parcel in) {
        this.accountId = in.readString();
        this.filialId = in.readString();
        this.vDisplay = BuilderDisplay.make(DS.getScope(accountId, filialId),
                Uzum.toValue(in.readString(), DisplayHolder.UZUM_ADAPTER));
    }
}
