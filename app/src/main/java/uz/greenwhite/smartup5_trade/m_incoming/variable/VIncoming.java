package uz.greenwhite.smartup5_trade.m_incoming.variable;

import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_incoming.bean.Incoming;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHeader;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingHolder;
import uz.greenwhite.smartup5_trade.m_incoming.bean.IncomingProduct;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class VIncoming extends VariableLike {

    public final IncomingHolder holder;
    public final VIncomingHeader vHeader;
    public final ValueArray<VIncomingProduct> vProducts;

    public final MyArray<Product> products;
    public final MyArray<ProductBarcode> barcodes;

    public VIncoming(IncomingHolder holder,
                     VIncomingHeader vHeader,
                     ValueArray<VIncomingProduct> vProducts,
                     MyArray<Product> products,
                     MyArray<ProductBarcode> barcodes) {
        this.holder = holder;
        this.vHeader = vHeader;
        this.vProducts = vProducts;
        this.products = products;
        this.barcodes = barcodes;
    }

    public void addProduct(Product product) {
        if (product == null) return;
        ProductBarcode barcode = barcodes.find(product.id, ProductBarcode.KEY_ADAPTER);
        VIncomingProductDetail detail = new VIncomingProductDetail(null, null, null, null, null);
        vProducts.append(new VIncomingProduct(product, barcode, new ValueArray<>(MyArray.from(detail))));
    }

    public void removeProduct(VIncomingProduct vIncomingProduct) {
        if (vIncomingProduct == null) return;
        vProducts.delete(vIncomingProduct);
    }

    public Incoming convertToIncoming() {
        Incoming incoming = holder.incoming;
        IncomingHeader header = this.vHeader.toValue();

        ArrayList<IncomingProduct> result = new ArrayList<>();
        for (VIncomingProduct item : vProducts.getItems()) {
            for (VIncomingProductDetail d : item.productDetails.getItems()) {
                if (d.hasValue()) {
                    result.add(new IncomingProduct(
                            item.product.id,
                            d.cardNumber.getText(),
                            d.manufacturePrice.getText(),
                            d.quantity.getQuantity(),
                            d.expireDate.getText(),
                            d.price.getText()
                    ));
                }
            }
        }

        return new Incoming(incoming.localId,
                holder.incoming.filialId,
                holder.incoming.warehouseId,
                header, MyArray.from(result));
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(vHeader, vProducts);
    }
}
