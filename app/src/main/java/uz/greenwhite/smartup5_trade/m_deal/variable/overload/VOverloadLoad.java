package uz.greenwhite.smartup5_trade.m_deal.variable.overload;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadLoad;

public class VOverloadLoad extends VariableLike {

    private final DealRef dealRef;

    public final OverloadLoad load;

    final MyArray<VOverloadProduct> products;

    public final ValueBoolean isTaken;

    public VOverloadLoad(DealRef dealRef,
                         OverloadLoad load,
                         MyArray<VOverloadProduct> products,
                         boolean isTaken) {
        this.dealRef = dealRef;
        this.load = load;
        this.products = products;
        this.isTaken = new ValueBoolean(isTaken);
    }

    @NonNull
    public BigDecimal getTotalSum() {
        if (!isTaken.getValue()){
            return BigDecimal.ZERO;
        }
        return getProducts().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VOverloadProduct>() {
            @Override
            public BigDecimal apply(BigDecimal amount, VOverloadProduct val) {
                return amount.add(val.getTotalPrice());
            }
        });
    }

    void setWarehouseAndPrice(String warehouseId, String currencyId) {
        for (VOverloadProduct product : products) {
            product.setBalanceOfWarehouse(dealRef, warehouseId, currencyId);
        }

    }

    void bookQuantity() {
        for (VOverloadProduct product : products) {
            product.bookQuantity();
        }
    }

    void unBookQuantity() {
        for (VOverloadProduct product : products) {
            product.unBookQuantity();
        }
    }

    public MyArray<VOverloadProduct> getProducts() {
        return this.products.filter(new MyPredicate<VOverloadProduct>() {
            @Override
            public boolean apply(VOverloadProduct vOverloadProduct) {
                return vOverloadProduct.canUse() &&
                        vOverloadProduct.getQuantity().compareTo(BigDecimal.ZERO) != 0;
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(isTaken).toSuper();
    }
}
