package uz.greenwhite.smartup5_trade.m_deal.ui;// 29.09.2016

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.MyImageView;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStock;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.stock.VDealStockProduct;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.ui.ProductInfoFragment;

public class StockFragment extends DealFormRecyclerFragment<VDealStockProduct> {

    private DealData data;
    private Drawable moreIcon = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.stock);
        this.data = Mold.getData(getActivity());

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VDealStockProduct item, String text) {
                boolean contains = CharSequenceUtil.containsIgnoreCase(item.product.name, text);
                if (!contains) {
                    contains = CharSequenceUtil.containsIgnoreCase(item.product.code, text);
                }
                return contains;
            }
        });

        addSubMenu(getString(R.string.filter), new Command() {
            @Override
            public void apply() {
                Mold.openTuningDrawer(getActivity());
            }
        });

        moreIcon = UI.changeDrawableColor(getActivity(),
                R.drawable.ic_more_vert_black_24dp, R.color.dark_silver);

        setHeader(R.layout.deal_stock_header);

        reloadContent();
    }

    @Override
    public void reloadContent() {
        VDealStockForm form = DealUtil.getDealForm(this);
        setListItems(form.stockProducts.getItems());
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new StockTuningFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_stock_product;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealStockProduct item) {

        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<VDealStockProduct> filteredItems = adapter.getFilteredItems();
            VDealStockProduct lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }


        vsItem.imageView(R.id.iv_more).setImageDrawable(moreIcon);
        vsItem.textView(R.id.name).setText(item.product.name);

        vsItem.id(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DealData data = Mold.getData(getActivity());
                DealRef dr = data.vDeal.dealRef;
                ArgProduct arg = new ArgProduct(dr.accountId, dr.filialId, item.product.id);
                ProductInfoFragment.open(arg);
            }
        });

        ViewGroup vg = vsItem.viewGroup(R.id.ll_stock_row);
        vg.removeAllViews();
        MyArray<VDealStock> productStocks = item.getStocks();
        for (int i = 0; i < productStocks.size(); i++) {
            final VDealStock val = productStocks.get(i);

            final ViewSetup vsRow = new ViewSetup(getActivity(), R.layout.deal_stock_row);

            vsRow.bind(R.id.et_expire_date, val.expireDate);
            vsRow.bind(R.id.et_stock, val.stock);
            vsRow.id(R.id.et_expire_date).setEnabled(data.hasEdit());
            vsRow.id(R.id.et_stock).setEnabled(data.hasEdit());
            vsRow.makeDatePicker(R.id.et_expire_date, true);

            ModelChange errorListener = new ModelChange() {
                @Override
                public void onChange() {
                    ErrorResult error = val.getError();
                    TextView tvError = vsRow.textView(R.id.error);
                    tvError.setVisibility(error.isError() ? View.VISIBLE : View.GONE);
                    if (error.isError()) {
                        tvError.setText(error.getErrorMessage());
                    }
                }
            };

            vsRow.model(R.id.et_stock).add(errorListener).notifyListeners();

            if (i == (productStocks.size() - 1)) {
                ((MyImageView) vsRow.imageView(R.id.miv_add_stock))
                        .setImageResource(R.drawable.ic_add_black_24dp, R.color.colorAccent);
                vsRow.id(R.id.miv_add_stock).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        item.appendNewProductStock();
                        adapter.notifyDataSetChanged();
                    }
                });

            } else {
                ((MyImageView) vsRow.imageView(R.id.miv_add_stock))
                        .setImageResource(R.drawable.ic_delete_black_24dp, R.color.colorAccent);
                vsRow.id(R.id.miv_add_stock).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        item.deleteStock(val);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            vg.addView(vsRow.view);
        }
    }
}
