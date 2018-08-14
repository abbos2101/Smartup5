package uz.greenwhite.smartup5_trade.m_deal.variable.action2;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.ui.DealData;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;

public class VDealActionForm extends VDealForm {

    public final ValueArray<VDealAction> actions;

    public VDealActionForm(VisitModule module, ValueArray<VDealAction> actions) {
        super(module);
        this.actions = actions;
    }

    @Override
    public boolean hasValue() {
        for (VDealAction action : actions.getItems()) {
            if (action.hasValue()) {
                return true;
            }
        }
        return false;
    }

    public void applyAction(DealData dealData) {
        VDealOrderModule orderModule = (VDealOrderModule) dealData.vDeal.modules.getItems()
                .find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);

        HashMap<String, BigDecimal> productDiscount = new HashMap<>();
        HashMap<String, String> productBonus = new HashMap<>();

        for (VDealAction action : actions.getItems()) {
            if (!action.isCanUse()) continue;

            for (VDealActionCondition condition : action.conditions.getItems()) {

                for (VDealActionConditionBonus val : condition.conditionBonus.getItems()) {
                    val.unBookQuantity();
                }

                for (VDealActionConditionBonus bonus : condition.conditionBonus.getItems()) {
                    if (bonus.isTaken.getValue()) {
                        for (VDealActionBonusProduct bonusProduct : bonus.bonusProducts.getItems()) {
                            if (BonusProduct.K_QUANTITY.equals(bonusProduct.bonusProduct.bonusKind)) {
                                bonusProduct.bookQuantity();

                            } else if (BonusProduct.K_DISCOUNT.equals(bonusProduct.bonusProduct.bonusKind)) {
                                productDiscount.put(bonusProduct.bonusProduct.productId, bonusProduct.getQuantity());
                                productBonus.put(bonusProduct.bonusProduct.productId, bonus.bonusId);
                            }
                        }
                    }
                }
            }
        }

        for (VForm form : orderModule.getModuleForms()) {
            VDealOrderForm orderForm = (VDealOrderForm) form;
            for (VDealOrder order : orderForm.orders.getItems()) {
                if (productDiscount.containsKey(order.product.id)) {
                    order.margin.setValue(productDiscount.get(order.product.id).multiply(new BigDecimal("-1")));
                    order.bonusId.setValue(productBonus.get(order.product.id));
                } else {
                    order.bonusId.setValue("");
                    if (order.marginOption == null) {
                        order.margin.setValue(BigDecimal.ZERO);
                    } else {
                        order.margin.setValue((BigDecimal) order.marginOption.tag);
                    }
                }
            }
        }
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return actions.getItems().toSuper();
    }
}
