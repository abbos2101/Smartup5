package uz.greenwhite.smartup5_trade.m_shipped.ui;// 09.09.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_shipped.ShippedUtil;
import uz.greenwhite.smartup5_trade.m_shipped.filter.SOrderFilter;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrder;
import uz.greenwhite.smartup5_trade.m_shipped.variable.order.VSDealOrderForm;

public class SOrderFragment extends MoldContentRecyclerFragment<VSDealOrder> {

    ModelChange OnChangeItem;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final VSDealOrderForm form = ShippedUtil.getDealForm(this);

        ViewSetup vsHeader = setHeader(R.layout.sdeal_order_header);
        final TextView cTotalSum = vsHeader.textView(R.id.total_amount);
        final TextView cTotalQuant = vsHeader.textView(R.id.total_quant);

        OnChangeItem = new ModelChange() {
            @Override
            public void onChange() {
                cTotalSum.setText(NumberUtil.formatMoney(form.getTotalSum()));
                cTotalQuant.setText(NumberUtil.formatMoney(form.getTotalQuantity()));
            }
        };

        OnChangeItem.onChange();

        setListItems(form.orders.getItems());

        SDealData data = Mold.getData(getActivity());
        SOrderFilter filter = data.filter.findOrder(form.code);
        if (filter != null) {
            setListFilter(filter.getPredicate());
        }
    }

    @Override
    public void reloadContent() {
        VSDealOrderForm form = ShippedUtil.getDealForm(this);
        setListItems(form.orders.getItems());
    }

    //TODO
    public void returnAllOrder() {
        VSDealOrderForm form = ShippedUtil.getDealForm(this);
        for (VSDealOrder o : form.orders.getItems()) {
            if (o.returnBox != null) o.returnBox.setText("");
            o.returnQuant.setValue(o.order.originQuant);
        }
        reloadContent();
    }

    //TODO
    public void cancelReturnOrder() {
        VSDealOrderForm form = ShippedUtil.getDealForm(this);
        for (VSDealOrder o : form.orders.getItems()) {
            o.returnBox.setText("");
            o.returnQuant.setText("");
        }
        reloadContent();
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new SOrderTuningFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.sdeal_order_row;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VSDealOrder item) {

        vsItem.textView(R.id.tv_title).setText(item.product.name);
        vsItem.textView(R.id.tv_card_code).setText(item.order.cardCode);
        vsItem.textView(R.id.tv_price).setText(getString(R.string.sdeal_price,
                NumberUtil.formatMoney(item.order.soldPrice)));
        vsItem.textView(R.id.tv_discount).setText(item.getDiscount());
        vsItem.textView(R.id.tv_delivery).setText(NumberUtil.formatMoney(item.order.originQuant));
        //vsItem.textView(R.id.tv_avail).setText(getString(R.string.sdeal_avail,
        //NumberUtil.formatMoney(item.getAvailQuant())));

        final ModelChange modelChange = new ModelChange() {
            @Override
            public void onChange() {
                BigDecimal quantity = item.getQuantity();
                BigDecimal amount = item.getTotalSum();

                vsItem.textView(R.id.tv_total).setText(NumberUtil.formatMoney(quantity));

                vsItem.textView(R.id.tv_order_total).setText(getString(R.string.sdeal_total_sum,
                        NumberUtil.formatMoney(amount)));

                ErrorResult error = item.getError();
                boolean hasError = error.isError();
                TextView tvError = vsItem.textView(R.id.error);
                tvError.setVisibility(hasError ? View.VISIBLE : View.GONE);
                tvError.setText(error.getErrorMessage());
            }
        };

        final EditText etBox = vsItem.editText(R.id.et_box);
        final EditText etQuant = vsItem.editText(R.id.et_quant);


        final ModelChange onChange = new ModelChange() {

            public void prepareBox(ValueBigDecimal box) {
                if (box != null) {
                    etBox.setVisibility(View.VISIBLE);

                    etBox.setHint(item.product.boxName);

                    UI.bind(etBox, box);
                    UI.getModel(etBox).add(modelChange).add(OnChangeItem);

                } else {
                    etBox.setVisibility(View.GONE);
                }
            }

            public void prepareQuant(ValueBigDecimal quant) {
                if (quant != null) {
                    etQuant.setVisibility(View.VISIBLE);

                    etQuant.setHint(item.product.measureName);
                    UI.bind(etQuant, quant);
                    UI.getModel(etQuant).add(modelChange).add(OnChangeItem);

                } else {
                    etQuant.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChange() {
                SpinnerOption value = item.spType.getValue();
                if ("+".equals(value.code)) {
                    prepareBox(item.deliverBox);
                    prepareQuant(item.deliverQuant);
                } else {
                    prepareBox(item.returnBox);
                    prepareQuant(item.returnQuant);
                }
            }
        };

        onChange.onChange();
        modelChange.onChange();

        ModelChange spinnerChange = new ModelChange() {
            @Override
            public void onChange() {
                String box = etBox.getText().toString();
                String quant = etQuant.getText().toString();

                etBox.setText("");
                etQuant.setText("");

                onChange.onChange();

                etBox.setText(box);
                etQuant.setText(quant);

                modelChange.onChange();
            }
        };

        vsItem.bind(R.id.sp_type, item.spType);
        vsItem.model(R.id.sp_type).add(spinnerChange);
        vsItem.id(R.id.sp_type).setVisibility(View.GONE);
    }
}
