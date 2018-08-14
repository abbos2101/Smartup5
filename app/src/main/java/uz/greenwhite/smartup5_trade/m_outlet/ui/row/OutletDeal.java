package uz.greenwhite.smartup5_trade.m_outlet.ui.row;// 30.06.2016

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;

public class OutletDeal extends OutletDealInfo {

    public static Drawable EDIT_DRAWABLE = UI.changeDrawableColor(SmartupApp.getContext(),
            R.drawable.ic_edit_black_24dp, R.color.colorAccent);

    public final DealHolder holder;
    public Tuple2 icon;
    public final HashMap<Currency, BigDecimal> payments;

    public OutletDeal(DealHolder holder, HashMap<Currency, BigDecimal> payments) {
        this.holder = holder;
        this.payments = payments;
        this.icon = getStateIcon();
    }

    @Override
    public int getInfoType() {
        return DEAL;
    }

    @Override
    public CharSequence getTitle() {
        return UI.html().v(holder.deal.getDealName()).v(" №").i().v(holder.deal.dealLocalId).i().html();
    }

    @Override
    public CharSequence getDetail() {
        ShortHtml html = UI.html().i().v(holder.deal.header.begunOn).v(" - ").v(holder.deal.header.endedOn).i();
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
