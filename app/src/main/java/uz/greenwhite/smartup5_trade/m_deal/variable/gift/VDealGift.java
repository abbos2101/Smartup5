package uz.greenwhite.smartup5_trade.m_deal.variable.gift;// 25.10.2016

import android.support.annotation.NonNull;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.Quantity;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class VDealGift extends VariableLike implements Quantity {

    public final Product product;
    public final String productUnitId;
    public final Card card = Card.ANY;
    public final WarehouseProductStock balanceOfWarehouse;
    public final String formKey;

    public final ValueBigDecimal quantity;

    public VDealGift(Product product,
                     String productUnitId,
                     WarehouseProductStock balanceOfWarehouse,
                     String formKey,
                     BigDecimal quantity) {
        this.product = product;
        this.productUnitId = productUnitId;
        this.balanceOfWarehouse = balanceOfWarehouse;
        this.formKey = formKey;

        this.quantity = new ValueBigDecimal(20, Math.min(product.measureScale, 6));
        this.quantity.setValue(quantity);
    }

    @Override
    public BigDecimal getQuantity() {
        return this.quantity.getQuantity();
    }

    public BigDecimal getBalanceOfWarehouse() {
        BigDecimal wa = balanceOfWarehouse.getAvailBalance(card, formKey);
        if (wa.signum() == -1) {
            return BigDecimal.ZERO;
        }
        return wa;
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }
        if (quantity.nonEmpty() && quantity.getValue().compareTo(getBalanceOfWarehouse()) > 0) {
            return ErrorResult.make(DS.getString(R.string.deal_gift_insufficient_gift));
        }
        return quantity.getError();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(quantity).toSuper();
    }

    public boolean hasValue() {
        return quantity.nonZero();
    }

    //----------------------------------------------------------------------------------------------
    @NonNull
    public CharSequence tWarehouseAvail() {
        return NumberUtil.formatMoney(getBalanceOfWarehouse());
    }

    //----------------------------------------------------------------------------------------------

    public static final MyMapper<VDealGift, String> KEY_ADAPTER = new MyMapper<VDealGift, String>() {
        @Override
        public String apply(VDealGift vDealGift) {
            return vDealGift.product.id;
        }
    };
}
