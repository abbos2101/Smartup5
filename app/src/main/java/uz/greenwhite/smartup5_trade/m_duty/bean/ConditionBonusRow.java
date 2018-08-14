package uz.greenwhite.smartup5_trade.m_duty.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.smartup5_trade.m_session.bean.action.ConditionBonus;

public class ConditionBonusRow {

    public final ConditionBonus conditionBonus;
    public final MyArray<BonusProductRow> item;

    public ConditionBonusRow(ConditionBonus conditionBonus, MyArray<BonusProductRow> item) {
        this.conditionBonus = conditionBonus;
        this.item = item;
    }
}
