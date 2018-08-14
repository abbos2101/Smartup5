package uz.greenwhite.smartup5_trade.m_deal.variable.overload;

import android.support.annotation.NonNull;
import android.widget.CompoundButton;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.Overload;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadRule;

public class VOverloadRule extends VariableLike {

    public final HashMap<String, CompoundButton> buttons = new HashMap<>();

    public final Overload overload;
    public final OverloadRule rule;
    public final ValueArray<VOverloadLoad> loads;

    private boolean canUse = false;

    public VOverloadRule(Overload overload,
                         OverloadRule rule,
                         ValueArray<VOverloadLoad> loads) {
        this.overload = overload;
        this.rule = rule;
        this.loads = loads;
    }

    @NonNull
    public BigDecimal getTotalSum() {
        if (!isCanUse()){
            return BigDecimal.ZERO;
        }
        return loads.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VOverloadLoad>() {
            @Override
            public BigDecimal apply(BigDecimal amount, VOverloadLoad val) {
                return amount.add(val.getTotalSum());
            }
        });
    }

    void bookQuantity() {
        for (VOverloadLoad item : loads.getItems()) {
            item.bookQuantity();
        }
    }

    void unBookQuantity() {
        for (VOverloadLoad item : loads.getItems()) {
            item.unBookQuantity();
        }
    }


    public boolean isCanUse() {
        return canUse;
    }

    public int calcBonusCount(String warehouseId, String currencyId, HashMap<String, BigDecimal> products) {
        canUse = false;
        int sumValue;

        if (products.containsKey(overload.productId)) {
            sumValue = products.get(overload.productId).intValue();
        } else {
            return 0;
        }

        int result;
        if ("Y".equals(overload.cyclic)) {
            if (rule.fromValue == null || rule.fromValue.compareTo(BigDecimal.ZERO) == 0) {
                return 0;
            }

            result = Math.abs(sumValue / rule.fromValue.intValue());
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

        canUse = true;

        ValueBoolean firstCkeck = null;
        VOverloadLoad firstItem = null;
        for (VOverloadLoad item : this.loads.getItems()) {

            if (firstCkeck == null || (!firstCkeck.getValue() && item.isTaken.getValue())) {
                firstCkeck = item.isTaken;
                firstItem = item;
            }

            for (VOverloadProduct val : item.products) {
                val.quantity.setValue(val.loadProduct.loadValue.multiply(new BigDecimal(result)));
            }

            item.unBookQuantity();

            if (item.isTaken.getValue()) {
                item.bookQuantity();
            }

            item.setWarehouseAndPrice(warehouseId, currencyId);
        }

        if (firstCkeck != null && !firstCkeck.getValue()) {
            firstCkeck.setValue(true);
            firstItem.bookQuantity();
        }

        return result;
    }

    public boolean hasValue() {
        return loads.getItems().contains(new MyPredicate<VOverloadLoad>() {
            @Override
            public boolean apply(VOverloadLoad item) {
                return item.isTaken.getValue();
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return loads.getItems().toSuper();
    }
}
