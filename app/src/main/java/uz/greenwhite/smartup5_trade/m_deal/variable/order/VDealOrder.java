package uz.greenwhite.smartup5_trade.m_deal.variable.order;// 30.06.2016


import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.Quantity;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.common.Card;
import uz.greenwhite.smartup5_trade.m_deal.common.WarehouseProductStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.OrderRecom;
import uz.greenwhite.smartup5_trade.m_product.bean.ProductBarcode;
import uz.greenwhite.smartup5_trade.m_session.bean.PriceEditable;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductBalance;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;

public class VDealOrder extends VariableLike implements Quantity {

    private static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    public final Product product;
    public final String productUnitId;
    public final ProductPrice price;
    public final ProductBalance balance;
    public final PriceEditable priceEditable;
    public final Card card;
    public final boolean mmlProduct;
    public final RoundModel roundModel;
    public final ProductBarcode barcode;

    @Nullable
    public final WarehouseProductStock balanceOfWarehouse;

    private final OrderRecom recom;

    public final ValueBigDecimal box, quant, margin, realPrice;
    public SpinnerOption marginOption;


    public MyArray<Quantity> stocks = null;

    public final ValueString bonusId;

    public VDealOrder(Product product,
                      String productUnitId,
                      ProductPrice price,
                      ProductBalance balance,
                      PriceEditable priceEditable,
                      Card card,
                      boolean mmlProduct,
                      BigDecimal realPrice,
                      BigDecimal quantity,
                      BigDecimal margin,
                      @Nullable WarehouseProductStock balanceOfWarehouse,
                      RoundModel roundModel,
                      ProductBarcode barcode,
                      OrderRecom recom,
                      String bonusId) {
        AppError.checkNull(product, realPrice, roundModel);

        this.product = product;
        this.productUnitId = productUnitId;
        this.price = price;
        this.balance = balance;
        this.priceEditable = priceEditable;
        this.card = card;
        this.mmlProduct = mmlProduct;
        this.balanceOfWarehouse = balanceOfWarehouse;
        this.roundModel = roundModel;
        this.barcode = barcode;
        this.recom = recom;

        this.bonusId = new ValueString(10, bonusId);

        this.margin = new ValueBigDecimal(20, 6);
        this.realPrice = new ValueBigDecimal(20, 6);

        this.margin.setValue(Util.nvl(margin, BigDecimal.ZERO));
        this.realPrice.setValue(roundModel.fixAmount(realPrice));

        boolean isBox = false;
        boolean isQuant = false;
        BigDecimal boxPart = null;
        BigDecimal quantPart;

        if (product.isInputBox() && product.boxQuant.compareTo(BigDecimal.ZERO) != 0) {
            isBox = true;
            boxPart = product.getBoxPart(quantity);
            quantPart = product.getQuantPart(quantity);
            if (product.isInputQuant() || (quantPart != null && quantPart.compareTo(BigDecimal.ZERO) != 0)) {
                isQuant = true;
            }
        } else {
            isQuant = true;
            quantPart = quantity;
        }

        if (isBox) {
            this.box = new ValueBigDecimal(6, 0);
            this.box.setValue(boxPart);
        } else {
            this.box = null;
        }

        if (isQuant) {
            this.quant = new ValueBigDecimal(20, Math.min(product.measureScale, 6));
            this.quant.setValue(quantPart);
        } else {
            this.quant = null;
        }

        if (box != null && box.isZero()) {
            box.setText("");
        }

        if (quant != null && quant.isZero()) {
            quant.setText("");
        }
    }

    //----------------------------------------------------------------------------------------------

    public VDealOrder cloneOrder() {
        BigDecimal box = BigDecimal.ZERO;
        BigDecimal quant = BigDecimal.ZERO;

        if (this.box != null) {
            box = this.box.getQuantity();
        }
        if (this.quant != null) {
            quant = this.quant.getQuantity();
        }
        VDealOrder result = new VDealOrder(
                this.product,
                this.productUnitId,
                this.price,
                this.balance,
                this.priceEditable,
                this.card,
                this.mmlProduct,
                this.realPrice.getQuantity(),
                this.getQuantity(),
                this.margin.getQuantity(),
                this.balanceOfWarehouse,
                this.roundModel,
                this.barcode,
                this.recom,
                this.bonusId.getText()
        );

        if (result.box != null) result.box.setValue(box);
        if (result.quant != null) result.quant.setValue(quant);

        return result;
    }

