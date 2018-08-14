package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.PopupBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.OnTryCatchCallback;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.calculator.CalcKey;
import uz.greenwhite.smartup5_trade.common.calculator.CalcProductDialog;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;

public class OrderCalcDialog extends CalcProductDialog<VDealOrder> {

    public static final String TAG = "order-calc";

    public static void show(Fragment fragment, ArgOrder arg) {
        OrderCalcDialog dialog = Mold.parcelableArgumentNewInstance(OrderCalcDialog.class, arg, ArgOutlet.UZUM_ADAPTER);
        dialog.show(fragment.getFragmentManager(), TAG);
    }

    public ArgOrder getArgOrder() {
        return Mold.parcelableArgument(this, ArgOrder.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;
    private ModelChange onChange;
    private ArgOrder arg;
    private VDealOrderForm form;

    @NonNull
    @Override
    protected View onCreateHeaderView() {
        this.vsRoot = new ViewSetup(getActivity(), R.layout.deal_order_calc);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        arg = getArgOrder();

        ErrorUtil.tryCatch(new OnTryCatchCallback() {
            @Override
            public void onTry() throws Exception {
                OrderFragment content = Mold.getContentFragment(getActivity());
                form = DealUtil.getDealForm(content);

                ArgOrder arg = getArgOrder();
                Tuple2 key = VDealOrder.getKey(arg.productId, arg.cardCode);
                oldValue = form.orders.getItems().find(key, VDealOrder.KEY_ADAPTER);
                newValue = oldValue.cloneOrder();

                TextView marginPercent = vsRoot.textView(R.id.tv_order_price);
                marginPercent.setPaintFlags(marginPercent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                marginPercent.setTextColor(DS.getColor(R.color.red));

                vsRoot.textView(R.id.tv_product_name).setText(newValue.product.name);
                vsRoot.textView(R.id.tv_card_code).setText(newValue.price.cardCode);
                vsRoot.textView(R.id.tv_warehouse_avail).setText(NumberUtil.formatMoney(newValue.getBalanceOfWarehouse()));

                CharSequence recomOrder = newValue.getRecomOrderByBoxText();
                boolean hasRecom = !TextUtils.isEmpty(recomOrder);
                if (hasRecom) {
                    vsRoot.textView(R.id.tv_order_recom).setText(recomOrder);
                }

                boolean orderHasQuant = newValue.quant != null;
                vsRoot.id(R.id.tv_quant_order).setEnabled(orderHasQuant);
                vsRoot.id(R.id.tv_box_order).setEnabled(!orderHasQuant);
                vsRoot.id(R.id.iv_price_edit).setEnabled(false);

                View.OnClickListener enableBoxQuantListener = getEnableBoxQuantListener();
                vsRoot.id(R.id.ll_quant_order).setOnClickListener(enableBoxQuantListener);
                vsRoot.id(R.id.ll_box_order).setOnClickListener(enableBoxQuantListener);
                vsRoot.id(R.id.ll_product_price).setOnClickListener(enableBoxQuantListener);

                vsRoot.id(R.id.ll_product_price).setClickable(newValue.priceEditable.editable);
                vsRoot.id(R.id.iv_price_edit).setVisibility(newValue.priceEditable.editable ? View.VISIBLE : View.GONE);

                boolean hasMargin = newValue.marginOption != null &&
                        ((BigDecimal) newValue.marginOption.tag).compareTo(BigDecimal.ZERO) != 0;

                vsRoot.id(R.id.ll_margin).setVisibility(hasMargin ? View.VISIBLE : View.INVISIBLE);
                vsRoot.id(R.id.ll_recom_calc).setVisibility(hasRecom ? View.VISIBLE : View.INVISIBLE);
                if (hasMargin) {
                    vsRoot.id(R.id.ll_margin).setOnClickListener(getMarginClickListener());
                }

                if (hasRecom) {
                    Tuple2 recom = newValue.getRecomOrderByBox();
                    if (recom == null || (recom.first == null && recom.second == null)) return;

                    if (newValue.quant != null &&
                            newValue.product.isInputQuant() &&
                            recom.first != null &&
                            ((BigDecimal) recom.first).compareTo(BigDecimal.ZERO) != 0) {
                        newValue.quant.setValue((BigDecimal) recom.first);
                    }

                    if (newValue.box != null &&
                            newValue.product.isInputBox() &&
                            recom.second != null &&
                            ((BigDecimal) recom.second).compareTo(BigDecimal.ZERO) != 0) {
                        newValue.box.setValue(newValue.product.getBoxPart((BigDecimal) recom.second));
                    }
                }

                onChange = getOnChangeOrder();
                onChange.onChange();
            }

            @Override
            public void onCatch(Exception ex) throws Exception {
                dismiss();
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(ex).message);
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    @Nullable
    @Override
    protected ValueBigDecimal getEnableValue() {
        if (newValue == null) return null;

        if (vsRoot.id(R.id.tv_quant_order).isEnabled()) {
            return newValue.quant;

        } else if (vsRoot.id(R.id.tv_box_order).isEnabled()) {
            return newValue.box;

        } else if (vsRoot.id(R.id.iv_price_edit).isEnabled()) {
            return newValue.realPrice;
        }
        return null;
    }

    private boolean isPriceEdit() {
        return vsRoot.id(R.id.iv_price_edit).isEnabled();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onKeyListener(@NonNull CalcKey key) {
        if (arg == null) arg = getArgOrder();
        Setting setting = arg.getSetting();
        if (!setting.deal.allowDealDraft) {
            ErrorResult error = newValue.getError();
            if (error.isError()) {
                if (key.isOk() || (!isPriceEdit() && !key.isRemove() && !key.isSubtractOne())) {
                    return;
                }
            }
        }

        switch (key) {
            case KEY_OK:
                boolean bookedQuant = false;
                if (setting.deal.allowDealDraft) {
                    bookedQuant = true;
                    if (newValue.balanceOfWarehouse != null) {
                        newValue.balanceOfWarehouse.bookQuantity(newValue.card,
                                newValue.price.priceTypeId, newValue.getQuantity());
                    }
                } else {
                    if (newValue.balanceOfWarehouse != null) {
                        bookedQuant = newValue.balanceOfWarehouse.tryBookQuantity(newValue.card,
                                newValue.price.priceTypeId, newValue.getQuantity());
                    }
                }

                if (bookedQuant) {

                    oldValue.copyIn(newValue);

                    MoldContentFragment content = Mold.getContentFragment(getActivity());
                    if (content instanceof OrderFragment) {
                        OrderFragment f = (OrderFragment) content;
                        f.reloadContent();
                        f.onChange.onChange();
                    }
                    dismiss();
                }
                break;
            default:
                super.onKeyListener(key);
        }

        if (onChange != null) onChange.onChange();
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    private View.OnClickListener getEnableBoxQuantListener() {
        return new View.OnClickListener() {

            private void setEnableValue(@IdRes int enable, @IdRes int d1, @IdRes int d2) {
                View v1 = vsRoot.id(enable);
                View v2 = vsRoot.id(d1);
                View v3 = vsRoot.id(d2);

                if (!v1.isEnabled()) v1.setEnabled(true);
                if (v2.isEnabled()) v2.setEnabled(false);
                if (v3.isEnabled()) v3.setEnabled(false);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ll_box_order:
                        setEnableValue(R.id.tv_box_order, R.id.tv_quant_order, R.id.iv_price_edit);
                        return;
                    case R.id.ll_quant_order:
                        setEnableValue(R.id.tv_quant_order, R.id.tv_box_order, R.id.iv_price_edit);
                        return;
                    case R.id.ll_product_price:
                        setEnableValue(R.id.iv_price_edit, R.id.tv_quant_order, R.id.tv_box_order);
                        return;
                    default:
                        throw AppError.Unsupported();
                }
            }
        };
    }

    @NonNull
    private View.OnClickListener getMarginClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UI.popup().option(form.discount.options, new PopupBuilder.CommandFacade<SpinnerOption>() {
                    @NonNull
                    @Override
                    public CharSequence getName(SpinnerOption val) {
                        return val.name;
                    }

                    @Override
                    public void apply(SpinnerOption val) {
                        BigDecimal percent = (BigDecimal) val.tag;
                        newValue.margin.setValue(percent);
                        newValue.marginOption = val;
                        if (onChange != null) onChange.onChange();
                    }
                }).show(view);
            }
        };
    }

    //----------------------------------------------------------------------------------------------

    @NonNull
    private ModelChange getOnChangeOrder() {
        return new ModelChange() {
            @Override
            public void onChange() {
                CharSequence name = "";
                if (newValue.marginOption != null && ((BigDecimal) newValue.marginOption.tag)
                        .compareTo(BigDecimal.ZERO) != 0) {

                    boolean zero = ((BigDecimal) newValue.marginOption.tag).compareTo(BigDecimal.ZERO) == 0;
                    name = zero ? getString(R.string.deal_discount_mark_up) : newValue.marginOption.name;
                }
                vsRoot.textView(R.id.tv_margin).setText(name);

                //----------------------------------------------------------------------------------

                vsRoot.textView(R.id.tv_order_total).setText(Utils.formatMoney(newValue.getQuantity()));
                vsRoot.textView(R.id.tv_total_sum).setText(DS.getString(R.string.deal_sum_calc,
                        NumberUtil.formatMoney(newValue.getTotalPrice())));

                String realPrice = NumberUtil.formatMoney(newValue.realPrice.getQuantity());
                String marginPrice = NumberUtil.formatMoney(newValue.getProductPriceWithMargin());

                TextView tvRP = vsRoot.textView(R.id.tv_order_price);
                TextView tvMP = vsRoot.textView(R.id.tv_margin_order_price);

                tvRP.setText(realPrice);
                tvMP.setText(marginPrice);

                tvRP.setVisibility(realPrice.equals(marginPrice) ? View.GONE : View.VISIBLE);
                tvMP.setVisibility(TextUtils.isEmpty(marginPrice) ? View.GONE : View.VISIBLE);

                //----------------------------------------------------------------------------------

                vsRoot.id(R.id.v_center_border).setVisibility(View.VISIBLE);

                TextView tvBox = vsRoot.textView(R.id.tv_box_order);
                if (newValue.box != null) {
                    vsRoot.textView(R.id.tv_measure_box).setText(newValue.product.boxName);

                    BigDecimal quantity = newValue.box.getQuantity();
                    String result = Utils.formatMoney(quantity);

                    tvBox.setText(result);

                    vsRoot.id(R.id.ll_box_order).setVisibility(View.VISIBLE);
                } else {
                    vsRoot.id(R.id.ll_box_order).setVisibility(View.GONE);
                    vsRoot.id(R.id.v_center_border).setVisibility(View.GONE);
                }

                //----------------------------------------------------------------------------------

                TextView tvQuant = vsRoot.textView(R.id.tv_quant_order);
                if (newValue.quant != null) {
                    vsRoot.textView(R.id.tv_measure_quant).setText(newValue.product.measureName);

                    BigDecimal quantity = newValue.quant.getQuantity();
                    String result = Utils.formatMoney(quantity);

                    tvQuant.setText(result);

                    vsRoot.id(R.id.ll_quant_order).setVisibility(View.VISIBLE);
                } else {
                    vsRoot.id(R.id.ll_quant_order).setVisibility(View.GONE);
                    vsRoot.id(R.id.v_center_border).setVisibility(View.GONE);
                }

                //----------------------------------------------------------------------------------

                ErrorResult error = newValue.getError();
                vsRoot.textView(R.id.tv_error).setVisibility(error.isError() ? View.VISIBLE : View.GONE);
                vsRoot.textView(R.id.tv_error).setText(Util.nvl(error.getErrorMessage()));
            }
        };
    }
}
