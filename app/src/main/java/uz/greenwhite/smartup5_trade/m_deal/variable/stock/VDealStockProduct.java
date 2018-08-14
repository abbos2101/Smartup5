package uz.greenwhite.smartup5_trade.m_deal.variable.stock;// 29.09.2016

import java.math.BigDecimal;
import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class VDealStockProduct extends VariableLike {

    private int stockOrderNo = 0;
    public final Product product;
    final ValueArray<VDealStock> stocksRows;

    public VDealStockProduct(Product product, ValueArray<VDealStock> stocks) {
        AppError.checkNull(product);
        this.stockOrderNo = stocks.getItems().size();
        this.product = product;
        this.stocksRows = stocks;
    }

    public BigDecimal getAllStockQuantity() {
        return stocksRows.getItems().reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, VDealStock>() {
            @Override
            public BigDecimal apply(BigDecimal bigDecimal, VDealStock vDealStock) {
                return bigDecimal.add(vDealStock.stock.getQuantity());
            }
        });
    }

    public MyArray<VDealStock> getStocks() {
        return stocksRows.getItems().sort(new Comparator<VDealStock>() {
            @Override
            public int compare(VDealStock l, VDealStock r) {
                return MyPredicate.compare(l.orderNo, r.orderNo);
            }
        });
    }

    public void appendNewProductStock() {
        stocksRows.append(new VDealStock(product, null, null, ++stockOrderNo));
    }

    public void deleteStock(VDealStock vDealStock) {
        stocksRows.delete(vDealStock);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected MyArray<Variable> gatherVariables() {
        return stocksRows.getItems().toSuper();
    }

    public boolean hasValue() {
        for (VDealStock val : stocksRows.getItems()) {
            if (val.hasValue()) return true;
        }
        return false;
    }

    public static final MyMapper<VDealStockProduct, String> KEY_ADAPTER = new MyMapper<VDealStockProduct, String>() {
        @Override
        public String apply(VDealStockProduct vDealStockProduct) {
            return vDealStockProduct.product.id;
        }
    };
}