    public void copyIn(VDealOrder order) {
        if (order.box != null) {
            this.box.setValue(order.box.getValue());
        }
        if (order.quant != null) {
            this.quant.setValue(order.quant.getValue());
        }

        if (order.margin != null) {
            this.margin.setValue(order.margin.getValue());
        }

        if (order.realPrice != null) {
            this.realPrice.setValue(order.realPrice.getValue());
        }
    }

    //----------------------------------------------------------------------------------------------

    public CharSequence tvTitleInfo() {
        ShortHtml html = UI.html().v(product.name).v(" ");
        if (this.margin.nonZero()) {
            String discount = this.margin.isZero() ? null : this.margin.getText() + "%";
            if (this.margin.getValue().compareTo(BigDecimal.ZERO) > 0) {
                discount = "+" + discount;
            }
            html.b().v("  (").v(discount).v(")").b();
        }
        return html.html();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public BigDecimal getQuantity() {
        return product.getBoxQuant(box == null ? BigDecimal.ZERO : box.getQuantity(),
                quant == null ? BigDecimal.ZERO : quant.getQuantity());
    }

    public void setQuantity(BigDecimal quantity) {
        if (quantity == null) {
            quantity = BigDecimal.ZERO;
        }
        if (box != null && quant != null) {
            box.setValue(product.getBoxPart(quantity));
            quant.setValue(product.getQuantPart(quantity));
        } else if (box != null) {
            box.setValue(product.getBoxPart(quantity));
        } else {
            quant.setValue(quantity);
        }
        if (box != null && box.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            box.setText("");
        }

        if (quant != null && quant.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            quant.setText("");
        }
    }

    //----------------------------------------------------------------------------------------------

    public BigDecimal getBalanceOfWarehouse() {
        BigDecimal wa = balanceOfWarehouse != null ? balanceOfWarehouse.getAvailBalance(card, price.priceTypeId) : BigDecimal.ZERO;
        if (wa.signum() == -1) {
            return BigDecimal.ZERO;
        }
        return wa;
    }

    public CharSequence getBalanceOfWarehouseByBox() {
        BigDecimal balance = getBalanceOfWarehouse();
        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            boolean isBox = false;
            boolean isQuant = false;
            BigDecimal boxPart = null;
            BigDecimal quantPart;

            if (product.isInputBox() && product.boxQuant.compareTo(BigDecimal.ZERO) != 0) {
                isBox = true;
                boxPart = product.getBoxPart(balance);
                quantPart = product.getQuantPart(balance);
                if (product.isInputQuant() || (quantPart != null && quantPart.compareTo(BigDecimal.ZERO) != 0)) {
                    isQuant = true;
                }
            } else {
                isQuant = true;
                quantPart = balance;
            }
            ShortHtml html = UI.html();
            if (isBox && boxPart.compareTo(BigDecimal.ZERO) != 0) {
                html.v(NumberUtil.formatMoney(boxPart)).v(" ").v(product.boxName);
            }

            if (isQuant && quantPart.compareTo(BigDecimal.ZERO) != 0) {
                if (isBox && boxPart.compareTo(BigDecimal.ZERO) != 0) {
                    html.v(", ");
                }
                html.v(NumberUtil.formatMoney(quantPart)).v(" ").v(product.measureName);
            }
            return html.html();
        }
        return "";
    }

    //----------------------------------------------------------------------------------------------

