package uz.greenwhite.smartup5_trade.m_deal_history;


import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGift;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealGiftModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHeader;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPayment;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealAction;
import uz.greenwhite.smartup5_trade.m_deal.bean.action.DealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverload;
import uz.greenwhite.smartup5_trade.m_deal.bean.overload.DealOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.service.DealService;
import uz.greenwhite.smartup5_trade.m_deal.bean.service.DealServiceModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDeal;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealHeader;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.HDealProduct;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.action.HActionModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.gift.HGiftModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.order.HOrderModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.overload.HOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.payment.HPayment;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.payment.HPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal_history.bean.service.HServiceModule;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;
import uz.greenwhite.smartup5_trade.m_session.bean.action.Condition;
import uz.greenwhite.smartup5_trade.m_session.bean.action.ConditionBonus;
import uz.greenwhite.smartup5_trade.m_session.bean.action.PersonAction;
import uz.greenwhite.smartup5_trade.m_session.bean.deal_history.DealHistory;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.Overload;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadLoad;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadRule;

public class HistoryUtil {

    public static Deal historyDealToLocalDeal(Scope scope, String dealId, HDeal hDeal) {
        HDealHeader hHeader = hDeal.header;

        HOrderModule hOrderModule = (HOrderModule) hDeal.modules.find(HDealModule.K_ORDER, HDealModule.KEY_ADAPTER);
        MyArray<DealOrder> orders = hOrderModule == null ? MyArray.<DealOrder>emptyArray() : hOrderModule.orders.map(new MyMapper<HDealProduct, DealOrder>() {
            @Override
            public DealOrder apply(HDealProduct val) {
                return new DealOrder(val.productId,
                        val.warehouseId,
                        val.priceTypeId,
                        val.cardCode,
                        new BigDecimal(val.price),
                        new BigDecimal(val.quantity),
                        new BigDecimal(val.margin),
                        val.currencyId,
                        MyArray.<CardQuantity>emptyArray(),
                        val.mml,
                        val.bonusId,
                        val.productUnitId);
            }
        });

        HGiftModule hGiftModule = (HGiftModule) hDeal.modules.find(HDealModule.K_GIFT, HDealModule.KEY_ADAPTER);
        MyArray<DealGift> gifts = hGiftModule == null ? MyArray.<DealGift>emptyArray() : hGiftModule.gifts.map(new MyMapper<HDealProduct, DealGift>() {
            @Override
            public DealGift apply(HDealProduct val) {
                return new DealGift(val.productId,
                        val.warehouseId,
                        new BigDecimal(val.quantity),
                        MyArray.<CardQuantity>emptyArray(),
                        val.productUnitId);
            }
        });

        final MyArray<PersonAction> personActions = scope.ref.getPersonActions();


        HActionModule hActionModule = (HActionModule) hDeal.modules.find(HDealModule.K_ACTION, HDealModule.KEY_ADAPTER);
        MyArray<DealAction> actions = hActionModule == null ? MyArray.<DealAction>emptyArray() : hActionModule.actions.map(new MyMapper<HDealProduct, DealAction>() {
            @Override
            public DealAction apply(final HDealProduct hAction) {
                PersonAction action = personActions.findFirst(new MyPredicate<PersonAction>() {
                    @Override
                    public boolean apply(PersonAction action) {
                        return action.conditions.contains(new MyPredicate<Condition>() {
                            @Override
                            public boolean apply(Condition condition) {
                                return condition.bonuses.contains(new MyPredicate<ConditionBonus>() {
                                    @Override
                                    public boolean apply(ConditionBonus bonus) {
                                        return hAction.bonusId.equals(bonus.bonusId);
                                    }
                                });
                            }
                        });
                    }
                });
                if (action == null) {
                    return null;
                }
                return new DealAction(action.actionId,
                        hAction.warehouseId,
                        hAction.productId,
                        new BigDecimal(hAction.quantity),
                        MyArray.<CardQuantity>emptyArray(),
                        hAction.bonusId,
                        hAction.productUnitId);
            }
        }).filterNotNull();

        final MyArray<Overload> refOverload = scope.ref.getOverloads();

        HOverloadModule hOverloadModule = (HOverloadModule) hDeal.modules.find(HDealModule.K_OVERLOAD, HDealModule.KEY_ADAPTER);
        MyArray<DealOverload> overloads = hOverloadModule == null ? MyArray.<DealOverload>emptyArray() : hOverloadModule.overloads.map(new MyMapper<HDealProduct, DealOverload>() {
            @Override
            public DealOverload apply(final HDealProduct val) {
                Overload overload = refOverload.findFirst(new MyPredicate<Overload>() {
                    @Override
                    public boolean apply(final Overload overload) {
                        return overload.rules.contains(new MyPredicate<OverloadRule>() {
                            @Override
                            public boolean apply(OverloadRule overloadRule) {
                                return overloadRule.loads.contains(new MyPredicate<OverloadLoad>() {
                                    @Override
                                    public boolean apply(OverloadLoad overloadLoad) {
                                        return val.loadId.equals(overloadLoad.loadId);
                                    }
                                });
                            }
                        });
                    }
                });

                return new DealOverload(overload.overloadId,
                        val.warehouseId,
                        val.priceTypeId,
                        val.cardCode,
                        val.loadId,
                        val.productId,
                        new BigDecimal(val.quantity),
                        new BigDecimal(val.price),
                        MyArray.<CardQuantity>emptyArray(),
                        val.currencyId,
                        val.productUnitId);
            }
        });

        HPaymentModule hPaymentModule = (HPaymentModule) hDeal.modules.find(HDealModule.K_PAYMENT, HDealModule.KEY_ADAPTER);
        MyArray<DealPayment> payments = hPaymentModule == null ? MyArray.<DealPayment>emptyArray() : hPaymentModule.payments.map(new MyMapper<HPayment, DealPayment>() {
            @Override
            public DealPayment apply(HPayment val) {
                return new DealPayment(val.currencyId,
                        val.paymentTypeId,
                        new BigDecimal(val.amount),
                        val.consignAmount,
                        val.consignDate);
            }
        });

        HServiceModule hServiceModule = (HServiceModule) hDeal.modules.find(HDealModule.K_SERVICE, HDealModule.KEY_ADAPTER);
        MyArray<DealService> services = hServiceModule == null ? MyArray.<DealService>emptyArray() : hServiceModule.services.map(new MyMapper<HDealProduct, DealService>() {
            @Override
            public DealService apply(HDealProduct val) {
                return new DealService(val.productId,
                        val.priceTypeId,
                        new BigDecimal(val.price),
                        new BigDecimal(val.quantity),
                        new BigDecimal(val.margin),
                        val.currencyId,
                        val.productUnitId);
            }
        });

        DealOrderModule orderModule = new DealOrderModule(orders);
        DealGiftModule giftModule = new DealGiftModule(gifts);
        DealActionModule actionModule = new DealActionModule(actions);
        DealOverloadModule overloadModule = new DealOverloadModule(overloads);
        DealPaymentModule paymentModule = new DealPaymentModule(payments);
        DealServiceModule serviceModule = new DealServiceModule(services);

        DealHeader header = new DealHeader(hHeader.begunOn,
                hHeader.endedOn,
                0,
                hHeader.location,
                hHeader.deliveryDate,
                RoundModel.make(hHeader.roundModel),
                hHeader.contractId,
                hHeader.expeditorId,
                "");

        MyArray<DealModule> modules = MyArray.from(
                orderModule, giftModule, actionModule, overloadModule, paymentModule, serviceModule);

        String localDealId = String.valueOf(AdminApi.nextSequence());
        return new Deal(hDeal.filialId, hDeal.roomId, hDeal.personId,
                localDealId, Deal.DEAL_EXTRAORDINARY, header, modules,
                DealHistory.DEAL_STATE_NEW.equals(hDeal.state) ? "Y" : "N", dealId, hDeal.state);
    }

}
