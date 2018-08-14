package uz.greenwhite.smartup5_trade.m_session.ui.customer.row;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Date;

import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonBalanceReceivable;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;

public class PersonCustomerRow extends CustomerRow {

    public final PersonLastInfo lastInfo;
    @Nullable
    public final OutletType speciality;

    public PersonCustomerRow(Outlet outlet,
                             @Nullable PersonBalanceReceivable balanceReceivable,
                             @Nullable Boolean showPersonDebtAmount,

                             PersonLastInfo lastInfo,
                             @Nullable OutletType speciality) {
        super(outlet, balanceReceivable, showPersonDebtAmount);
        this.lastInfo = lastInfo;
        this.speciality = speciality;
    }

    @Override
    protected CharSequence getBalanceReceivable(@Nullable PersonBalanceReceivable balanceReceivable) {
        ShortHtml html = UI.html();

        if (speciality != null) {
            html.v(DS.getString(R.string.speciality)).v(": ").v(speciality.name).br();
        }

        if (balanceReceivable != null &&
                balanceReceivable.amount.compareTo(BigDecimal.ZERO) != 0) {
            if (balanceReceivable.amount.compareTo(BigDecimal.ZERO) < 0) {
                html.c("#990000");
            } else {
                html.c("#09a667");
            }

            String balanceText = "";
            if (showPersonDebtAmount == null) {
                showPersonDebtAmount = true;
            }

            if (showPersonDebtAmount) {
                balanceText = NumberUtil.formatMoney(balanceReceivable.amount);
            } else {
                if (balanceReceivable.amount.compareTo(BigDecimal.ZERO) < 0) {
                    html.v(DS.getString(R.string.person_indebted));
                } else {
                    html.v(DS.getString(R.string.person_prepayment));
                }
                return html.c().html();

            }
            return html.v(DS.getString(R.string.session_person_balance_info)).v(balanceText).c().html();
        }
        return html.html();
    }

    @Override
    protected CharSequence getInfoTitle() {
        if (this.lastInfo != null && this.lastInfo.hasLastDate()) {
            try {
                String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE);
                String lastVisit = DateUtil.convert(lastInfo.lastVisit, DateUtil.FORMAT_AS_DATE);
                if (today.equals(lastVisit)) {
                    return DS.getString(R.string.session_last_visit_inf,
                            DateUtil.convert(lastInfo.lastVisit, FORMAT_AS_TIME));
                }
                return lastVisit;
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
                ErrorUtil.saveThrowable(e);
                return "";
            }
        }
        return super.getInfoTitle();
    }
}
