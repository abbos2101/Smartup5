package uz.greenwhite.smartup5_trade.m_deal.variable.action2;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealData;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;

public class VDealAction extends VariableLike {

    public final PersonAction action;
    public final ValueArray<VDealActionCondition> conditions;

    @SuppressWarnings("FieldCanBeLocal")
    private int calcBonusCount;
    private boolean canUse;

    public VDealAction(PersonAction action,
                       ValueArray<VDealActionCondition> conditions) {
        this.action = action;
        this.conditions = conditions;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void suitableConditions(
            HashMap<String, BigDecimal> productQuantity,
            HashMap<String, BigDecimal> productAmount,
            HashMap<String, BigDecimal> productWeight
    ) {
        canUse = false;
        calcBonusCount = 0;

        BigDecimal quantityTotal = BigDecimal.ZERO;
        BigDecimal amountTotal = BigDecimal.ZERO;
        BigDecimal weightTotal = BigDecimal.ZERO;

        for (String productId : productQuantity.keySet()) {
            quantityTotal = quantityTotal.add(productQuantity.get(productId));
            amountTotal = amountTotal.add(productAmount.get(productId));
            weightTotal = weightTotal.add(productWeight.get(productId));
        }

        for (VDealActionCondition condition : conditions.getItems()) {

            switch (action.actionKind) {
                case PersonAction.K_AMOUNT:
                    calcBonusCount = condition.calcBonusCount(productAmount, amountTotal);
                    break;
                case PersonAction.K_QUANT:
                    calcBonusCount = condition.calcBonusCount(productQuantity, quantityTotal);
                    break;
                case PersonAction.K_WEIGHT:
                    calcBonusCount = condition.calcBonusCount(productWeight, weightTotal);
                    break;
            }

            if (calcBonusCount == 0) continue;
            if (!canUse) canUse = true;
        }
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return conditions.getItems().toSuper();
    }

    public boolean hasValue() {
        for (VDealActionCondition condition : conditions.getItems()) {
            if (condition.hasValue()) {
                return true;
            }
        }
        return false;
    }
}
