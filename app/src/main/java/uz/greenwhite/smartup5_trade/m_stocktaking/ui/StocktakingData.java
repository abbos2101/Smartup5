package uz.greenwhite.smartup5_trade.m_stocktaking.ui;

import android.os.Parcel;
import android.os.Parcelable;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHolder;
import uz.greenwhite.smartup5_trade.m_stocktaking.builder.BuilderStocktaking;
import uz.greenwhite.smartup5_trade.m_stocktaking.filter.StocktakingFilter;
import uz.greenwhite.smartup5_trade.m_stocktaking.filter.StocktakingFilterBuilder;
import uz.greenwhite.smartup5_trade.m_stocktaking.filter.StocktakingFilterValue;
import uz.greenwhite.smartup5_trade.m_stocktaking.variable.VStocktaking;

public class StocktakingData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final VStocktaking vStocktaking;
    public final StocktakingFilter filter;

    public StocktakingData(Scope scope, StocktakingHolder holder) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.vStocktaking = BuilderStocktaking.make(scope, holder);
        this.filter = StocktakingFilterBuilder.build(scope, vStocktaking, StocktakingFilterValue.makeDefault());
    }

    public StocktakingData(Parcel parcel) {
        this.accountId = parcel.readString();
        this.filialId = parcel.readString();
        this.vStocktaking = BuilderStocktaking.make(DS.getScope(accountId, filialId), Uzum.toValue(parcel.readString(), StocktakingHolder.UZUM_ADAPTER));
        this.filter = StocktakingFilterBuilder.build(DS.getScope(accountId, filialId), vStocktaking, Uzum.toValue(parcel.readString(), StocktakingFilterValue.UZUM_ADAPTER));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountId);
        parcel.writeString(filialId);
        parcel.writeString(BuilderStocktaking.stringify(vStocktaking));
        parcel.writeString(Uzum.toJson(filter.toValue(), StocktakingFilterValue.UZUM_ADAPTER));
    }

    public static final Creator<StocktakingData> CREATOR = new Creator<StocktakingData>() {
        @Override
        public StocktakingData createFromParcel(Parcel parcel) {
            return new StocktakingData(parcel);
        }

        @Override
        public StocktakingData[] newArray(int i) {
            return new StocktakingData[i];
        }
    };
}
