package uz.greenwhite.smartup5_trade.m_duty.bean;

import uz.greenwhite.lib.collection.MyArray;

public class ActionConditionRow {


    public final String title;
    public final MyArray<ConditionRuleRow> rules;
    public final MyArray<ConditionBonusRow> bonuces;

    public ActionConditionRow(String title, MyArray<ConditionRuleRow> rules, MyArray<ConditionBonusRow> bonuces) {
        this.title = title;
        this.rules = rules;
        this.bonuces = bonuces;
    }
}
