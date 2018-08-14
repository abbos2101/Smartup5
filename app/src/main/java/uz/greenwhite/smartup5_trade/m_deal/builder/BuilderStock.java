package uz.greenwhite.smartup5_trade.m_deal.builder;// 30.06.2016

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealStock;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealStockModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockProduct;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class BuilderStock {

    public final DealRef dealRef;
    public final VisitModule module;
    public final MyArray<DealStock> initial;

    public BuilderStock(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitialStock();
    }

    private MyArray<DealStock> getInitialStock() {
        DealStockModule module = dealRef.findDealModule(this.module.id);
        return module != null ? module.stocks : MyArray.<DealStock>emptyArray();
    }

    private MyArray<String> getProductIds() {
        Set<String> result = dealRef.getStockProductIds().asSet();
        for (DealStock stock : initial) {
            result.add(stock.productId);
        }
        return MyArray.from(result);
    }

    private VDealStockForm makeForm() {
        MyArray<String> productIds = getProductIds();

        ArrayList<VDealStockProduct> result = new ArrayList<>();
        for (final String productId : productIds) {
            Product product = dealRef.findProduct(productId);
            if (product == null) continue;

            MyArray<DealStock> filterDealStocks = initial.filter(new MyPredicate<DealStock>() {
                @Override
                public boolean apply(DealStock val) {
                    return val.productId.equals(productId);
                }
            });

            ArrayList<VDealStock> vDealStocks = new ArrayList<>();
            if (filterDealStocks.isEmpty()) {
                vDealStocks.add(new VDealStock(product, null, null, 0));
            } else {
                for (DealStock val : filterDealStocks) {
                    vDealStocks.add(new VDealStock(product, val.getStock(), val.expireDate, vDealStocks.size()));
                }
            }
            result.add(new VDealStockProduct(product, new ValueArray<>(MyArray.from(vDealStocks))));
        }
        Collections.sort(result, new Comparator<VDealStockProduct>() {
            @Override
            public int compare(VDealStockProduct l, VDealStockProduct r) {
                int compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                if (compare == 0) {
                    return CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                }
                return compare;
            }
        });
        return new VDealStockForm(module, new ValueArray<>(MyArray.from(result)));
    }

    public VDealStockModule build() {
        return new VDealStockModule(module, makeForm());
    }
}
