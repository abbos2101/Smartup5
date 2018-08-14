package uz.greenwhite.smartup5_trade.m_debtor.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.builder.BuilderDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.variable.debtor.VDebtor;

public class DebtorData implements Parcelable {

    public final String accountId;
    public final String filialId;
    public final String outletId;
    public final String dealId;
    public final String debtorDate;
    public final boolean consign;
    public final VDebtor vDebtor;

    public DebtorData(Scope scope, ArgDebtor arg) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.outletId = arg.outletId;
        this.dealId = arg.dealId;
        this.debtorDate = arg.debtorDate;
        this.consign = arg.consign;
        this.vDebtor = BuilderDebtor.make(scope, outletId, dealId, debtorDate, consign);
    }

    protected DebtorData(Parcel in) {
        accountId = in.readString();
        filialId = in.readString();
        outletId = in.readString();
        dealId = in.readString();
        debtorDate = in.readString();
        consign = in.readInt() == 1;
        vDebtor = BuilderDebtor.make(DS.getScope(accountId, filialId), outletId, dealId, debtorDate, consign);
    }

    public boolean hasEdit() {
        int state = vDebtor.entryState.state;
        return state == EntryState.NOT_SAVED || state == EntryState.SAVED;
    }

    public boolean hasError() {
        return !TextUtils.isEmpty(vDebtor.entryState.serverResult);
    }

    public static final Creator<DebtorData> CREATOR = new Creator<DebtorData>() {
        @Override
        public DebtorData createFromParcel(Parcel in) {
            return new DebtorData(in);
        }

        @Override
        public DebtorData[] newArray(int size) {
            return new DebtorData[size];
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
        parcel.writeString(outletId);
        parcel.writeString(dealId);
        parcel.writeString(debtorDate);
        parcel.writeInt(consign ? 1 : 0);
        parcel.writeString(BuilderDebtor.stringify(this.vDebtor));
    }
}
