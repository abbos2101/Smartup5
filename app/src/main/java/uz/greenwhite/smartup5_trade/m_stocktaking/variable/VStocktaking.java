package uz.greenwhite.smartup5_trade.m_stocktaking.variable;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.Stocktaking;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHeader;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingHolder;
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.StocktakingProduct;

public class VStocktaking extends VariableLike {

    public final StocktakingHolder holder;
    public final VStocktakingHeader vHeader;
    public final ValueArray<VStocktakingProduct> vProducts;

    public VStocktaking(StocktakingHolder holder, VStocktakingHeader vHeader, ValueArray<VStocktakingProduct> vProducts) {
        this.holder = holder;
        this.vHeader = vHeader;
        this.vProducts = vProducts;
    }

    public Stocktaking convertToValue() {
        Stocktaking value = holder.stocktaking;
        StocktakingHeader header = this.vHeader.toValue();

        ArrayList<StocktakingProduct> result = new ArrayList<>();
        for (VStocktakingProduct item : vProducts.getItems()) {
            if (item.hasValue()) {
                BigDecimal stocktakingQuantity = item.quantity.getQuantity().subtract(item.getAllBalance());
                result.add(new StocktakingProduct(
                        item.product.id,
                        item.card.code,
                        item.getExpireDate(),
                        item.getQuantity(),
                        stocktakingQuantity,
                        item.price.getText()
                ));
            }
        }

        return new Stocktaking(value.localId,
                holder.stocktaking.filialId,
                holder.stocktaking.warehouseId,
                header, MyArray.from(result));
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(vHeader, vProducts);
    }
}
