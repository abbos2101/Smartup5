package uz.greenwhite.smartup5_trade.m_deal.builder;// 30.06.2016

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyFlatMapper;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.Quantity;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealHeader;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealAction;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionCondition;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionConditionBonus;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.attach.VAttachModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.returns.VDealReturnModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.rpayment.VDealRPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.total.VDealTotalForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.total.VDealTotalModule;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletContract;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialAgent;
import uz.greenwhite.smartup5_trade.m_session.bean.FilialExpeditor;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomExpeditor;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Ban;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Violation;

public class BuilderDeal {

    public static String stringify(VDeal vDeal) {
        DealHolder old = vDeal.dealRef.dealHolder;
        Deal deal = vDeal.convertToDeal(vDeal.dealRef.dealHolder.deal.dealNew,
                vDeal.dealRef.dealHolder.deal.dealState);
        DealHolder dealHolder = new DealHolder(deal, old.entryState);
        return Uzum.toJson(dealHolder, DealHolder.UZUM_ADAPTER);
    }

    public static VDeal make(Scope scope, DealHolder dealHolder) {
        DealRef dealRef = new DealRef(scope, dealHolder);
        VDealHeader header = new VDealHeader(dealHolder.deal.header,
                makeContractNumber(dealRef), makeExpeditor(dealRef), makeAgent(dealRef));
        ValueArray<VDealModule> modules = makeModules(dealRef);
        return new VDeal(dealRef, header, modules);
    }

    @Nullable
    private static ValueSpinner makeContractNumber(final DealRef dealRef) {
        MyArray<OutletContract> contracts = dealRef.getOutletContracts();

        if (contracts.nonEmpty()) {
            SpinnerOption empty = new SpinnerOption("", DS.getString(R.string.not_selected));
            MyArray<SpinnerOption> options = contracts.map(new MyMapper<OutletContract, SpinnerOption>() {
                @Override
                public SpinnerOption apply(OutletContract c) {
                    return new SpinnerOption(c.contractId, c.contractNumber, c);
                }
            }).prepend(empty);

            SpinnerOption value = null;

            String cid = dealRef.dealHolder.deal.header.contractNumberId;
            if (cid != null) {
                value = options.find(cid, SpinnerOption.KEY_ADAPTER);
            }
            return new ValueSpinner(options, Util.nvl(value, empty));
        }
        return null;
    }

    @Nullable
    private static ValueSpinner makeExpeditor(DealRef dealRef) {
        TradeRoleKeys tradeRoleKeys = dealRef.getRoleKeys();
        if (dealRef.isRole(tradeRoleKeys.expeditor)) {
            return new ValueSpinner(MyArray.from(new SpinnerOption(dealRef.createdBy, DS.getString(R.string.deal_attach_agent_is_are_you))));
        }

        if (!dealRef.setting.deal.selectExpeditor) {
            return null;
        }

        if (dealRef.dealHolder.deal.is(Deal.DEAL_ORDER) ||
                dealRef.dealHolder.deal.is(Deal.DEAL_EXTRAORDINARY) ||
                dealRef.dealHolder.deal.is(Deal.DEAL_PHARMCY) ||
                dealRef.dealHolder.deal.is(Deal.DEAL_DOCTOR)) {

            MyArray<RoomExpeditor> expeditor = dealRef.getExpeditor();
            if (expeditor.nonEmpty()) {
                SpinnerOption empty = new SpinnerOption("", DS.getString(R.string.not_selected));
                MyArray<SpinnerOption> options = expeditor.map(new MyMapper<RoomExpeditor, SpinnerOption>() {
                    @Override
                    public SpinnerOption apply(RoomExpeditor val) {
                        return new SpinnerOption(val.userId, val.name);
                    }
                }).prepend(empty);

                SpinnerOption value = null;

                String cid = dealRef.dealHolder.deal.header.expeditorId;
                if (!TextUtils.isEmpty(cid)) {
                    value = options.find(cid, SpinnerOption.KEY_ADAPTER);
                }
                return new ValueSpinner(options, Util.nvl(value, empty));
            }
        } else {
            MyArray<FilialExpeditor> expeditor = dealRef.getFilialExpeditors();
            if (expeditor.nonEmpty()) {
                SpinnerOption empty = new SpinnerOption("", DS.getString(R.string.not_selected));
                MyArray<SpinnerOption> options = expeditor.map(new MyMapper<FilialExpeditor, SpinnerOption>() {
                    @Override
                    public SpinnerOption apply(FilialExpeditor val) {
                        return new SpinnerOption(val.userId, val.name);
                    }
                }).prepend(empty);

                SpinnerOption value = null;

                String cid = dealRef.dealHolder.deal.header.expeditorId;
                if (!TextUtils.isEmpty(cid)) {
                    value = options.find(cid, SpinnerOption.KEY_ADAPTER);
                }
                return new ValueSpinner(options, Util.nvl(value, empty));
            }
        }
        return null;
    }

