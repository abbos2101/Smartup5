package uz.greenwhite.smartup5_trade.m_deal.ui;// 30.06.2016

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Comparator;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.PopupBuilder;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.BarcodeUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.CustomNumberKeyboard;
import uz.greenwhite.smartup5_trade.m_deal.DealApi;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgOrder;
import uz.greenwhite.smartup5_trade.m_deal.filter.OrderFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.ui.ProductInfoFragment;

public class OrderFragment extends DealFormRecyclerFragment<VDealOrder> {

    public ArgDeal getArgDeal() {
        return Mold.parcelableArgument(this, ArgDeal.UZUM_ADAPTER);
    }

    VDealOrderForm form;

    private DealData data;
    private OrderFilter filter;

    private boolean hasDiscount;
    public ModelChange onChange;
    private Drawable moreIcon = null;
    private boolean hasRecom;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.order);

        data = Mold.getData(getActivity());
        form = DealUtil.getDealForm(this);
        filter = data.filter.findOrder(DealUtil.getFormCode(this));

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VDealOrder item, String text) {
                boolean contains = CharSequenceUtil.containsIgnoreCase(item.product.name, text);
                if (!contains && item.barcode != null) {
                    contains = MyArray.nvl(item.barcode.barcodes).contains(text, MyMapper.<String>string());
                }
                if (!contains) {
                    contains = CharSequenceUtil.containsIgnoreCase(item.product.code, text);
                }
                return contains;
            }
        });

        hasDiscount = form.discount != null;

        final ViewSetup vsHeader = setHeader(R.layout.deal_order_header);
        onChange = new ModelChange() {
            @Override
            public void onChange() {
                MyArray<BigDecimal> headerInfo = form.getOrderHeaderInfo();
                vsHeader.textView(R.id.total_position).setText(NumberUtil.formatMoney(headerInfo.get(0)));
                vsHeader.textView(R.id.total_sku).setText(NumberUtil.formatMoney(headerInfo.get(1)));
                vsHeader.textView(R.id.total_sum).setText(NumberUtil.formatMoney(headerInfo.get(2)));
                vsHeader.textView(R.id.total_count).setText(Utils.formatMoney(headerInfo.get(3)));

                final ErrorResult error = form.getError();
                final TextView tvError = vsHeader.textView(R.id.tv_header_error);
                tvError.setVisibility(error.isError() ? View.VISIBLE : View.GONE);
                tvError.setText(error.getErrorMessage());
            }
        };
        onChange.onChange();

        setEmptyText(getString(R.string.list_is_empty));
        reloadContent();

        addSubMenu(getString(R.string.filter), new Command() {
            @Override
            public void apply() {
                Mold.openTuningDrawer(getActivity());
            }
        });

        addSubMenu(getString(R.string.barcode), new Command() {
            @Override
            public void apply() {
                BarcodeUtil.showBarcodeDialog(OrderFragment.this);
            }
        });

        OrderFilter filter = data.filter.findOrder(form.code);
        if (filter != null) {
            setListFilter(filter.getPredicate());
        }
        moreIcon = UI.changeDrawableColor(getActivity(),
                R.drawable.ic_more_vert_black_24dp, R.color.dark_silver);

        if (!DealApi.isCalculatorKeyboard()) {
            ViewSetup vsFooter = new ViewSetup(getActivity(), R.layout.z_custom_number_keyboard);
            Mold.makeBottomSheet(getActivity(), vsFooter.view)
                    .setState(BottomSheetBehavior.STATE_COLLAPSED);
            CustomNumberKeyboard.init(getActivity(), vsFooter);
        }
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, VDealOrder item) {
        if (DealApi.isCalculatorKeyboard() && data != null && data.hasEdit()) {
            OrderCalcDialog.show(this, new ArgOrder(getArgDeal(), item.product.id, item.price.cardCode));
        }
    }

    @Override
    public void reloadContent() {
        MyArray<VDealOrder> items = form.orders.getItems();
        hasRecom = items.contains(new MyPredicate<VDealOrder>() {
            @Override
            public boolean apply(VDealOrder val) {
                return !TextUtils.isEmpty(val.getRecomOrder()) &&
                        val.getBalanceOfWarehouse().compareTo(BigDecimal.ZERO) != 0;
            }
        });
        getHeader().findViewById(R.id.tv_recom_header)
                .setVisibility(hasRecom ? View.VISIBLE : View.GONE);

        MyArray<VDealOrder> orders = items;

        if (data != null && data.vDeal.dealRef.dealHolder.entryState.isSaved()) {
            orders = orders.sort(new Comparator<VDealOrder>() {
                @Override
                public int compare(VDealOrder l, VDealOrder r) {
                    int compare = 0;
                    if (filter == null || filter.sortFirstMll.value.getValue()) {
                        compare = MyPredicate.compare(l.mmlProduct ? 0 : 1, r.mmlProduct ? 0 : 1);
                    }
                    if (l.getError().isError()) {
                        return -1;
                    } else if (r.getError().isError()) {
                        return 1;
                    }

                    if (compare == 0) {
                        compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                        if (compare == 0) {
                            return CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                        }
                    }
                    return compare;
                }
            });
        } else {
            orders = orders.sort(new Comparator<VDealOrder>() {
                @Override
                public int compare(VDealOrder l, VDealOrder r) {
                    int compare = 0;
                    if (filter == null || filter.sortFirstMll.value.getValue()) {
                        compare = MyPredicate.compare(l.mmlProduct ? 0 : 1, r.mmlProduct ? 0 : 1);
                    }
                    if (compare == 0) {
                        compare = MyPredicate.compare(l.product.orderNo, r.product.orderNo);
                        if (compare == 0) {
                            return CharSequenceUtil.compareToIgnoreCase(l.product.name, r.product.name);
                        }
                    }
                    return compare;
                }
            });
        }

        setListItems(orders);
    }

    public void clearOrder() {
        form.clearOrder();
        reloadContent();
    }

    public void moveFrom(String code) {
        DealData dealData = Mold.getData(getActivity());
        dealData.vDeal.moveOrder(code, form.code);
        reloadContent();
    }

    public void moveTo(String code) {
        DealData dealData = Mold.getData(getActivity());
        dealData.vDeal.moveOrder(form.code, code);
        Mold.popContent(getActivity());
    }

    @Override
    public void setListFilter(MyPredicate<VDealOrder> predicate) {
        reloadContent();
        super.setListFilter(predicate);
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new OrderTuningFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(), true);

        final String barcode = BarcodeUtil.getBarcodeInActivityResult(getActivity(), requestCode, resultCode, data);
        if (!TextUtils.isEmpty(barcode)) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Mold.setSearchViewText(getActivity(), barcode);
                }
            }, 500);
        }
    }

    @Override
    protected int adapterGetLayoutResource() {
        if (DealApi.isCalculatorKeyboard()) {
            return R.layout.deal_order;
        } else if (form.priceEditable.editable) {
            return R.layout.deal_order3;
        }
        return R.layout.deal_order2;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealOrder item) {

        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<VDealOrder> filteredItems = adapter.getFilteredItems();
            VDealOrder lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }


        vsItem.textView(R.id.name).setText(item.tvTitleInfo());
        vsItem.textView(R.id.card).setText(item.price.cardCode);
        vsItem.textView(R.id.tv_order_recom).setText(Util.nvl(item.getRecomOrderByBoxText(), ""));
        vsItem.textView(R.id.warehouse_avail).setText(item.getBalanceOfWarehouseByBox());
        vsItem.imageView(R.id.iv_more).setImageDrawable(moreIcon);

        vsItem.id(R.id.miv_mml_star).setVisibility(item.mmlProduct ? View.VISIBLE : View.GONE);

        vsItem.id(R.id.ll_deal_order_row).setBackgroundResource(filter.sortFirstMll.value.getValue() && item.mmlProduct ?
                R.color.app_color_9_alfa : R.color.color_100_alfa);

        vsItem.id(R.id.card).setVisibility(TextUtils.isEmpty(item.price.cardCode.trim())
                ? View.GONE : View.VISIBLE);
        vsItem.id(R.id.tv_order_recom).setVisibility(hasRecom ? View.VISIBLE : View.GONE);

        final ErrorResult error = item.getError();
        final TextView tvError = vsItem.textView(R.id.error);
        tvError.setVisibility(error.isError() ? View.VISIBLE : View.GONE);
        tvError.setText(error.getErrorMessage());

        if (DealApi.isCalculatorKeyboard()) {
            ShortHtml html = null;
            if (item.box != null && item.box.nonZero()) {
                html = UI.html().v(Utils.formatMoney(item.box.getQuantity())).v(" ").b().v(item.product.boxName).b().v(". ");
            }

            if (item.quant != null && item.quant.nonZero()) {
                if (html == null) html = UI.html();
                else html.br();
                html.v(Utils.formatMoney(item.quant.getQuantity())).v(" ").b().v(item.product.measureName).b();
            }

            vsItem.textView(R.id.tv_order_quant).setText(html == null ? "" : html.html());
            vsItem.textView(R.id.tv_price).setText(NumberUtil.formatMoney(item.realPrice.getQuantity()));
        } else {

            ModelChange OnChange = new ModelChange() {
                @Override
                public void onChange() {
                    UIUtils.showErrorText(tvError, item.getError());

                    if (item.balanceOfWarehouse != null) {
                        item.balanceOfWarehouse.bookQuantity(item.card,
                                item.price.priceTypeId, item.getQuantity());
                    }
                }
            };
            OnChange.onChange();

            EditText box = vsItem.editText(R.id.et_box);
            EditText quant = vsItem.editText(R.id.et_quant);

            CustomNumberKeyboard.prepare(box);
            CustomNumberKeyboard.prepare(quant);
            box.setHint(item.product.boxName);
            quant.setHint(item.product.measureName);

            if (item.box != null) {
                UI.bind(box, item.box);
                UI.getModel(box).add(OnChange).add(onChange);
                box.setVisibility(View.VISIBLE);
            } else {
                box.setVisibility(View.GONE);
            }

            if (item.quant != null) {
                UI.bind(quant, item.quant);
                UI.getModel(quant).add(OnChange).add(onChange);
                quant.setVisibility(View.VISIBLE);
            } else {
                quant.setVisibility(View.GONE);
            }

            if (form.priceEditable.editable) {
                EditText price = vsItem.editText(R.id.et_price);
                CustomNumberKeyboard.prepare(price);
                UI.bind(price, item.realPrice);
                UI.getModel(price).add(OnChange).add(onChange);
            } else {
                vsItem.textView(R.id.tv_price).setText(NumberUtil.formatMoney(item.realPrice.getQuantity()));
            }
        }

        vsItem.id(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupBuilder option = UI.popup()
                        .option(R.string.product_info, new Command() {
                            @Override
                            public void apply() {
                                DealRef dr = ((DealData) Mold.getData(getActivity())).vDeal.dealRef;
                                ArgProduct argProduct = new ArgProduct(dr.accountId, dr.filialId, item.product.id);
                                ProductInfoFragment.open(argProduct);
                            }
                        });

                if (data.hasEdit()) {
                    if (hasDiscount) {
                        option.option(R.string.deal_discount, new Command() {
                            @Override
                            public void apply() {
                                UI.popup().option(form.discount.options, new PopupBuilder.CommandFacade<SpinnerOption>() {

                                    @Override
                                    @NonNull
                                    public CharSequence getName(SpinnerOption val) {
                                        return val.name;
                                    }

                                    @Override
                                    public void apply(SpinnerOption val) {
                                        BigDecimal percent = (BigDecimal) val.tag;
                                        item.margin.setValue(percent);
                                        item.marginOption = val;
                                        onChange.onChange();
                                        reloadContent();
                                    }
                                }).show(view);
                            }
                        });
                    }
                }

                if (item.balance != null && !TextUtils.isEmpty(item.balance.expireDate)) {
                    option.option(R.string.deal_expire_date, new Command() {
                        @Override
                        public void apply() {
                            UI.dialog()
                                    .message(item.balance.expireDate)
                                    .negative(R.string.close, Util.NOOP)
                                    .show(getActivity());
                        }
                    });
                }
                option.show(view);
            }
        });
    }
}
