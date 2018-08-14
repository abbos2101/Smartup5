package uz.greenwhite.smartup5_trade.m_debtor.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_debtor.arg.ArgDebtor;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_debtor.builder.BuilderPrepayment;
import uz.greenwhite.smartup5_trade.m_debtor.variable.prepayment.VPrepayment;

public class PrepaymentData implements Parcelable {

    public final String entryId;
    public final String accountId;
    public final String filialId;
    public final String outletId;
    public final String paymentKind;
    public final VPrepayment vDebtor;

    public PrepaymentData(Scope scope, ArgDebtor arg) {
        this.accountId = scope.accountId;
        this.filialId = scope.filialId;
        this.entryId = arg.entryId;
        this.outletId = arg.outletId;
        this.paymentKind = arg.paymentKind;
        this.vDebtor = BuilderPrepayment.make(scope, filialId, outletId, arg.entryId, arg.paymentKind, null);
    }

    protected PrepaymentData(Parcel in) {
        entryId = in.readString();
        accountId = in.readString();
        filialId = in.readString();
        outletId = in.readString();
        paymentKind = in.readString();
        vDebtor = BuilderPrepayment.make(DS.getScope(accountId, filialId), filialId, outletId, entryId, paymentKind,
                Uzum.toValue(in.readString(), Debtor.UZUM_ADAPTER));
    }

    public boolean hasEdit() {
        int state = vDebtor.entryState.state;
        return state == EntryState.NOT_SAVED || state == EntryState.SAVED;
    }

    public boolean hasError() {
        return !TextUtils.isEmpty(vDebtor.entryState.serverResult);
    }

    public static final Creator<PrepaymentData> CREATOR = new Creator<PrepaymentData>() {
        @Override
        public PrepaymentData createFromParcel(Parcel in) {
            return new PrepaymentData(in);
        }

        @Override
        public PrepaymentData[] newArray(int size) {
            return new PrepaymentData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(entryId);
        parcel.writeString(accountId);
        parcel.writeString(filialId);
        parcel.writeString(outletId);
        parcel.writeString(paymentKind);
        parcel.writeString(BuilderPrepayment.stringify(this.vDebtor));
    }
}