    @Nullable
    private static ValueSpinner makeAgent(DealRef dealRef) {
        TradeRoleKeys tradeRoleKeys = dealRef.getRoleKeys();
        if (!dealRef.isRole(tradeRoleKeys.expeditor) || !dealRef.dealHolder.deal.is(Deal.DEAL_RETURN)) {
            return null;
        }
        if (dealRef.isRole(tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser)) {
            return new ValueSpinner(MyArray.from(new SpinnerOption(dealRef.createdBy, DS.getString(R.string.deal_attach_agent_is_are_you))));
        }

        MyArray<FilialAgent> agents = dealRef.getFilialAgents();
        if (agents.nonEmpty()) {
            SpinnerOption empty = new SpinnerOption("", DS.getString(R.string.not_selected));

            MyArray<SpinnerOption> options = agents.map(new MyMapper<FilialAgent, SpinnerOption>() {
                @Override
                public SpinnerOption apply(FilialAgent val) {
                    return new SpinnerOption(val.userId, val.name, val);
                }
            }).prepend(empty);

            SpinnerOption value = null;

            String cid = dealRef.dealHolder.deal.header.agentId;
            if (!TextUtils.isEmpty(cid)) {
                value = options.find(cid, SpinnerOption.KEY_ADAPTER);
            }
            return new ValueSpinner(options, Util.nvl(value, empty));
        }
        return null;
    }

    private static MyArray<VDealModule> makeDealReturn(DealRef dealRef) {
        VDealReturnModule returnModule = new BuilderReturn(dealRef).build();
        VDealRPaymentModule paymentModule = new BuilderRPayment(dealRef).build();
        VAttachModule attachModule = new VAttachModule();

        paymentModule.orderModule = returnModule;

        return MyArray.from(returnModule, paymentModule, attachModule);
    }

    private static MyArray<VDealModule> makeDealExtraordinary(DealRef dealRef) {
        boolean hasBanOrder = false;
        boolean hasBanGift = false;

        for (Violation item : dealRef.allViolations) {
            for (Ban ban : item.bans) {
                if (!hasBanGift && Ban.K_GIFT.equals(ban.kind)) {
                    hasBanGift = true;
                }

                if (!hasBanOrder && Ban.K_DEAL.equals(ban.kind)) {
                    hasBanOrder = true;
                }
            }
        }

        MyArray<VisitModule> moduleIds = getModuleIds(dealRef);
        ArrayList<VDealModule> r = new ArrayList<>();

        if (!hasBanGift && !TextUtils.isEmpty(dealRef.dealHolder.deal.finalDealId) ||
                moduleIds.contains(VisitModule.M_GIFT, VisitModule.KEY_ADAPTER)) {
            r.add(new BuilderGift(dealRef, new VisitModule(VisitModule.M_GIFT, false)).build());
        }

        VDealOrderModule vDealOrderModule = null;
        if (!hasBanOrder) {
            vDealOrderModule = new BuilderOrder(dealRef, new VisitModule(VisitModule.M_ORDER, false)).build();
            r.add(vDealOrderModule);
        }
        if (vDealOrderModule != null && !TextUtils.isEmpty(dealRef.dealHolder.deal.finalDealId)) {
            HashSet<String> bonusIds = vDealOrderModule.getOrderBonusIds();
            r.add(new BuilderAction(MyArray.from(bonusIds), dealRef).build());
        }
        if (vDealOrderModule != null) {
            r.add(new BuildOverload(dealRef).build());
        }
        if (moduleIds.contains(VisitModule.M_NOTE, VisitModule.KEY_ADAPTER)) {
            r.add(new BuilderNote(dealRef, new VisitModule(VisitModule.M_NOTE, false)).build());
        }
        r.add(new BuilderService(dealRef, new VisitModule(VisitModule.M_SERVICE, false)).build());
        r.add(new BuilderPayment(dealRef, true).build());
        r.add(new VAttachModule());
        return MyArray.from(r);
    }

