package uz.greenwhite.smartup5_trade.m_deal.variable.overload;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.variable.Quantity;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.WP;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceType;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.overload.OverloadProduct;

public class VOverloadProduct extends VariableLike implements Quantity {

    public final Product product;
    public final String productUnitId;
    public final OverloadProduct loadProduct;

    public final ValueString warehouseId;
    public final ValueBigDecimal quantity;

    public final Card card = Card.ANY;
    public final String overloadKey;

    @Nullable
    private WarehouseProductStock balanceOfWarehouse;
    @Nullable
    private ProductPrice productPrice;
    @Nullable
    private PriceType priceType;

    public VOverloadProduct(Product product,
                            String productUnitId,
                            OverloadProduct loadProduct,
                            String warehouseId,
                            BigDecimal quantity,
                            @Nullable WarehouseProductStock balanceOfWarehouse,
                            @Nullable ProductPrice productPrice,
                            @Nullable PriceType priceType,
                            String overloadKey) {
        this.product = product;
        this.productUnitId = productUnitId;
        this.loadProduct = loadProduct;
        this.warehouseId = new ValueString(100, warehouseId);

        this.quantity = new ValueBigDecimal(10, 9);
        this.quantity.setValue(quantity);

        this.balanceOfWarehouse = balanceOfWarehouse;
        this.productPrice = productPrice;
        this.priceType = priceType;

        this.overloadKey = overloadKey;

    }


    void setBalanceOfWarehouse(final DealRef dealRef, final String warehouseId, final String currencyId) {
        this.warehouseId.setValue("");
        this.balanceOfWarehouse = null;
        this.productPrice = null;

        MyArray<ProductPrice> productPrices = dealRef.getProductPrices();

        final MyArray<PriceType> priceTypes = dealRef.makeWP()
                .map(new MyMapper<WP, PriceType>() {
                    @Override
                    public PriceType apply(WP wp) {
                        if (!warehouseId.equals(wp.warehouseId)) {
                            return null;
                        }
                        PriceType priceType = dealRef.getPriceType(wp.priceTypeId);
                        if (priceType == null || !priceType.currencyId.equals(currencyId)) {
                            return null;
                        }

                        return priceType;
                    }
                }).filterNotNull();

        ProductPrice productPrice = productPrices.findFirst(new MyPredicate<ProductPrice>() {
            @Override
            public boolean apply(ProductPrice val) {
                return priceTypes.contains(val.priceTypeId, PriceType.KEY_ADAPTER) &&
                        val.productId.equals(product.id);
            }
        });

        if (productPrice == null || productPrice.price.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        WarehouseProductStock balance = dealRef.balance.getBalance(warehouseId, product.id);
        if (balance == null || !balance.hasBalance(card)) {
            return;
        }

        this.warehouseId.setValue(warehouseId);
        this.balanceOfWarehouse = balance;
        this.productPrice = productPrice;
        this.priceType = priceTypes.find(productPrice.priceTypeId, PriceType.KEY_ADAPTER);
    }

    void bookQuantity() {
        if (balanceOfWarehouse != null) {
            balanceOfWarehouse.bookQuantity(card, overloadKey, getQuantity());
        }
    }

    void unBookQuantity() {
        if (balanceOfWarehouse != null) {
            balanceOfWarehouse.bookQuantity(card, overloadKey, BigDecimal.ZERO);
        }
    }

    public boolean canUse() {
        return warehouseId.nonEmpty() &&
                productPrice != null &&
                priceType != null &&
                balanceOfWarehouse != null;
    }

    @NonNull
    public BigDecimal getTotalPrice() {
        return getProductPrice().price.multiply(getQuantity());
    }

    @NonNull
    public ProductPrice getProductPrice() {
        AppError.checkNull(this.productPrice);
        return this.productPrice;
    }

    @NonNull
    public PriceType getPriceType() {
        AppError.checkNull(this.priceType);
        return this.priceType;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    public WarehouseProductStock getBalanceOfWarehouse() {
        AppError.checkNull(this.balanceOfWarehouse);
        return this.balanceOfWarehouse;
    }

    @Override
    public BigDecimal getQuantity() {
        if (balanceOfWarehouse == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance = balanceOfWarehouse.getAvailBalance(card, overloadKey);
        if (balance.signum() == -1 || balance.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal quantity = this.quantity.getQuantity();

        BigDecimal result = balance.subtract(quantity);
        if (result.compareTo(BigDecimal.ZERO) >= 0) {
            return quantity;
        } else {
            return balance;
        }
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.emptyArray();
    }
}
