package uz.greenwhite.smartup5_trade.m_deal.variable.action2;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.Quantity;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;

public class VDealActionBonusProduct extends VariableLike implements Quantity {

    public final BonusProduct bonusProduct;
    public final Product product;
    public final String productUnitId;

    public final ValueBigDecimal quantity;

    public final WarehouseProductStock balanceOfWarehouse;
    public final String actionKey;
    public final Card card = Card.ANY;

    public VDealActionBonusProduct(BonusProduct bonusProduct,
                                   Product product,
                                   String productUnitId,
                                   BigDecimal quantity,
                                   WarehouseProductStock balanceOfWarehouse,
                                   String actionKey) {
        this.bonusProduct = bonusProduct;
        this.product = product;
        this.productUnitId = productUnitId;
        this.balanceOfWarehouse = balanceOfWarehouse;
        this.actionKey = actionKey;

        this.quantity = new ValueBigDecimal(20, 9);
        this.quantity.setValue(quantity);
    }

    void bookQuantity() {
        balanceOfWarehouse.bookQuantity(card, actionKey, getQuantity());
    }

    void unBookQuantity() {
        balanceOfWarehouse.bookQuantity(card, actionKey, BigDecimal.ZERO);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }


    @Override
    public BigDecimal getQuantity() {
        if (BonusProduct.K_DISCOUNT.equals(bonusProduct.bonusKind)){
            return this.quantity.getQuantity();
        }
        BigDecimal balance = balanceOfWarehouse.getAvailBalance(card, actionKey);
        if (balance.signum() == -1 || balance.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal quantity = this.quantity.getQuantity();

        BigDecimal result = balance.subtract(quantity);
        if (result.compareTo(BigDecimal.ZERO) >= 0) {
            return quantity;
        } else {
            return balance;
        }
    }
}