    private static MyArray<VDealModule> makeDealOrder(DealRef dealRef) {

        MyArray<VisitModule> moduleIds = getModuleIds(dealRef);


        boolean hasBanOrder = false;
        boolean hasBanGift = false;
        boolean hasBanAction = false;

        for (Violation item : dealRef.allViolations) {
            for (Ban ban : item.bans) {
                if (!hasBanGift && Ban.K_GIFT.equals(ban.kind)) {
                    hasBanGift = true;
                }

                if (!hasBanOrder && Ban.K_DEAL.equals(ban.kind)) {
                    hasBanOrder = true;
                }

                if (!hasBanAction && Ban.K_ACTION.equals(ban.kind)) {
                    hasBanAction = true;
                }
            }
        }

        if (hasBanOrder) {
            moduleIds = moduleIds.filter(new MyPredicate<VisitModule>() {
                @Override
                public boolean apply(VisitModule visitModule) {
                    return VisitModule.M_ORDER != visitModule.id;
                }
            });
        }

        if (hasBanGift) {
            moduleIds = moduleIds.filter(new MyPredicate<VisitModule>() {
                @Override
                public boolean apply(VisitModule visitModule) {
                    return VisitModule.M_GIFT != visitModule.id;
                }
            });
        }

        VDealOrderModule orderModule = null;
        VDealActionModule actionModule = null;
        VOverloadModule overload = null;
        VDealGiftModule gift = null;

        ArrayList<VDealModule> r = new ArrayList<>();
        for (VisitModule module : moduleIds) {
            switch (module.id) {

                case VisitModule.M_GIFT:
                    r.add(gift = new BuilderGift(dealRef, module).build());
                    break;

                case VisitModule.M_ORDER:
                    VDealOrderModule order = new BuilderOrder(dealRef, module).build();
                    orderModule = order;
                    r.add(order);

                    if (order != null && order.getModuleForms().nonEmpty()) {
                        if (!hasBanAction) {
                            HashSet<String> bonusIds = order.getOrderBonusIds();
                            VDealActionModule action = new BuilderAction(MyArray.from(bonusIds), dealRef).build();
                            actionModule = action;
                            r.add(action);
                        }

                        overload = new BuildOverload(dealRef).build();
                        r.add(overload);

                        r.add(new VAttachModule());
                    }
                    break;

                case VisitModule.M_PHOTO:
                    r.add(new BuilderPhoto(dealRef, module).build());
                    break;

                case VisitModule.M_STOCK:
                    r.add(new BuilderStock(dealRef, module).build());
                    break;

                case VisitModule.M_MEMO:
                    r.add(new BuilderMemo(dealRef, module).build());
                    break;

                case VisitModule.M_NOTE:
                    r.add(new BuilderNote(dealRef, module).build());
                    break;

                case VisitModule.M_QUIZ:
                    r.add(new BuilderQuiz(dealRef, module).build());
                    break;

                case VisitModule.M_SERVICE:
                    r.add(new BuilderService(dealRef, module).build());
                    break;

                case VisitModule.M_COMMENT:
                    r.add(new BuilderComment(dealRef, module).build());
                    break;

                case VisitModule.M_RETAIL_AUDIT:
                    r.add(new BuilderRetailAudit(dealRef, module).build());
                    break;

                case VisitModule.M_AGREE:
                    r.add(new BuilderAgree(dealRef, module).build());
                    break;
            }
        }
        MyArray<VDealModule> result = MyArray.from(r);
        if (result.findFirst(new MyPredicate<VDealModule>() {
            @Override
            public boolean apply(VDealModule val) {
                int moduleId = val.getModuleId();
                return (moduleId == VisitModule.M_SERVICE || moduleId == VisitModule.M_ORDER)
                        && val.getModuleForms().nonEmpty();
            }
        }) != null) {
            boolean hasConsignmentModule = moduleIds.contains(VisitModule.M_CONSIGNMENT, VisitModule.KEY_ADAPTER);
            result = result.append(new BuilderPayment(dealRef, hasConsignmentModule).build());
        }

        if (orderModule != null && actionModule != null) {
            Set<String> orderBonusIds = orderModule.orderForms.getItems().flatMap(new MyFlatMapper<VDealOrderForm, String>() {
                @Override
                public MyArray<String> apply(VDealOrderForm vDealOrderForm) {
                    ArrayList<String> orderBonusIds = new ArrayList<>();
                    for (VDealOrder vDealOrder : vDealOrderForm.orders.getItems()) {
                        if (vDealOrder.bonusId.nonEmpty())
                            orderBonusIds.add(vDealOrder.bonusId.getValue());
                    }

                    return MyArray.from(orderBonusIds);
                }
            }).asSet();

            // TODO prepare action discount bonusIds
            for (VDealAction action : actionModule.form.actions.getItems()) {
                for (VDealActionCondition condition : action.conditions.getItems()) {
                    for (VDealActionConditionBonus bonus : condition.conditionBonus.getItems()) {
                        if (orderBonusIds.contains(bonus.bonusId)) {
                            bonus.isTaken.setValue(true);
                        }
                    }
                }
            }
        }

        if (orderModule != null || actionModule != null || gift != null) {

            result = result.append(
                    new VDealTotalModule(
                            new VDealTotalForm(
                                    new VisitModule(VisitModule.M_TOTAL, false),
                                    MyArray.from(orderModule).filterNotNull(),
                                    MyArray.from(actionModule).filterNotNull(),
                                    MyArray.from(overload).filterNotNull(),
                                    MyArray.from(gift).filterNotNull())
                    )
            );
        }

        return result;
    }

