package uz.greenwhite.smartup5_trade.m_deal.variable.action2;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;

public class VDealActionConditionBonus extends VariableLike {

    public final String bonusId;
    public final ValueArray<VDealActionBonusProduct> bonusProducts;
    public final ValueBoolean isTaken;

    public VDealActionConditionBonus(String bonusId,
                                     ValueArray<VDealActionBonusProduct> bonusProducts,
                                     boolean isTaken) {
        this.bonusId = bonusId;
        this.bonusProducts = bonusProducts;
        this.isTaken = new ValueBoolean(isTaken);
    }

    public void unBookQuantity() {
        for (VDealActionBonusProduct val : bonusProducts.getItems()) {
            val.unBookQuantity();
        }
    }


    @Override
    protected MyArray<Variable> gatherVariables() {
        return bonusProducts.getItems().toSuper();
    }
}
