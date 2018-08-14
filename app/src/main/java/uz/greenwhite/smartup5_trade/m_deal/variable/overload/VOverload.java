package uz.greenwhite.smartup5_trade.m_deal.variable.overload;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.math.BigDecimal;
import java.util.HashMap;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.Overload;

public class VOverload extends VariableLike {

    public final Overload overload;
    public final ValueArray<VOverloadRule> rules;

    private boolean canUse;

    public VOverload(Overload overload, ValueArray<VOverloadRule> rules) {
        this.overload = overload;
        this.rules = rules;
    }

    @NonNull
    public BigDecimal getTotalSum() {
        if (!isCanUse()) {
            return BigDecimal.ZERO;
        }
        return rules.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VOverloadRule>() {
            @Override
            public BigDecimal apply(BigDecimal amount, VOverloadRule val) {
                return amount.add(val.getTotalSum());
            }
        });
    }

    public boolean isCanUse() {
        return canUse;
    }

    public boolean hasProduct() {
        return rules.getItems().contains(new MyPredicate<VOverloadRule>() {
            @Override
            public boolean apply(VOverloadRule vOverloadRule) {
                return vOverloadRule.loads.getItems().contains(new MyPredicate<VOverloadLoad>() {
                    @Override
                    public boolean apply(VOverloadLoad vOverloadLoad) {
                        return vOverloadLoad.getProducts().nonEmpty();
                    }
                });
            }
        });
    }

    public void suitableConditions(
            HashMap<Pair<String, String>, HashMap<String, BigDecimal>> productQuantity,
            HashMap<Pair<String, String>, HashMap<String, BigDecimal>> productAmount,
            HashMap<Pair<String, String>, HashMap<String, BigDecimal>> productWeight
    ) {
        canUse = false;
        int calcBonusCount = 0;

        for (VOverloadRule rule : rules.getItems()) {

            switch (overload.overloadKind) {
                case Overload.K_AMOUNT:
                    for (HashMap.Entry<Pair<String, String>, HashMap<String, BigDecimal>>
                            wp : productAmount.entrySet()) {
                        Pair<String, String> key = wp.getKey();
                        calcBonusCount = rule.calcBonusCount(key.first, key.second, wp.getValue());
                    }
                    break;
                case Overload.K_QUANT:
                    for (HashMap.Entry<Pair<String, String>, HashMap<String, BigDecimal>>
                            wp : productQuantity.entrySet()) {
                        Pair<String, String> key = wp.getKey();
                        calcBonusCount = rule.calcBonusCount(key.first, key.second, wp.getValue());
                    }
                    break;
                case Overload.K_WEIGHT:
                    for (HashMap.Entry<Pair<String, String>, HashMap<String, BigDecimal>>
                            wp : productWeight.entrySet()) {
                        Pair<String, String> key = wp.getKey();
                        calcBonusCount = rule.calcBonusCount(key.first, key.second, wp.getValue());
                    }
                    break;
            }

            if (calcBonusCount == 0) continue;
            if (!canUse) canUse = true;
        }
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return rules.getItems().toSuper();
    }

    public boolean hasValue() {
        return rules.getItems().contains(new MyPredicate<VOverloadRule>() {
            @Override
            public boolean apply(VOverloadRule val) {
                return val.hasValue();
            }
        });
    }
}