    public BigDecimal getTotalOrderPrice() {
        return roundModel.fixAmount(getQuantity().multiply(realPrice.getQuantity()));
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    public BigDecimal getProductPriceWithMargin() {
        if (this.margin.nonZero()) {
            BigDecimal priceMargin = this.margin.getQuantity().multiply(this.realPrice.getQuantity()).divide(HUNDRED);
            return roundModel.fixAmount(this.realPrice.getQuantity().add(priceMargin));
        }
        return roundModel.fixAmount(this.realPrice.getQuantity());
    }

    @SuppressWarnings({"BigDecimalMethodWithoutRoundingCalled", "UnnecessaryLocalVariable"})
    public BigDecimal getTotalPriceWithMargin() {
        if (margin.nonZero()) {
            BigDecimal orderPrice = getTotalOrderPrice();
            return roundModel.fixAmount(margin.getQuantity().multiply(orderPrice).divide(HUNDRED));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal orderPrice = getTotalOrderPrice();
        BigDecimal marginPrice = getTotalPriceWithMargin();
        return roundModel.fixAmount(orderPrice.add(marginPrice));
    }

    //----------------------------------------------------------------------------------------------

    public boolean canGenerateRecom() {
        return recom != null && stocks != null && stocks.reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, Quantity>() {
            @Override
            public BigDecimal apply(BigDecimal result, Quantity quantity) {
                return result.add(quantity.getQuantity());
            }
        }).compareTo(BigDecimal.ZERO) != 0;
    }

    public BigDecimal getRecomOrderToBigDecimal() {
        if (recom != null && stocks != null) {
            BigDecimal allStockQuant = stocks.reduce(BigDecimal.ZERO, new MyReducer<BigDecimal, Quantity>() {
                @Override
                public BigDecimal apply(BigDecimal result, Quantity quantity) {
                    return result.add(quantity.getQuantity());
                }
            });
            if (allStockQuant != null && allStockQuant.compareTo(BigDecimal.ZERO) != 0) {
                String calcRecom = recom.calcRecom(allStockQuant.doubleValue());
                try {
                    if (!TextUtils.isEmpty(calcRecom)) return new BigDecimal(calcRecom);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    ErrorUtil.saveThrowable(e);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public String getRecomOrder() {
        BigDecimal recomOrderToBigDecimal = getRecomOrderToBigDecimal();
        if (recomOrderToBigDecimal.compareTo(BigDecimal.ZERO) == 0) return "";
        return recomOrderToBigDecimal.toPlainString();
    }

    @Nullable
    public Tuple2 getRecomOrderByBox() {
        String recomOrder = getRecomOrder();
        if (TextUtils.isEmpty(recomOrder)) return null;
        BigDecimal recom = null;
        try {
            recom = new BigDecimal(recomOrder);
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            return null;
        }

        if (recom.compareTo(BigDecimal.ZERO) != 0) {
            boolean isBox = false;
            boolean isQuant = false;
            BigDecimal boxPart = null;
            BigDecimal quantPart;

            if (product.isInputBox() && product.boxQuant.compareTo(BigDecimal.ZERO) != 0) {
                isBox = true;
                boxPart = product.getBoxPart(recom);
                quantPart = product.getQuantPart(recom);
                if (product.isInputQuant() || (quantPart != null && quantPart.compareTo(BigDecimal.ZERO) != 0)) {
                    isQuant = true;
                }
            } else {
                isQuant = true;
                quantPart = recom;
            }
            return new Tuple2(isQuant ? quantPart : null, isBox ? boxPart : null);
        }
        return null;
    }

    @Nullable
    public CharSequence getRecomOrderByBoxText() {
        Tuple2 recom = getRecomOrderByBox();
        if (recom == null || (recom.first == null && recom.second == null)) return null;
        BigDecimal quantPart = (BigDecimal) recom.first;
        BigDecimal boxPart = (BigDecimal) recom.second;

        ShortHtml html = UI.html();
        if (boxPart != null && boxPart.compareTo(BigDecimal.ZERO) != 0) {
            html.v(NumberUtil.formatMoney(boxPart)).v(" ").v(product.boxName);
        }

        if (quantPart != null && quantPart.compareTo(BigDecimal.ZERO) != 0) {
            if (boxPart != null && boxPart.compareTo(BigDecimal.ZERO) != 0) {
                html.v(", ");
            }
            html.v(NumberUtil.formatMoney(quantPart)).v(" ").v(product.measureName);
        }
        return html.html();
    }

    public boolean hasValue() {
        return (box != null && box.nonZero()) || (quant != null && quant.nonZero());
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(box, quant);
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }
        if (getQuantity().compareTo(getBalanceOfWarehouse()) > 0) {
            return ErrorResult.make(DS.getString(R.string.deal_insufficient_product));
        }

        if (priceEditable.editable) {
            BigDecimal min = roundModel.fixAmount(priceEditable.getMinimumAmount(price.price));
            BigDecimal max = roundModel.fixAmount(priceEditable.getMaximumAmount(price.price));

            BigDecimal rPrice = roundModel.fixAmount(realPrice.getQuantity());
            if (!(min.compareTo(rPrice) <= 0 && rPrice.compareTo(max) <= 0)) {
                return ErrorResult.make(DS.getString(R.string.price_edit_min_max, min.toPlainString(), max.toPlainString()));
            }
        }
        return ErrorResult.NONE;
    }

    @Override
    public String toString() {
        return product.name;
    }

    public static Tuple2 getKey(String productId, String cardCode) {
        return new Tuple2(productId, cardCode);
    }

    public static final MyMapper<VDealOrder, Tuple2> KEY_ADAPTER = new MyMapper<VDealOrder, Tuple2>() {
        @Override
        public Tuple2 apply(VDealOrder val) {
            return getKey(val.product.id, val.price.cardCode);
        }
    };
    public static final MyMapper<VDealOrder, String> MAP_TO_PRODUCT_ID = new MyMapper<VDealOrder, String>() {
        @Override
        public String apply(VDealOrder val) {
            return val.product.id;
        }
    };

}
