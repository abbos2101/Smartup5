package uz.greenwhite.smartup5_trade.m_incoming.variable;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class VIncomingProduct extends VariableLike {

    public final Product product;

    @Nullable
    public final ProductBarcode productBarcode;

    public final ValueArray<VIncomingProductDetail> productDetails;

    public VIncomingProduct(Product product,
                            @Nullable ProductBarcode productBarcode,
                            ValueArray<VIncomingProductDetail> productDetails) {

        this.product = product;
        this.productBarcode = productBarcode;
        this.productDetails = productDetails;
    }

    public void removeProductDetail(VIncomingProductDetail item) {
        productDetails.delete(item);
    }

    public void copy(VIncomingProductDetail item) {
        if (item == null) return;
        productDetails.append(new VIncomingProductDetail(null,
                null,
                item.quantity.getQuantity(),
                null,
                item.price.getQuantity()));
    }

    public BigDecimal getAllQuantity() {
        return productDetails.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VIncomingProductDetail>() {
            @Override
            public BigDecimal apply(BigDecimal result, VIncomingProductDetail val) {
                return result.add(val.quantity.getQuantity());
            }
        });
    }

    public boolean hasValue() {
        return productDetails.getItems().contains(new MyPredicate<VIncomingProductDetail>() {
            @Override
            public boolean apply(VIncomingProductDetail v) {
                return v.hasValue();
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return productDetails.getItems().toSuper();
    }

    @Override
    public String toString() {
        return product.name;
    }

    public static final MyMapper<VIncomingProduct, String> KEY_ADAPTER = new MyMapper<VIncomingProduct, String>() {
        @Override
        public String apply(VIncomingProduct vIncomingProduct) {
            return vIncomingProduct.product.id;
        }
    };

    public static final MyMapper<VIncomingProduct, Product> MAP_PRODUCT = new MyMapper<VIncomingProduct, Product>() {
        @Override
        public Product apply(VIncomingProduct vIncomingProduct) {
            return vIncomingProduct.product;
        }
    };
}
