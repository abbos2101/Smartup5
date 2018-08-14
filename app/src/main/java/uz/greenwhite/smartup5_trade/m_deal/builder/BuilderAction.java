package uz.greenwhite.smartup5_trade.m_deal.builder;// 07.12.2016

import java.math.BigDecimal;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealAction;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealAction;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionBonusProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionCondition;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionConditionBonus;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionModule;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.action.Condition;
import uz.greenwhite.smartup5_trade.m_session.bean.action.ConditionBonus;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;

class BuilderAction {

    private final MyArray<String> bonusId;
    private final DealRef dealRef;
    private final VisitModule module;
    private final MyArray<DealAction> initialActions;

    BuilderAction(MyArray<String> bonusId,
                  DealRef dealRef) {
        this.bonusId = bonusId;
        this.dealRef = dealRef;
        this.module = new VisitModule(VisitModule.M_ACTION, false);
        this.initialActions = getInitial();
    }

    private MyArray<DealAction> getInitial() {
        DealActionModule module = dealRef.findDealModule(this.module.id);
        return module != null ? module.actions : MyArray.<DealAction>emptyArray();
    }

    private MyArray<String> getActionIds() {
        Set<String> actIds = dealRef.filial.actionIds.asSet();
        for (DealAction action : initialActions) {
            actIds.add(action.actionId);
        }
        return MyArray.from(actIds);
    }

    private ValueArray<VDealActionBonusProduct> makeBonusProducts(final MyArray<BonusProduct> products,
                                                                  final PersonAction action,
                                                                  final ConditionBonus bonus) {
        MyArray<VDealActionBonusProduct> bonusProducts = products.map(new MyMapper<BonusProduct, VDealActionBonusProduct>() {
            @Override
            public VDealActionBonusProduct apply(BonusProduct bonusProduct) {
                String actionKey = "action_bonus_" + bonus.bonusId;

                Product product = dealRef.findProduct(bonusProduct.productId);
                if (product == null) {
                    return null;
                }

                WarehouseProductStock balance = dealRef.balance.getBalance(action.warehouseId, bonusProduct.productId);
                if (balance == null || !balance.hasBalance(Card.ANY)) {
                    return null;
                }

                BigDecimal quantity = BigDecimal.ZERO;
                String productUnitId = "";
                DealAction dealAction = initialActions.find(DealAction.getKey(action.actionId, bonus.bonusId, bonusProduct.productId), DealAction.KEY_ADAPTER);
                if (dealAction != null) {
                    quantity = dealAction.quantity;
                    productUnitId = dealAction.productUnitId;
                    balance.bookQuantity(Card.ANY, actionKey, quantity);
                }

                return new VDealActionBonusProduct(bonusProduct, product, productUnitId,
                        quantity, balance, actionKey);
            }
        }).filterNotNull();
        return new ValueArray<>(bonusProducts);
    }

    private ValueArray<VDealActionConditionBonus> makeConditionBonus(final PersonAction action,
                                                                     final Condition condition) {
        MyArray<VDealActionConditionBonus> conditionBonuses = condition.bonuses.map(new MyMapper<ConditionBonus, VDealActionConditionBonus>() {
            @Override
            public VDealActionConditionBonus apply(ConditionBonus conditionBonus) {
                ValueArray<VDealActionBonusProduct> bonusProducts = makeBonusProducts(conditionBonus.products, action, conditionBonus);
                if (bonusProducts.getItems().isEmpty()) return null;
                MyArray<VDealActionBonusProduct> items = bonusProducts.getItems();
                return new VDealActionConditionBonus(conditionBonus.bonusId, bonusProducts,
                        items.get(0).quantity.nonZero() || bonusId.contains(conditionBonus.bonusId, MyMapper.<String>identity()));
            }
        }).filterNotNull();
        return new ValueArray<>(conditionBonuses);
    }

    private ValueArray<VDealActionCondition> makeConditions(final PersonAction action) {
        MyArray<VDealActionCondition> actionConditions = action.conditions.map(new MyMapper<Condition, VDealActionCondition>() {
            @Override
            public VDealActionCondition apply(Condition condition) {
                ValueArray<VDealActionConditionBonus> conditionBonus = makeConditionBonus(action, condition);
                if (conditionBonus.getItems().isEmpty()) return null;
                return new VDealActionCondition(condition, conditionBonus);
            }
        }).filterNotNull();
        return new ValueArray<>(actionConditions);
    }

    private VDealActionForm makeForm() {
        final MyArray<PersonAction> personActions = dealRef.getPersonActions();

        MyArray<VDealAction> actions = getActionIds().map(new MyMapper<String, VDealAction>() {
            @Override
            public VDealAction apply(String actionId) {
                PersonAction action = personActions.find(actionId, PersonAction.KEY_ADAPTER);
                if (action == null) return null;
                ValueArray<VDealActionCondition> actionConditions = makeConditions(action);
                if (actionConditions.getItems().isEmpty()) return null;
                return new VDealAction(action, actionConditions);
            }
        }).filterNotNull();

        return new VDealActionForm(module, new ValueArray<>(actions));
    }


    public VDealActionModule build() {
        return new VDealActionModule(module, makeForm());
    }
}