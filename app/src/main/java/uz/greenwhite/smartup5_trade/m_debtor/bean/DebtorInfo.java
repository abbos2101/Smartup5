package uz.greenwhite.smartup5_trade.m_debtor.bean;

import android.text.TextUtils;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class DebtorInfo {

    public final String localId;
    public final String filialId;
    public final String outletId;
    public final String roomName;
    public final String dealId;
    public final String debtorDate;
    public final String tpName;
    public final String exName;
    public final String consignDate;
    public final String dealDeliveryDate;

    public DebtorInfo(String localId,
                      String filialId,
                      String outletId,
                      String roomName,
                      String dealId,
                      String debtorDate,
                      String tpName,
                      String exName,
                      String consignDate,
                      String dealDeliveryDate) {
        this.localId = localId;
        this.filialId = filialId;
        this.outletId = outletId;
        this.roomName = roomName;
        this.dealId = dealId;
        this.debtorDate = debtorDate;
        this.tpName = Util.nvl(tpName);
        this.exName = Util.nvl(exName);
        this.consignDate = Util.nvl(consignDate);
        this.dealDeliveryDate = Util.nvl(dealDeliveryDate);
    }

    public CharSequence getAgentName() {
        if (TextUtils.isEmpty(tpName)) {
            return DS.getString(R.string.unknown);
        }
        return tpName;
    }

    public CharSequence getExpeditorName() {
        if (TextUtils.isEmpty(exName)) {
            return DS.getString(R.string.unknown);
        }
        return exName;
    }
}
