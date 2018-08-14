package uz.greenwhite.smartup5_trade.m_stocktaking.variable;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Map;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.Quantity;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.WarehouseBalance;

public class VStocktakingProduct extends VariableLike implements Quantity {

    public final Product product;
    public final Card card;

    @Nullable
    private final WarehouseBalance balance;

    private final Map<String, BigDecimal> inputPrices;

    @Nullable
    public ProductBarcode barcodes;

    public final ValueBigDecimal quantity;
    public final ValueBigDecimal price;


    public VStocktakingProduct(Product product,
                               Card card,
                               @Nullable WarehouseBalance balance,
                               @Nullable Map<String, BigDecimal> inputPrices,
                               @Nullable ProductBarcode barcodes,
                               BigDecimal quantity,
                               BigDecimal price) {

        this.product = product;
        this.card = card;
        this.balance = balance;
        this.inputPrices = inputPrices;
        this.barcodes = barcodes;
        this.quantity = new ValueBigDecimal(20, 9);
        this.price = new ValueBigDecimal(20, 9);

        this.quantity.setValue(quantity);
        this.price.setValue(price);
    }

    public String getExpireDate() {
        return balance != null ? balance.expireDate : "";
    }

    public final void setLastPrice(String currencyId) {
        if (inputPrices == null) return;
        BigDecimal lastInputPrice = Util.nvl(inputPrices.get(currencyId), BigDecimal.ZERO);
        if (getAllBalance().compareTo(quantity.getQuantity()) < 0 &&
                lastInputPrice.compareTo(BigDecimal.ZERO) != 0) {
            price.setValue(lastInputPrice);
        }
    }

    public BigDecimal getAllBalance() {
        if (balance == null) {
            return BigDecimal.ZERO;
        }
        return balance.balance.add(balance.booked);
    }

    public BigDecimal getAvailBalance() {
        if (balance == null) {
            return BigDecimal.ZERO;
        }
        return balance.balance;
    }

    public boolean hasValue() {
        return quantity.nonZero();
    }

    @Override
    public BigDecimal getQuantity() {
        return quantity.getQuantity();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(quantity, price).toSuper();
    }

    public static final MyMapper<VStocktakingProduct, String> KEY_ADAPTER = new MyMapper<VStocktakingProduct, String>() {
        @Override
        public String apply(VStocktakingProduct vIncomingProduct) {
            return vIncomingProduct.product.id;
        }
    };

    @Override
    public ErrorResult getError() {
        ErrorResult errorResult = super.getError();
        if (errorResult.isError()) return errorResult;

        if ((getAllBalance().compareTo(quantity.getQuantity()) < 0 && price.isEmpty())) {
            return ErrorResult.make(DS.getString(R.string.stocktaking_error_1));
        }

        return ErrorResult.NONE;
    }

    @Override
    public String toString() {
        return product.name;
    }

    public static final MyMapper<VStocktakingProduct, Product> MAP_PRODUCT = new MyMapper<VStocktakingProduct, Product>() {
        @Override
        public Product apply(VStocktakingProduct vStocktakingProduct) {
            return vStocktakingProduct.product;
        }
    };
}
