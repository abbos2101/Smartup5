package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.view_setup.DialogBuilder;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealService;
import uz.greenwhite.smartup5_trade.m_deal.variable.service.VDealServiceForm;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.ui.ProductInfoFragment;

public class ServiceFragment extends DealFormRecyclerFragment<VDealService> {

    private Drawable moreIcon = null;
    private VDealServiceForm form;
    private DealData data;
    private ModelChange onChange;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.service);
        moreIcon = UI.changeDrawableColor(getActivity(),
                R.drawable.ic_more_vert_black_24dp, R.color.dark_silver);
        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VDealService item, String text) {
                boolean contains = CharSequenceUtil.containsIgnoreCase(item.product.name, text);
                if (!contains) {
                    contains = CharSequenceUtil.containsIgnoreCase(item.product.code, text);
                }
                return contains;
            }
        });

        final ViewSetup vsHeader = setHeader(R.layout.deal_service_header);

        this.data = Mold.getData(getActivity());
        this.form = DealUtil.getDealForm(this);

        reloadContent();

        onChange = new ModelChange() {
            @Override
            public void onChange() {
                vsHeader.textView(R.id.tv_total_sum).setText(form.tvHeaderTotalAmount());
                vsHeader.textView(R.id.tv_total_count).setText(form.tvHeaderTotalQuantity());
            }
        };
        onChange.onChange();
    }

    @Override
    public void reloadContent() {
        setListItems(form.services.getItems());
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_service;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealService item) {
        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<VDealService> filteredItems = adapter.getFilteredItems();
            VDealService lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }

        vsItem.textView(R.id.tv_name).setText(item.tvTitleInfo());
        vsItem.imageView(R.id.iv_more).setImageDrawable(moreIcon);
        vsItem.textView(R.id.tv_price).setText(item.tvPrice());
        vsItem.bind(R.id.et_service_quant, item.quant);
        if (!data.hasEdit()) vsItem.id(R.id.et_service_quant).setEnabled(false);

        vsItem.model(R.id.et_service_quant).add(onChange).add(new ModelChange() {
            @Override
            public void onChange() {
                ErrorResult error = item.getError();
                TextView tvError = vsItem.textView(R.id.tv_error);
                tvError.setVisibility(error.isError() ? View.VISIBLE : View.GONE);
                tvError.setText(error.getErrorMessage());
            }
        }).notifyListeners();


        vsItem.id(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBuilder option = UI.dialog().title(R.string.select)
                        .option(R.string.product_info, new Command() {
                            @Override
                            public void apply() {
                                DealRef dr = data.vDeal.dealRef;
                                ArgProduct argProduct = new ArgProduct(dr.accountId, dr.filialId, item.product.id);
                                ProductInfoFragment.open(argProduct);
                            }
                        });

                if (data.hasEdit()) {
                    if (item.discountSpinner != null) {
                        option.option(R.string.deal_discount, new Command() {
                            @Override
                            public void apply() {
                                UI.dialog().title(R.string.select)
                                        .option(item.discountSpinner.options, new DialogBuilder.CommandFacade<SpinnerOption>() {
                                            @Override
                                            public CharSequence getName(SpinnerOption val) {
                                                return val.name;
                                            }

                                            @Override
                                            public void apply(SpinnerOption val) {
                                                BigDecimal percent = (BigDecimal) val.tag;
                                                item.margin.setValue(percent);
                                                reloadContent();
                                                onChange.onChange();
                                            }
                                        }).show(getActivity());
                            }
                        });
                    }
                }

                option.negative(R.string.close, Util.NOOP).show(getActivity());
            }
        });
    }
}
