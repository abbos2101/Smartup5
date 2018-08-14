package uz.greenwhite.smartup5_trade.m_duty.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.action.ConditionRule;

public class ConditionRuleRow {

    public final ConditionRule rule;
    public final MyArray<Product> products;

    public ConditionRuleRow(ConditionRule rule, MyArray<Product> products) {
        this.rule = rule;
        this.products = products;
    }


    public CharSequence getRuleValue() {
        ShortHtml html = UI.html().v(DS.getString(R.string.duty_action_rule_detail, rule.fromValue.toPlainString()));
        if (!"Y".equals(rule.cyclic) && rule.toValue != null) {
            html.v(DS.getString(R.string.duty_action_rule_detail_to_value, rule.toValue.toPlainString()));
        }
        return html.html();
    }
}
