package uz.greenwhite.smartup5_trade.m_duty.bean;

import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;

public class FilialActionRow {

    public final PersonAction action;
    public final Warehouse warehouse;

    public FilialActionRow(PersonAction action, Warehouse warehouse) {
        this.action = action;
        this.warehouse = warehouse;
    }

    public String getActionKind() {
        switch (this.action.actionKind) {
            case PersonAction.K_AMOUNT:
                return DS.getString(R.string.duty_action_amount);
            case PersonAction.K_QUANT:
                return DS.getString(R.string.duty_action_quant);
            case PersonAction.K_WEIGHT:
                return DS.getString(R.string.duty_action_weight);
            default:
                return DS.getString(R.string.unknown);
        }
    }

}