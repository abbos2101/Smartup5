package uz.greenwhite.smartup5_trade.m_shipped.bean;

import android.util.SparseArray;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SOrder;
import uz.greenwhite.smartup5_trade.m_shipped.variable.SDealRef;

public class SDBalance {

    private final SparseArray<SparseArray<BigDecimal>> wp;

    public SDBalance(SDealRef sDealRef) {
        this.wp = new SparseArray<>();
        initial(sDealRef);
    }

    private void initial(final SDealRef sDealRef) {
        MyArray<SDealHolder> sDealHolders = sDealRef.getSDealHolders()
                .filter(new MyPredicate<SDealHolder>() {
                    @Override
                    public boolean apply(SDealHolder val) {
                        return !sDealRef.holder.deal.dealId.equals(val.deal.dealId);
                    }
                });

        for (SDealHolder s : sDealHolders) {
            for (SOrder p : s.deal.orders) {
                BigDecimal subtractQuant = p.originQuant.subtract(p.deliverQuant);
                BigDecimal result = p.returnQuant.add(subtractQuant);
                this.addQuant(p.warehouseId, p.productId, result);
            }
        }
    }

    public BigDecimal getQuant(String warehouseId, String productId) {
        SparseArray<BigDecimal> w = this.wp.get(Integer.parseInt(warehouseId));
        if (w != null && w.size() != 0) {
            return w.get(Integer.parseInt(productId), BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    public void addQuant(String warehouseId, String productId, BigDecimal quant) {
        SparseArray<BigDecimal> products = wp.get(Integer.parseInt(warehouseId));
        if (products == null) {
            products = new SparseArray<>();
            wp.put(Integer.parseInt(warehouseId), products);
        }
        BigDecimal rQuant = products.get(Integer.parseInt(productId), BigDecimal.ZERO);
        products.put(Integer.parseInt(productId), rQuant.add(quant));

    }
}
