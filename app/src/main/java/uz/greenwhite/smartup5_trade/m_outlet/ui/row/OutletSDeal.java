package uz.greenwhite.smartup5_trade.m_outlet.ui.row;// 30.06.2016

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class OutletSDeal extends OutletDealInfo {

    public final SDealHolder holder;
    private Tuple2 icon;
    public final HashMap<Currency, BigDecimal> payments;

    public OutletSDeal(SDealHolder holder, HashMap<Currency, BigDecimal> payments) {
        this.holder = holder;
        this.payments = payments;
    }

    @Override
    public CharSequence getTitle() {
        return UI.html().v(DS.getString(R.string.deal)).v(" №").i().v(holder.deal.dealId).i().html();
    }

    @Override
    public CharSequence getDetail() {
        ShortHtml html = UI.html();
        html.i().v(DS.getString(R.string.outlet_date_of_shipment, holder.deal.deliveryDate)).i();

        if (!payments.isEmpty()) {
            html.br().v(OutletUtil.makePaymentDetail(payments));
        }
        return html.html();
    }

    @Override
    public CharSequence getError() {
        ShortHtml html = UI.html();
        if (!TextUtils.isEmpty(holder.entryState.serverResult)) {
            html.fRed().v(holder.entryState.serverResult).fRed();
        }
        return html.html();
    }

    @Override
    public boolean hasEdit() {
        return holder.entryState.isReady();
    }

    @Override
    public int getInfoType() {
        return SDEAL;
    }

    @Override
    public EntryState getEntryState() {
        return holder.entryState;
    }

    @Nullable
    @Override
    public Tuple2 getStateIcon() {
        if (icon == null) {
            icon = evalStateIconResId(holder.entryState);
        }
        return icon;
    }
}