    private static MyArray<VDealModule> makeDealSupervisor(DealRef dealRef) {
        MyArray<VisitModule> moduleIds = getModuleIds(dealRef);

        ArrayList<VDealModule> r = new ArrayList<>();
        for (VisitModule module : moduleIds) {
            switch (module.id) {

                case VisitModule.M_PHOTO:
                    r.add(new BuilderPhoto(dealRef, module).build());
                    break;

                case VisitModule.M_STOCK:
                    r.add(new BuilderStock(dealRef, module).build());
                    break;

                case VisitModule.M_RETAIL_AUDIT:
                    r.add(new BuilderRetailAudit(dealRef, module).build());
                    break;

                case VisitModule.M_QUIZ:
                    r.add(new BuilderQuiz(dealRef, module).build());
                    break;

                case VisitModule.M_COMMENT:
                    r.add(new BuilderComment(dealRef, module).build());
                    break;

                case VisitModule.M_MEMO:
                    r.add(new BuilderMemo(dealRef, module).build());
                    break;
            }
        }
        return MyArray.from(r);
    }


    private static ValueArray<VDealModule> makeModules(final DealRef dealRef) {
        MyArray<VDealModule> result = MyArray.emptyArray();

        switch (dealRef.dealHolder.deal.dealType) {
            case Deal.DEAL_SUPERVISOR:
                result = makeDealSupervisor(dealRef);
                break;
            case Deal.DEAL_RETURN:
                result = makeDealReturn(dealRef);
                break;
            case Deal.DEAL_EXTRAORDINARY:
                result = makeDealExtraordinary(dealRef);
                break;
            case Deal.DEAL_ORDER:
            case Deal.DEAL_DOCTOR:
            case Deal.DEAL_PHARMCY:
                result = makeDealOrder(dealRef);
                break;
        }

        shareModuleValues(result);

        return new ValueArray<>(result);
    }

    private static MyArray<VisitModule> getModuleIds(final DealRef dealRef) {
        MyArray<VisitModule> keys = dealRef.getModuleIds();

        MyArray<VisitModule> savedKeys = dealRef.dealHolder.deal.modules.map(DealModule.KEY_ADAPTER)
                .map(new MyMapper<Integer, VisitModule>() {
                    @Override
                    public VisitModule apply(Integer id) {
                        return VisitModule.makeDefault(id);
                    }
                });

        keys = keys.union(savedKeys, VisitModule.KEY_ADAPTER);
        keys.checkUniqueness(VisitModule.KEY_ADAPTER);
        return keys;
    }

    private static void shareModuleValues(MyArray<VDealModule> modules) {
        VDealStockModule stockModule = (VDealStockModule) modules.find(VisitModule.M_STOCK, VDealModule.KEY_ADAPTER);
        VDealOrderModule orderModule = (VDealOrderModule) modules.find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);

        MyArray<VDealStockProduct> stockProducts = MyArray.emptyArray();
        if (stockModule != null) {
            stockProducts = stockModule.form.stockProducts.getItems();
        }

        if (stockProducts.nonEmpty() && orderModule != null) {
            for (VDealOrderForm form : orderModule.orderForms.getItems()) {
                for (final VDealOrder order : form.orders.getItems()) {
                    MyArray<VDealStockProduct> filterStockProducts = stockProducts.filter(new MyPredicate<VDealStockProduct>() {
                        @Override
                        public boolean apply(VDealStockProduct val) {
                            return val.product.id.equals(order.product.id);
                        }
                    });
                    if (filterStockProducts != null && filterStockProducts.nonEmpty()) {
                        order.stocks = filterStockProducts.flatMap(new MyFlatMapper<VDealStockProduct, Quantity>() {
                            @Override
                            public MyArray<Quantity> apply(VDealStockProduct element) {
                                return element.getStocks().map(new MyMapper<VDealStock, Quantity>() {
                                    @Override
                                    public Quantity apply(VDealStock vDealStock) {
                                        return vDealStock.stock;
                                    }
                                });
                            }
                        });
                    }
                }
            }
        }
    }
}
