package uz.greenwhite.smartup5_trade.m_deal.variable.action2;

import android.widget.CompoundButton;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.action.Condition;
import uz.greenwhite.smartup5_trade.m_session.bean.action.ConditionRule;

public class VDealActionCondition extends VariableLike {

    public final HashMap<String, CompoundButton> buttons = new HashMap<>();

    public final Condition condition;
    public final ValueArray<VDealActionConditionBonus> conditionBonus;

    private int calcBonusCount;
    private boolean canUse;

    public VDealActionCondition(Condition condition,
                                ValueArray<VDealActionConditionBonus> conditionBonus) {
        this.condition = condition;
        this.conditionBonus = conditionBonus;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public int calcBonusCount(HashMap<String, BigDecimal> products, BigDecimal total) {
        canUse = false;
        calcBonusCount = 0;
        int result = 0;

        for (ConditionRule rule : condition.rules) {
            int sumValue = 0;

            for (String productId : rule.productIds) {
                if (products.containsKey(productId)) {
                    sumValue = sumValue + products.get(productId).intValue();
                }
            }

            if (rule.productIds.isEmpty()) {
                sumValue = total.intValue();
            }

            if ("Y".equals(rule.cyclic)) {
                if (rule.fromValue == null || rule.fromValue.compareTo(BigDecimal.ZERO) == 0) {
                    return 0;
                }

                if (result == 0) {
                    result = Math.abs(sumValue / rule.fromValue.intValue());
                } else {
                    result = Math.min(Math.abs(sumValue / rule.fromValue.intValue()), result);
                }
            } else {
                if (rule.fromValue != null && rule.fromValue.intValue() > sumValue ||
                        rule.toValue != null && rule.toValue.intValue() < sumValue || sumValue == 0) {
                    return 0;
                } else {
                    result = 1;
                }
            }

            if (result <= 0) {
                return 0;
            }
        }
        calcBonusCount = result;
        canUse = calcBonusCount != 0;

        for (VDealActionConditionBonus conditionBonus : conditionBonus.getItems()) {
            for (VDealActionBonusProduct bonusProduct : conditionBonus.bonusProducts.getItems()) {
                if (BonusProduct.K_DISCOUNT.equals(bonusProduct.bonusProduct.bonusKind)) {
                    bonusProduct.quantity.setValue(bonusProduct.bonusProduct.bonusValue);
                } else {
                    BigDecimal multiply = bonusProduct.bonusProduct.bonusValue
                            .multiply(new BigDecimal(calcBonusCount));

                    BigDecimal totalQuantity = bonusProduct.bonusProduct.maxValue != null
                            ? multiply.min(bonusProduct.bonusProduct.maxValue) : multiply;

                    bonusProduct.quantity.setValue(totalQuantity);
                }
            }
        }

        return result;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return conditionBonus.getItems().toSuper();
    }

    public boolean hasValue() {
        for (VDealActionConditionBonus bonus : conditionBonus.getItems()) {
            if (bonus.isTaken.getValue()) {
                return true;
            }
        }
        return false;
    }
}
