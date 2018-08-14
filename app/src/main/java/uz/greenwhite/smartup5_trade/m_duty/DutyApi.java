package uz.greenwhite.smartup5_trade.m_duty;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_duty.bean.ActionConditionRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.BonusProductRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.ConditionBonusRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.ConditionRuleRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.FilialActionRow;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.action.Condition;
import uz.greenwhite.smartup5_trade.m_session.bean.action.ConditionBonus;
import uz.greenwhite.smartup5_trade.m_session.bean.action.ConditionRule;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;

public class DutyApi {

    public static MyArray<FilialActionRow> getPersonActions(final Scope scope) {
        Filial filial = scope.ref.getFilial(scope.filialId);
        final MyArray<PersonAction> personActions = scope.ref.getPersonActions();
        return filial.actionIds.map(new MyMapper<String, FilialActionRow>() {
            @Override
            public FilialActionRow apply(String actionId) {
                PersonAction personAction = personActions.find(actionId, PersonAction.KEY_ADAPTER);
                if (personAction == null) {
                    return null;
                }
                Warehouse warehouse = scope.ref.getWarehouse(personAction.warehouseId);
                if (warehouse == null) {
                    return null;
                }
                return new FilialActionRow(personAction, warehouse);
            }
        }).filterNotNull();
    }

    //----------------------------------------------------------------------------------------------

    private static MyArray<ConditionRuleRow> getConditionRule(final Scope scope, Condition condition) {
        return condition.rules.map(new MyMapper<ConditionRule, ConditionRuleRow>() {
            @Override
            public ConditionRuleRow apply(ConditionRule conditionRule) {
                MyArray<Product> products = conditionRule.productIds.map(new MyMapper<String, Product>() {
                    @Override
                    public Product apply(String productId) {
                        return scope.ref.getProduct(productId);
                    }
                }).filterNotNull();

                if (products.isEmpty()) {
                    return null;
                }
                return new ConditionRuleRow(conditionRule, products);
            }
        }).filterNotNull();
    }


    private static MyArray<BonusProductRow> getBonusProduct(final Scope scope, ConditionBonus bonus) {
        return bonus.products.map(new MyMapper<BonusProduct, BonusProductRow>() {
            @Override
            public BonusProductRow apply(BonusProduct bonusProduct) {
                Product product = scope.ref.getProduct(bonusProduct.productId);
                if (product == null) {
                    return null;
                }
                return new BonusProductRow(bonusProduct, product);
            }
        });
    }

    private static MyArray<ConditionBonusRow> getConditionBonus(final Scope scope, Condition condition) {
        return condition.bonuses.map(new MyMapper<ConditionBonus, ConditionBonusRow>() {
            @Override
            public ConditionBonusRow apply(ConditionBonus conditionBonus) {
                return new ConditionBonusRow(conditionBonus,
                        getBonusProduct(scope, conditionBonus));
            }
        });
    }

    public static MyArray<ActionConditionRow> getActionConditionRows(final Scope scope, FilialActionRow action) {
        final Setter<Integer> conditionSetter = new Setter<>();
        conditionSetter.value = 1;
        return action.action.conditions.map(new MyMapper<Condition, ActionConditionRow>() {
            @Override
            public ActionConditionRow apply(Condition condition) {
                MyArray<ConditionRuleRow> conditionRule = getConditionRule(scope, condition);
                MyArray<ConditionBonusRow> conditionBonus = getConditionBonus(scope, condition);

                if (conditionRule.isEmpty() || conditionBonus.isEmpty()) {
                    return null;
                }

                String title = DS.getString(R.string.duty_action_condition_title, String.valueOf(conditionSetter.value++));
                return new ActionConditionRow(title, conditionRule, conditionBonus);
            }
        }).filterNotNull();
    }
}
