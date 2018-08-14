package uz.greenwhite.smartup5_trade.m_deal.variable;// 30.06.2016

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyFlatMapper;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.recom.DealRecom;
import uz.greenwhite.smartup5_trade.m_deal.bean.recom.DealRecomModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.attach.VAttachModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.error.VDealErrorForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.error.VDealErrorModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.info.VDealInfoForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.info.VDealInfoModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverload;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadLoad;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadRule;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPayment;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentCurrency;
import uz.greenwhite.smartup5_trade.m_deal.variable.payment.VDealPaymentModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhoto;
import uz.greenwhite.smartup5_trade.m_deal.variable.photo.VDealPhotoModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAudit;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.retail_audit.VDealRetailAuditModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealService;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealServiceForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealServiceModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDeal extends VariableLike {

    public final DealRef dealRef;
    public final VDealHeader header;
    public final ValueArray<VDealModule> modules;
    private long startTime;

    public VDeal(DealRef dealRef,
                 VDealHeader header,
                 ValueArray<VDealModule> modules) {
        this.dealRef = dealRef;
        this.header = header;
        this.modules = gatherModule(modules);
        this.startTime = 0;
    }

    private ValueArray<VDealModule> gatherModule(ValueArray<VDealModule> modules) {

        modules.prepend(new VDealInfoModule(new VDealInfoForm(dealRef)));

        if (!TextUtils.isEmpty(dealRef.dealHolder.entryState.serverResult)) {
            modules.prepend(new VDealErrorModule(new VDealErrorForm(dealRef)));
        }

        ValueArray<VDealModule> result = new ValueArray<>();
        for (VDealModule module : modules.getItems()) {
            if (module.getModuleForms().nonEmpty()) result.append(module);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public String getFirstModuleFormCode() {
        for (VDealModule module : modules.getItems()) {
            MyArray<VForm> forms = module.getModuleForms();
            if (forms.nonEmpty()) {
                return forms.get(0).code;
            }
        }
        return null;
    }

    public boolean isModuleReady(int moduleId) {
        VDealModule vDealModule = modules.getItems().find(moduleId, VDealModule.KEY_ADAPTER);
        return vDealModule != null && vDealModule.isReady();
    }

    public boolean isValidForm(VDealForm form) {
        for (VDealModule f : modules.getItems()) {
            if (((VisitModule) f.tag).id == ((VisitModule) form.tag).id) {
                return true;
            }
            if (f.isMandatory() && !isModuleReady(((VisitModule) f.tag).id)) {
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends VDealForm> T findForm(int moduleId, String formCode) {
        VDealModule module = modules.getItems().find(moduleId, VDealModule.KEY_ADAPTER);
        if (module == null) return null;
        MyArray<VForm> forms = module.getModuleForms();
        return (T) forms.find(formCode, VForm.KEY_ADAPTER);
    }

    @SuppressWarnings("unchecked")
    public <T extends VDealForm> T findForm(String formCode) {
        for (VDealModule m : modules.getItems()) {
            for (VForm f : m.getModuleForms()) {
                if (f.code.equals(formCode)) {
                    return (T) f;
                }
            }
        }
        return null;
    }

    public void reset() {
        startTime = 0;
    }

    public void start() {
        if (startTime <= 0) {
            startTime = System.currentTimeMillis();
        }

        if (header.begunOn != null) {
            return;
        }

        header.begunOn = new Date();
        header.roundModel = dealRef.getRoundModel();
    }

    public Deal convertToDeal(String dealNew, String dealState) {
        header.endedOn = new Date();
        if (startTime > 0) {
            header.spendTime += ((System.currentTimeMillis() - startTime) / 1000);
            header.spendTime = Math.round(header.spendTime);
        }

        //TODO refactor
        VDealOrderModule orderModule = (VDealOrderModule) modules.getItems()
                .find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        VDealActionModule actionModule = (VDealActionModule) modules.getItems()
                .find(VisitModule.M_ACTION, VDealModule.KEY_ADAPTER);
        VOverloadModule overloadModule = (VOverloadModule) modules.getItems()
                .find(VisitModule.M_OVERLOAD, VDealModule.KEY_ADAPTER);

        if (orderModule != null && actionModule != null && actionModule.getModuleForms().nonEmpty()) {
            DealUtil.makeAction(orderModule, actionModule.form);
        }

        if (orderModule != null && overloadModule != null && overloadModule.getModuleForms().nonEmpty()) {
            DealUtil.makeOverload(orderModule, overloadModule.form);
        }


        MyArray<DealModule> ms = modules.getItems().filter(new MyPredicate<VDealModule>() {
            @Override
            public boolean apply(VDealModule vDealModule) {
                return vDealModule.hasValue();
            }
        }).map(new MyMapper<VDealModule, DealModule>() {
            @Override
            public DealModule apply(VDealModule vDealModule) {
                return vDealModule.convertToDealModule();
            }
        }).filterNotNull();

        if (orderModule != null) {
            //TODO refactor Recom

            MyArray<VDealOrderForm> forms = orderModule.getModuleForms().toSuper();
            final Set<String> userProductIds = new HashSet<>();
            MyArray<VDealOrder> vDealOrders = forms.flatMap(new MyFlatMapper<VDealOrderForm, VDealOrder>() {
                @Override
                public MyArray<VDealOrder> apply(VDealOrderForm element) {
                    return element.orders.getItems().filter(new MyPredicate<VDealOrder>() {
                        @Override
                        public boolean apply(VDealOrder vDealOrder) {
                            if (!userProductIds.contains(vDealOrder.product.id)) {
                                userProductIds.add(vDealOrder.product.id);
                                return vDealOrder.canGenerateRecom();
                            }
                            return false;
                        }
                    });
                }
            });
            ArrayList<DealRecom> recoms = new ArrayList<>();
            for (VDealOrder order : vDealOrders) {
                BigDecimal recomOrder = order.getRecomOrderToBigDecimal();
                if (recomOrder.compareTo(BigDecimal.ZERO) != 0) {
                    recoms.add(new DealRecom(order.product.id, recomOrder));
                }
            }
            if (!recoms.isEmpty()) {
                ms = ms.append(new DealRecomModule(MyArray.from(recoms)));
            }
        }

        Deal d = dealRef.dealHolder.deal;
        return new Deal(d.filialId, d.roomId, d.outletId, d.dealLocalId,
                d.dealType, header.toDealHeader(), ms, dealNew, d.finalDealId, dealState);
    }

    public void prepareToReady() {
        MyArray<VDealModule> items = modules.getItems();

        VDealPaymentModule paymentModule = (VDealPaymentModule) items.find(VisitModule.M_PAYMENT, VDealModule.KEY_ADAPTER);
        if (paymentModule != null) {
            if (!dealRef.setting.deal.multiAccounts) {
                paymentModule.form.setCurrencyOrderSums(getCurrencyOrderSums());

                MyArray<VDealPaymentCurrency> payments = paymentModule.form.payment.getItems()
                        .filter(DealUtil.getPaymentCurrencyPredicate(this));

                for (VDealPaymentCurrency c : payments) {
                    BigDecimal total = c.getOrderCurrencySum();

                    ValueSpinner singlePayment = c.getSinglePayment(total);
                    MyArray<VDealPayment> multyPayment = c.getMultyPayment();

                    if (singlePayment.options.size() == 2) {
                        singlePayment.setValue(singlePayment.options.get(1));
                    }

                    SpinnerOption value = singlePayment.getValue();
                    for (VDealPayment p : multyPayment) {
                        p.amount.setValue(BigDecimal.ZERO);
                        if (p.paymentType.id.equals(value.code) && !"#null".equals(value.code)) {
                            p.amount.setValue(total);
                        }
                    }
                }
            }


        }

    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }

        for (VDealModule module : modules.getItems()) {
            ErrorResult moduleError = module.getMandatoryError();
            if (moduleError.isError()) {
                return moduleError;
            }
        }

        TradeRoleKeys roleKeys = dealRef.getRoleKeys();
        if (dealRef.isRole(roleKeys.expeditor) && dealRef.dealHolder.deal.is(Deal.DEAL_RETURN)) {
            if (header.agents == null || header.agents.options.isEmpty()) {
                return ErrorResult.make(DS.getString(R.string.deal_attach_agent_is_null_or_empty));
            }

            if (TextUtils.isEmpty(header.agents.getValue().code)) {
                return ErrorResult.make(DS.getString(R.string.deal_attach_agent_not_select));
            }
        }

        MyArray<VDealModule> items = modules.getItems();
        VDealOrderModule orderModule = (VDealOrderModule) items.find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        VDealServiceModule serviceModule = (VDealServiceModule) items.find(VisitModule.M_SERVICE, VDealModule.KEY_ADAPTER);
        VDealPaymentModule paymentModule = (VDealPaymentModule) items.find(VisitModule.M_PAYMENT, VDealModule.KEY_ADAPTER);
        VOverloadModule overloadModule = (VOverloadModule) items.find(VisitModule.M_OVERLOAD, VDealModule.KEY_ADAPTER);

        if ((orderModule != null || serviceModule != null) && paymentModule != null) {
            BigDecimal amount = BigDecimal.ZERO;
            if (orderModule != null) amount = amount.add(orderModule.getTotalOrderSum());
            if (serviceModule != null) amount = amount.add(serviceModule.getTotalOrderSum());
            if (overloadModule != null) amount = amount.add(overloadModule.getTotalSum());

            BigDecimal payment = paymentModule.getTotalPaymentSum();

            if (amount.compareTo(BigDecimal.ZERO) != 0 && payment.compareTo(BigDecimal.ZERO) == 0) {
                return ErrorResult.make(DS.getString(R.string.deal_specify_type_payment));
            }

            if (amount.compareTo(payment) != 0) {
                return ErrorResult.make(DS.getString(R.string.deal_order_total_sum_not_equal));
            }
        }

        if (paymentModule != null && paymentModule.hasConsignment()) {

            MyArray<VDealPaymentCurrency> payments = paymentModule.form.payment.getItems()
                    .filter(DealUtil.getPaymentCurrencyPredicate(this));

            if (payments.contains(new MyPredicate<VDealPaymentCurrency>() {
                @Override
                public boolean apply(VDealPaymentCurrency val) {
                    return val.getMultyPayment().contains(new MyPredicate<VDealPayment>() {
                        @Override
                        public boolean apply(VDealPayment item) {
                            if (item.consignmentDate.nonEmpty()) {
                                Integer data;
                                if (header.deliveryDate.nonEmpty()) {
                                    data = Integer.parseInt(DateUtil.convert(header.deliveryDate.getText(),
                                            DateUtil.FORMAT_AS_NUMBER));
                                } else {
                                    data = Integer.parseInt(DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER));
                                }
                                return data >= Integer.parseInt(DateUtil.convert(
                                        item.consignmentDate.getText(), DateUtil.FORMAT_AS_NUMBER));
                            }
                            return false;
                        }
                    });
                }
            })) {
                return ErrorResult.make(DS.getString(R.string.deal_consign_date_error));
            }
        }

        if (dealRef.setting.deal.requiredDeliveryDate) {
            if (header.deliveryDate.isEmpty()) {
                return ErrorResult.make(DS.getString(R.string.deal_required_delivery_date));
            }
        }
        return ErrorResult.NONE;
    }

    public Map<String, BigDecimal> getCurrencyOrderSums() {
        Map<String, BigDecimal> m = new HashMap<>();

        VDealOrderModule order = (VDealOrderModule) modules.getItems()
                .find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        if (order != null) {
            for (VDealOrderForm f : order.orderForms.getItems()) {
                BigDecimal totalSum = Util.nvl(m.get(f.currency.currencyId), BigDecimal.ZERO);
                m.put(f.currency.currencyId, totalSum.add(f.getTotalPrice()));
            }
        }

        VDealServiceModule service = (VDealServiceModule) modules.getItems()
                .find(VisitModule.M_SERVICE, VDealModule.KEY_ADAPTER);
        if (service != null) {
            for (VDealServiceForm f : service.forms.getItems()) {
                BigDecimal totalSum = Util.nvl(m.get(f.currency.currencyId), BigDecimal.ZERO);
                m.put(f.currency.currencyId, totalSum.add(f.getTotalPrice()));
            }
        }

        VOverloadModule overload = (VOverloadModule) modules.getItems()
                .find(VisitModule.M_OVERLOAD, VDealModule.KEY_ADAPTER);

        if (overload != null && overload.getModuleForms().nonEmpty()) {
            for (VOverload vOver : overload.form.overloads.getItems()) {
                if (!vOver.isCanUse()) {
                    continue;
                }

                for (VOverloadRule rule : vOver.rules.getItems()) {
                    for (VOverloadLoad load : rule.loads.getItems()) {
                        if (load.isTaken.getValue()) {
                            for (VOverloadProduct product : load.getProducts()) {
                                if (product.canUse()) {
                                    PriceType priceType = product.getPriceType();
                                    BigDecimal totalSum = Util.nvl(m.get(priceType.currencyId), BigDecimal.ZERO);
                                    m.put(priceType.currencyId, totalSum.add(product.getTotalPrice()));
                                }
                            }
                        }
                    }
                }

            }
        }
        return m;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(header, modules);
    }

    public void makePhotoSaveAndRemove(Scope scope, boolean ready) {
        VDealPhotoModule pModule = (VDealPhotoModule) modules.getItems()
                .find(VisitModule.M_PHOTO, VDealModule.KEY_ADAPTER);
        if (pModule != null) {
            int state = ready ? EntryState.READY : EntryState.SAVED;
            for (VDealPhoto p : pModule.form.photos.getItems()) {
                pModule.form.makePhotoSaved(scope, p.sha, state);
            }
            pModule.form.removeNotSavedPhoto(scope);
        }
    }

    public void moveOrder(String codeFrom, String codeTo) {
        VDealOrderModule module = (VDealOrderModule) modules.getItems().find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        VDealServiceModule serviceModule = (VDealServiceModule) modules.getItems().find(VisitModule.M_SERVICE, VDealModule.KEY_ADAPTER);

        MyArray<VForm> forms = module.orderForms.getItems().toSuper();

        final VDealOrderForm ordersFrom = (VDealOrderForm) forms.find(codeFrom, VForm.KEY_ADAPTER);
        final VDealOrderForm ordersTo = (VDealOrderForm) forms.find(codeTo, VForm.KEY_ADAPTER);

        if (ordersFrom == null || ordersTo == null) return;

        boolean equalCurrency = ordersTo.currency.currencyId.equals(ordersFrom.currency.currencyId);
        boolean equalPrice = ordersTo.priceType.id.equals(ordersFrom.priceType.id);

        Map<String, BigDecimal> map = new HashMap<>();

        if (equalCurrency && equalPrice) {
            if (ordersFrom.priceType.withCard && !ordersTo.priceType.withCard) {
                for (final VDealOrder from : ordersFrom.orders.getItems()) {
                    BigDecimal res = Util.nvl(map.get(from.product.id), BigDecimal.ZERO);
                    map.put(from.product.id, res.add(from.getQuantity()));
                }
                for (final VDealOrder to : ordersTo.orders.getItems()) {
                    BigDecimal quantity = Util.nvl(map.get(to.product.id), BigDecimal.ZERO);
                    if (quantity.compareTo(BigDecimal.ZERO) != 0) {
                        to.setQuantity(quantity);
                    }
                }
            } else {
                for (final VDealOrder from : ordersFrom.orders.getItems()) {
                    VDealOrder to = ordersTo.orders.getItems()
                            .find(VDealOrder.getKey(from.product.id, from.price.cardCode), VDealOrder.KEY_ADAPTER);
                    if (to != null) {
                        to.setQuantity(from.getQuantity());
                    }
                }
            }
        } else {
            MyArray<String> paymentIds = forms.reduce(MyArray.<String>emptyArray(), new MyReducer<MyArray<String>, VForm>() {
                @Override
                public MyArray<String> apply(MyArray<String> ids, VForm form) {
                    VDealOrderForm v = (VDealOrderForm) form;
                    return ids.isEmpty() ? v.priceType.paymentTypeIds : Utils.intersect(ids, v.priceType.paymentTypeIds);
                }
            });

            boolean notEqualPayments = Utils.intersect(ordersFrom.priceType.paymentTypeIds, paymentIds).isEmpty();
            Map<String, BigDecimal> serviceMap = new HashMap<>();

            if (notEqualPayments) {
                for (VDealOrderForm f : module.orderForms.getItems()) {
                    if (f.enable && f.hasValue()) {
                        for (final VDealOrder v : f.orders.getItems()) {
                            BigDecimal res = Util.nvl(map.get(v.product.id), BigDecimal.ZERO);
                            map.put(v.product.id, res.add(v.getQuantity()));
                            v.setQuantity(BigDecimal.ZERO);
                        }
                    }
                }

                if (serviceModule != null) {
                    for (VDealServiceForm f : serviceModule.forms.getItems()) {
                        if (f.enable && f.hasValue()) {
                            for (VDealService v : f.services.getItems()) {
                                BigDecimal res = Util.nvl(serviceMap.get(v.product.id), BigDecimal.ZERO);
                                serviceMap.put(v.product.id, res.add(v.quant.getQuantity()));
                                v.quant.setValue(BigDecimal.ZERO);
                            }
                        }
                    }
                }

            } else {
                for (final VDealOrder from : ordersFrom.orders.getItems()) {
                    BigDecimal res = Util.nvl(map.get(from.product.id), BigDecimal.ZERO);
                    map.put(from.product.id, res.add(from.getQuantity()));
                    from.setQuantity(BigDecimal.ZERO);
                }
            }

            for (final VDealOrder to : ordersTo.orders.getItems()) {
                BigDecimal quantity = Util.nvl(map.get(to.product.id), BigDecimal.ZERO);
                if (quantity.compareTo(BigDecimal.ZERO) != 0) {
                    to.setQuantity(quantity);
                }
            }

            if (serviceModule != null && !serviceMap.isEmpty()) {
                VDealServiceForm serviceTo = serviceModule.forms.getItems().findFirst(new MyPredicate<VDealServiceForm>() {
                    @Override
                    public boolean apply(VDealServiceForm val) {
                        return val.priceType.id.equals(ordersTo.priceType.id);
                    }
                });
                for (final VDealService v : serviceTo.services.getItems()) {
                    BigDecimal quantity = Util.nvl(serviceMap.get(v.product.id), BigDecimal.ZERO);
                    if (quantity.compareTo(BigDecimal.ZERO) != 0) {
                        v.quant.setValue(quantity);
                    }
                }

            }
        }
    }

    public ValueSpinner getOrderFormsExceptThis(final String formCode) {
        VDealOrderModule module = (VDealOrderModule) modules.getItems()
                .find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);


        MyArray<SpinnerOption> warehouse = module.orderForms.getItems().filter(new MyPredicate<VDealOrderForm>() {
            @Override
            public boolean apply(VDealOrderForm f) {
                return !f.code.equals(formCode);
            }
        }).map(new MyMapper<VDealOrderForm, SpinnerOption>() {
            @Override
            public SpinnerOption apply(VDealOrderForm f) {
                return new SpinnerOption(f.code, f.warehouse.name + " | " + f.priceType.name, f);
            }
        });

        if (warehouse.isEmpty()) {
            return null;
        }
        return new ValueSpinner(warehouse);
    }

    private String getOrderOrServiceFirstFoundCurrencyId(@Nullable VDealOrderModule orderModule,
                                                         @Nullable VDealServiceModule serviceModule) {
        String currencyId = null;

        if (orderModule != null) {
            VDealOrderForm orderForm = orderModule.orderForms.getItems().findFirst(new MyPredicate<VDealOrderForm>() {
                @Override
                public boolean apply(VDealOrderForm val) {
                    return val.hasValue();
                }
            });

            if (orderForm != null) {
                currencyId = orderForm.currency.currencyId;
            }

        }


        if (serviceModule != null && TextUtils.isEmpty(currencyId)) {
            VDealServiceForm serviceForm = serviceModule.forms.getItems().findFirst(new MyPredicate<VDealServiceForm>() {
                @Override
                public boolean apply(VDealServiceForm val) {
                    return val.hasValue();
                }
            });
            if (serviceForm != null) {
                currencyId = serviceForm.currency.currencyId;
            }

        }
        return currencyId;
    }

    private MyArray<String> getOrderAndServicePaymentIntersects(@Nullable VDealOrderModule orderModule,
                                                                @Nullable VDealServiceModule serviceModule) {
        MyArray<String> paymentIds = MyArray.emptyArray();
        if (orderModule != null) {
            paymentIds = orderModule.orderForms.getItems().reduce(paymentIds, new MyReducer<MyArray<String>, VDealOrderForm>() {
                @Override
                public MyArray<String> apply(MyArray<String> ids, VDealOrderForm val) {
                    MyArray<String> pIds = val.priceType.paymentTypeIds;
                    if (val.hasValue() && pIds.nonEmpty()) {
                        ids = ids.isEmpty() ? pIds : Utils.intersect(ids, pIds);
                    }
                    return ids;
                }
            });
        }

        if (serviceModule != null) {
            paymentIds = serviceModule.forms.getItems().reduce(paymentIds, new MyReducer<MyArray<String>, VDealServiceForm>() {
                @Override
                public MyArray<String> apply(MyArray<String> ids, VDealServiceForm val) {
                    MyArray<String> pIds = val.priceType.paymentTypeIds;
                    if (val.hasValue() && pIds.nonEmpty()) {
                        ids = ids.isEmpty() ? pIds : Utils.intersect(ids, pIds);
                    }
                    return ids;
                }
            });
        }
        return paymentIds;
    }

    public void checkModuleForms() {
        VDealOrderModule orderModule = (VDealOrderModule) modules.getItems().find(VisitModule.M_ORDER, VDealModule.KEY_ADAPTER);
        VDealServiceModule serviceModule = (VDealServiceModule) modules.getItems().find(VisitModule.M_SERVICE, VDealModule.KEY_ADAPTER);
        VOverloadModule overloadModule = (VOverloadModule) modules.getItems().find(VisitModule.M_OVERLOAD, VDealModule.KEY_ADAPTER);
        VAttachModule attachModule = (VAttachModule) modules.getItems().find(VisitModule.M_ATTACH, VDealModule.KEY_ADAPTER);

        if (attachModule != null) {
            attachModule.form.setVariableDeal(this);
        }

        String currencyId = getOrderOrServiceFirstFoundCurrencyId(orderModule, serviceModule);

        MyArray<String> paymentIds = getOrderAndServicePaymentIntersects(orderModule, serviceModule);

        if (orderModule != null) {
            for (VDealOrderForm v : orderModule.orderForms.getItems()) {
                v.enable = !(!TextUtils.isEmpty(currencyId) && !currencyId.equals(v.currency.currencyId)) &&
                        !(v.priceType.paymentTypeIds.nonEmpty() && paymentIds.nonEmpty() &&
                                Utils.intersect(paymentIds, v.priceType.paymentTypeIds).isEmpty());
            }
        }

        if (serviceModule != null) {
            for (VDealServiceForm v : serviceModule.forms.getItems()) {
                v.enable = !(!TextUtils.isEmpty(currencyId) && !currencyId.equals(v.currency.currencyId)) &&
                        !(v.priceType.paymentTypeIds.nonEmpty() && paymentIds.nonEmpty()
                                && Utils.intersect(paymentIds, v.priceType.paymentTypeIds).isEmpty());
            }
        }

        if (orderModule != null && orderModule.getModuleForms().nonEmpty() &&
                overloadModule != null && overloadModule.getModuleForms().nonEmpty()) {
            DealUtil.makeOverload(orderModule, overloadModule.form);
        }

        VDealStockModule stockModule = (VDealStockModule) modules.getItems().find(VisitModule.M_STOCK, VDealModule.KEY_ADAPTER);
        VDealRetailAuditModule auditModule = (VDealRetailAuditModule) modules.getItems().find(VisitModule.M_RETAIL_AUDIT, VDealModule.KEY_ADAPTER);
        if (auditModule != null && auditModule.forms.getItems().nonEmpty() &&
                stockModule != null && stockModule.form != null) {
            MyArray<VDealRetailAudit> vDealRetailAudits = auditModule.forms.getItems().flatMap(new MyFlatMapper<VDealRetailAuditForm, VDealRetailAudit>() {
                @Override
                public MyArray<VDealRetailAudit> apply(VDealRetailAuditForm element) {
                    return element.retailAudits.getItems();
                }
            });

            Map<String, ArrayList<VDealRetailAudit>> auditProducts = new HashMap<>();
            for (VDealRetailAudit val : vDealRetailAudits) {
                if (val.retailAuditProduct == null || val.alreadySetted || !val.retailAuditProduct.ourProduct)
                    continue;

                ArrayList<VDealRetailAudit> valueBigDecimals = auditProducts.get(val.product.id);
                if (valueBigDecimals == null) {
                    valueBigDecimals = new ArrayList<>();
                    auditProducts.put(val.product.id, valueBigDecimals);
                }
                valueBigDecimals.add(val);
            }

            MyArray<VDealStockProduct> dealStockProducts = stockModule.form.stockProducts.getItems();
            for (Map.Entry<String, ArrayList<VDealRetailAudit>> mapEntry : auditProducts.entrySet()) {
                VDealStockProduct vDealStockProduct = dealStockProducts.find(mapEntry.getKey(), VDealStockProduct.KEY_ADAPTER);
                BigDecimal allStockQuantity;

                if (vDealStockProduct == null || (allStockQuantity = vDealStockProduct.getAllStockQuantity())
                        .compareTo(BigDecimal.ZERO) == 0)
                    continue;

                for (VDealRetailAudit val : mapEntry.getValue()) {
                    val.alreadySetted = true;
                    if (val.extFaceOtherQuant.isZero()) {
                        val.extFaceOtherQuant.setValue(allStockQuantity);
                    }
                }
            }
        }
    }
}
