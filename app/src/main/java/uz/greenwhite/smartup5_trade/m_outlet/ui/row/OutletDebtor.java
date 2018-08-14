package uz.greenwhite.smartup5_trade.m_outlet.ui.row;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_session.bean.debtor.CashingRequest;

public class OutletDebtor extends OutletDealInfo {

    public final String dealId;
    public final String date;
    public final String debtorDate;
    public final String roomName;

    private Tuple2 icon;

    public final EntryState entryState;
    public final Debtor debtor;

    public final boolean consign;

    public final HashMap<Currency, BigDecimal> payments;

    private MyArray<CashingRequest> cashingRequests;

    public OutletDebtor(String dealId,
                        String date,
                        String debtorDate,
                        String roomName,
                        Debtor debtor,
                        EntryState entryState,
                        HashMap<Currency, BigDecimal> payments,
                        boolean consign) {
        this.dealId = dealId;
        this.date = date;
        this.debtorDate = debtorDate;
        this.roomName = roomName;
        this.entryState = entryState;
        this.payments = payments;
        this.debtor = debtor;
        this.consign = consign;
    }

    public OutletDebtor(Debtor debtor, EntryState entryState, HashMap<Currency, BigDecimal> payments) {
        this.dealId = debtor.localId;
        this.date = debtor.debtorDate;
        this.debtorDate = debtor.debtorDate;
        this.roomName = "";
        this.entryState = entryState;
        this.payments = payments;
        this.debtor = debtor;
        this.consign = false;
    }

    public void setCashingRequests(MyArray<CashingRequest> cashingRequests) {
        this.cashingRequests = cashingRequests;
    }

    public boolean hasCashingRequestWaiting() {
        return MyArray.nvl(cashingRequests).contains(new MyPredicate<CashingRequest>() {
            @Override
            public boolean apply(CashingRequest cashingRequest) {
                return CashingRequest.K_WAITING.equals(cashingRequest.state);
            }
        });
    }

    public boolean isPrepayment() {
        return debtor != null && TextUtils.isEmpty(debtor.dealId);
    }

    @Override
    public CharSequence getTitle() {
        String infDealId = Util.nvl(dealId);
        if (TextUtils.isEmpty(infDealId) && debtor != null && !TextUtils.isEmpty(debtor.dealId)) {
            infDealId = debtor.dealId;
        }
        int titleResId = isPrepayment() ? R.string.prepayment : R.string.deal;
        ShortHtml html = UI.html().v(DS.getString(titleResId)).v(" №").i().v(infDealId).i();
        html.i().v(" (").v(date).v(")").i();
        if (!TextUtils.isEmpty(roomName)) {
            html.br().v(DS.getString(R.string.outlet_info_room, roomName));
        }
        return html.html();
    }

    @Override
    public CharSequence getDetail() {
        ShortHtml html = UI.html();

        boolean hasError = false;
        if (!TextUtils.isEmpty(entryState.serverResult)) {
            hasError = true;
            html.br().fRed().v(entryState.serverResult).fRed();
        }

        if (!payments.isEmpty()) {
            if (hasError) html.br();
            html.v(OutletUtil.makePaymentDetail(payments));
        }

        if (hasCashingRequestWaiting()) {
            html.br().c("#FFCA00").v(DS.getString(R.string.cashing_waiting)).c();
        }
        return html.html();
    }

    @Override
    public CharSequence getError() {
        ShortHtml html = UI.html();
        if (!TextUtils.isEmpty(entryState.serverResult)) {
            html.fRed().v(entryState.serverResult).fRed();
        }
        return html.html();
    }

    @Override
    public boolean hasEdit() {
        return entryState.isReady() && debtor != null;
    }

    @Override
    public int getInfoType() {
        return DEBTOR;
    }

    @Override
    public EntryState getEntryState() {
        return entryState;
    }

    @Nullable
    @Override
    public Tuple2 getStateIcon() {
        if (icon == null) {
            icon = evalStateIconResId(entryState);
        }
        return icon;
    }
}
