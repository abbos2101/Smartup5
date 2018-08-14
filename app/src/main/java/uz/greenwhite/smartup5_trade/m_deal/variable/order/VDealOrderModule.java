package uz.greenwhite.smartup5_trade.m_deal.variable.order;// 30.06.2016

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import uz.greenwhite.lib.Tuple3;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.bean.CardQuantity;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrder;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealOrderModule extends VDealModule {

    public final ValueArray<VDealOrderForm> orderForms;

    public VDealOrderModule(VisitModule module, ValueArray<VDealOrderForm> orderForms) {
        super(module);
        this.orderForms = orderForms;
    }

    @NonNull
    public BigDecimal getTotalOrderSum() {
        return orderForms.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealOrderForm>() {
            @Override
            public BigDecimal apply(BigDecimal acc, VDealOrderForm vDealOrderForm) {
                return acc.add(vDealOrderForm.getTotalPrice());
            }
        });
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        if (orderForms == null) return MyArray.emptyArray();
        return orderForms.getItems().filter(new MyPredicate<VDealOrderForm>() {
            @Override
            public boolean apply(VDealOrderForm vDealOrderForm) {
                return vDealOrderForm.enable;
            }
        }).toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VDealOrderForm form : orderForms.getItems()) {
            if (form.hasValue()) {
                return true;
            }
        }
        return false;
    }

    public HashSet<String> getOrderBonusIds() {
        return getModuleForms().reduce(new HashSet<String>(), new MyReducer<HashSet<String>, VForm>() {
            @Override
            public HashSet<String> apply(HashSet<String> items, VForm vForm) {
                VDealOrderForm form = (VDealOrderForm) vForm;
                for (VDealOrder order : form.orders.getItems()) {
                    if (!TextUtils.isEmpty(order.bonusId.getText())) {
                        items.add(order.bonusId.getText());
                    }
                }
                return items;
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return orderForms.getItems().toSuper();
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealOrder> os = new ArrayList<>();
        for (VDealOrderForm form : orderForms.getItems()) {
            for (VDealOrder o : form.orders.getItems()) {
                if (o.hasValue()) {
                    String warehouseId = form.warehouse.id;
                    String priceTypeId = form.priceType.id;
                    String productId = o.product.id;
                    String cardCode = o.price.cardCode;
                    BigDecimal price = o.realPrice.getQuantity();
                    BigDecimal quantity = o.getQuantity();
                    BigDecimal discount = o.margin.getQuantity();
                    String currencyId = form.currency.currencyId;

                    MyArray<CardQuantity> charges = o.balanceOfWarehouse != null ?
                            o.balanceOfWarehouse.getCharges(o.card, o.price.priceTypeId) :
                            MyArray.<CardQuantity>emptyArray();

                    os.add(new DealOrder(productId, warehouseId, priceTypeId, cardCode,
                            price, quantity, discount, currencyId, charges, o.mmlProduct,
                            o.bonusId.getText(), o.productUnitId));
                }
            }
        }
        return new DealOrderModule(MyArray.from(os));
    }

    //----------------------------------------------------------------------------------------------

    // Tuple3 first = ProductQuant, second = ProductAmount, third = ProductWeight
    public Tuple3 getProductInfoForAction() {
        HashMap<String, BigDecimal> productQuantity = new HashMap<>();
        HashMap<String, BigDecimal> productAmount = new HashMap<>();
        HashMap<String, BigDecimal> productWeight = new HashMap<>();

        for (VDealOrderForm form : orderForms.getItems()) {
            for (VDealOrder order : form.orders.getItems()) {
                if (order.hasValue()) {
                    BigDecimal quantity = order.getQuantity();

                    BigDecimal sumQuant = Util.nvl(productQuantity.get(order.product.id), BigDecimal.ZERO);
                    productQuantity.put(order.product.id, sumQuant.add(quantity));

                    BigDecimal totalAmount = Util.nvl(order.getTotalPrice(), BigDecimal.ZERO)
                            .multiply(form.currency.price);
                    BigDecimal sumAmount = Util.nvl(productAmount.get(order.product.id), BigDecimal.ZERO);
                    productAmount.put(order.product.id, sumAmount.add(totalAmount));

                    BigDecimal productTotalWeight = order.product.getWeight();
                    productTotalWeight = Util.nvl(productTotalWeight, BigDecimal.ZERO).multiply(quantity);
                    BigDecimal sumWeight = Util.nvl(productWeight.get(order.product.id), BigDecimal.ZERO);
                    productWeight.put(order.product.id, sumWeight.add(productTotalWeight));
                }
            }
        }
        return new Tuple3(productQuantity, productAmount, productWeight);
    }

    public Tuple3 getProductInfoForOverload() {
        HashMap<Pair<String, String>, HashMap<String, BigDecimal>> productQuantity = new HashMap<>();
        HashMap<Pair<String, String>, HashMap<String, BigDecimal>> productAmount = new HashMap<>();
        HashMap<Pair<String, String>, HashMap<String, BigDecimal>> productWeight = new HashMap<>();

        for (VDealOrderForm form : orderForms.getItems()) {
            for (VDealOrder order : form.orders.getItems()) {
                if (order.hasValue()) {
                    BigDecimal quantity = order.getQuantity();


                    Pair<String, String> KEY = new Pair<>(form.warehouse.id, form.currency.currencyId);


                    HashMap<String, BigDecimal> productQuantityFound = productQuantity.get(KEY);
                    if (productQuantityFound == null) {
                        productQuantityFound = new HashMap<>();
                        productQuantity.put(KEY, productQuantityFound);
                    }
                    BigDecimal sumQuant = Util.nvl(productQuantityFound.get(order.product.id), BigDecimal.ZERO);
                    productQuantityFound.put(order.product.id, sumQuant.add(quantity));


                    HashMap<String, BigDecimal> productAmountFound = productAmount.get(KEY);
                    if (productAmountFound == null) {
                        productAmountFound = new HashMap<>();
                        productAmount.put(KEY, productAmountFound);
                    }
                    BigDecimal totalAmount = Util.nvl(order.getTotalPrice(), BigDecimal.ZERO)
                            .multiply(form.currency.price);
                    BigDecimal sumAmount = Util.nvl(productAmountFound.get(order.product.id), BigDecimal.ZERO);
                    productAmountFound.put(order.product.id, sumAmount.add(totalAmount));


                    HashMap<String, BigDecimal> productWeightFound = productWeight.get(KEY);
                    if (productWeightFound == null) {
                        productWeightFound = new HashMap<>();
                        productWeight.put(KEY, productWeightFound);
                    }
                    BigDecimal productTotalWeight = order.product.getWeight();
                    productTotalWeight = Util.nvl(productTotalWeight, BigDecimal.ZERO).multiply(quantity);
                    BigDecimal sumWeight = Util.nvl(productWeightFound.get(order.product.id), BigDecimal.ZERO);
                    productWeightFound.put(order.product.id, sumWeight.add(productTotalWeight));
                }
            }
        }
        return new Tuple3(productQuantity, productAmount, productWeight);
    }


}
