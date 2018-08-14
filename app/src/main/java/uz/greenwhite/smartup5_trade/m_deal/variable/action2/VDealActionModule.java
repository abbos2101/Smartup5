package uz.greenwhite.smartup5_trade.m_deal.variable.action2;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealAction;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;

public class VDealActionModule extends VDealModule {

    public final VDealActionForm form;

    public VDealActionModule(VisitModule module, VDealActionForm form) {
        super(module);
        this.form = form;
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (form == null || form.actions.getItems().isEmpty()) return MyArray.emptyArray();
        return MyArray.from(form).toSuper();
    }

    @Override
    public boolean hasValue() {
        return form != null && form.hasValue();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(form).filterNotNull().toSuper();
    }

    @Override
    public DealModule convertToDealModule() {
        ArrayList<DealAction> r = new ArrayList<>();
        for (VDealAction action : form.actions.getItems()) {
            if (!action.isCanUse()) {
                continue;
            }

            for (VDealActionCondition condition : action.conditions.getItems()) {
                if (condition.isCanUse()) {
                    VDealActionConditionBonus takenBonus = condition.conditionBonus.getItems().findFirst(new MyPredicate<VDealActionConditionBonus>() {
                        @Override
                        public boolean apply(VDealActionConditionBonus val) {
                            return val.isTaken.getValue();
                        }
                    });
                    if (takenBonus != null) {
                        for (VDealActionBonusProduct product : takenBonus.bonusProducts.getItems()) {
                            if (BonusProduct.K_QUANTITY.equals(product.bonusProduct.bonusKind)) {
                                BigDecimal quantity = product.getQuantity();
                                MyArray<CardQuantity> charges = product.balanceOfWarehouse.
                                        getCharges(product.card, product.actionKey);

                                r.add(new DealAction(
                                        action.action.actionId,
                                        action.action.warehouseId,
                                        product.product.id,
                                        quantity,
                                        charges,
                                        takenBonus.bonusId,
                                        product.productUnitId));
                            }
                        }
                    }
                }
            }
        }
        return new DealActionModule(MyArray.from(r));
    }
}
