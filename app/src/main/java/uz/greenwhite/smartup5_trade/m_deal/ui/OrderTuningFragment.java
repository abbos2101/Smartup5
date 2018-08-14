package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.filter.FilterBooleanList;
import uz.greenwhite.lib.filter.FilterUtil;
import uz.greenwhite.lib.filter.FilterValue;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.view_setup.Model;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.autocomplete.TokenCompleteTextView;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.common.SimilarAutoComplete;
import uz.greenwhite.smartup5_trade.m_deal.filter.OrderFilter;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductSimilar;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.SettingDeal;

public class OrderTuningFragment extends MoldTuningSectionFragment {

    private final Set<String> selectedProductIds = new HashSet<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyArray<Section> sections = MyArray.from(getFilterSection());
        DealData data = Mold.getData(getActivity());
        if (data.hasEdit()) {
            sections = sections.prepend(getDiscountSection());
            sections = sections.append(getCopyOrderSection());
            sections = sections.append(getSimilarSection());
        }
        setSections(sections.filterNotNull());
    }

    private OrderFilter getFilter() {
        FragmentActivity activity = getActivity();
        String formCode = DealUtil.getFormCode(Mold.getContentFragment(activity));
        DealData dealData = Mold.getData(activity);
        return dealData.filter.findOrder(formCode);
    }

    private void setDiscountToFiltered(ValueSpinner value) {
        BigDecimal percent = (BigDecimal) value.getValue().tag;
        FragmentActivity activity = getActivity();
        OrderFragment orderFragment = Mold.getContentFragment(activity);
        MyArray<VDealOrder> items = orderFragment.getListFilteredItems();
        for (VDealOrder order : items) {
            order.margin.setValue(percent);
        }
        Mold.closeTuningDrawer(getActivity());
        orderFragment.reloadContent();
    }

    private Section getDiscountSection() {
        final VDealOrderForm form = DealUtil.getDealForm(Mold.getContentFragment(getActivity()));
        if (form.discount == null) {
            return null;
        }
        return new Section() {
            @Override
            public View createView(LayoutInflater inflater, ViewGroup parent) {
                View v = inflater.inflate(R.layout.deal_order_tuning_discount, parent, false);
                ViewSetup vs = new ViewSetup(v);
                vs.bind(R.id.discount, form.discount);
                vs.button(R.id.btn_discount_filtered).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setDiscountToFiltered(form.discount);
                    }
                });
                return v;
            }
        };
    }

    private Section getSimilarSection() {
        final VDealOrderForm form = DealUtil.getDealForm(Mold.getContentFragment(getActivity()));
        return new Section() {
            @Override
            public View createView(LayoutInflater inflater, ViewGroup parent) {
                ViewSetup vs = new ViewSetup(inflater.inflate(R.layout.z_deal_similar, parent, false));
                SimilarAutoComplete autoComplete = vs.id(R.id.tac_similar);
                autoComplete.allowDuplicates(false);
                autoComplete.setThreshold(1);
                autoComplete.allowCollapse(false);
                autoComplete.setItems(form.products);
                autoComplete.initAdapter();
                autoComplete.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.SelectDeselect);

                autoComplete.setTokenListener(new TokenCompleteTextView.TokenListener<Product>() {
                    @Override
                    public void onTokenAdded(Product product) {
                        selectedProductIds.add(product.id);
                    }

                    @Override
                    public void onTokenRemoved(Product product) {
                        selectedProductIds.remove(product.id);
                    }
                });
                return vs.view;
            }
        };
    }

    private Section getFilterSection() {
        final OrderFilter filter = getFilter();
        if (filter == null) {
            return null;
        }
        DealData dealData = Mold.getData(getActivity());
        final SettingDeal deal = dealData.vDeal.dealRef.setting.deal;
        return new LinearLayoutSection() {

            @Override
            public void addViews(LinearLayout cnt) {
                cnt.removeAllViews();

                MyArray<FilterValue> filters = MyArray.from(
                        filter.product.groupFilter,
                        filter.productCard,
                        new FilterBooleanList(
                                MyArray.from(
                                        filter.product.hasBarcode,
                                        filter.product.hasPhoto,
                                        filter.product.hasFile,
                                        deal.mml ? null : filter.product.mml,
                                        filter.sortFirstMll,
                                        filter.hasValue,
                                        filter.hasDiscount,
                                        filter.warehouseAvail
                                ).filterNotNull())
                );

                MyArray<View> views = FilterUtil.addAll(cnt, filters.filterNotNull());
                DealUtil.addClearButton(getActivity(), cnt, views);
            }
        };
    }

    private Section getCopyOrderSection() {
        OrderFragment f = Mold.getContentFragment(getActivity());
        final Setter<VDealOrderForm> orderForm = new Setter<>();
        orderForm.value = f.form;
        if (orderForm.value == null) {
            orderForm.value = DealUtil.getDealForm(f);
        }
        DealData dealData = Mold.getData(getActivity());
        final ValueSpinner formsExceptThis = dealData.vDeal.getOrderFormsExceptThis(orderForm.value.code);

        if (formsExceptThis == null) {
            return null;
        }

        return new Section() {

            private ModelChange getSpinnerModelChange(final ViewSetup vs) {
                return new ModelChange() {
                    @Override
                    public void onChange() {
                        SpinnerOption value = formsExceptThis.getValue();
                        VDealOrderForm form = (VDealOrderForm) value.tag;
                        View from = vs.id(R.id.btn_from);
                        View to = vs.id(R.id.btn_to);
                        if (orderForm.value.priceType.withCard) {
                            if (form.priceType.withCard) {
                                from.setVisibility(View.VISIBLE);
                                to.setVisibility(View.VISIBLE);
                            } else {
                                from.setVisibility(View.GONE);
                                to.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (form.priceType.withCard) {
                                from.setVisibility(View.VISIBLE);
                                to.setVisibility(View.GONE);
                            } else {
                                from.setVisibility(View.VISIBLE);
                                to.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                };
            }

            @Override
            public View createView(LayoutInflater inflater, ViewGroup parent) {
                ViewSetup vs = new ViewSetup(inflater, parent, R.layout.deal_order_tuning_price);
                vs.bind(R.id.orders, formsExceptThis);
                vs.model(R.id.orders).add(getSpinnerModelChange(vs)).notifyListeners();
                View.OnClickListener onClick = getOnClick(vs);
                vs.button(R.id.btn_clear).setOnClickListener(onClick);
                vs.button(R.id.btn_from).setOnClickListener(onClick);
                vs.button(R.id.btn_to).setOnClickListener(onClick);
                return vs.view;
            }
        };
    }

    private View.OnClickListener getOnClick(final ViewSetup vs) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Mold.closeDrawers(getActivity());
                final OrderFragment f = Mold.getContentFragment(getActivity());
                Model model = vs.model(R.id.orders);
                switch (v.getId()) {
                    case R.id.btn_clear:
                        f.clearOrder();
                        break;
                    case R.id.btn_from:
                        f.moveFrom(model.value.getText());
                        break;
                    case R.id.btn_to:
                        f.moveTo(model.value.getText());
                        break;
                    default:
                        throw AppError.Unsupported();
                }
                Mold.closeTuningDrawer(getActivity());
            }
        };
    }

    @Override
    public void onDrawerClosed() {
        OrderFilter filter = getFilter();
        MyPredicate<VDealOrder> predicate = null;
        if (filter != null) {
            predicate = filter.getPredicate();
        }

        if (!selectedProductIds.isEmpty()) {
            final VDealOrderForm form = DealUtil.getDealForm(Mold.getContentFragment(getActivity()));
            final Set<String> predicateProductIds = new HashSet<>();
            for (String productId : selectedProductIds) {
                ProductSimilar productSimilar = form.productSimilars.find(productId, ProductSimilar.KEY_ADAPTER);
                if (productSimilar != null) {
                    predicateProductIds.addAll(productSimilar.similarIds.asSet());
                }
            }

            MyPredicate<VDealOrder> similarPredicate = new MyPredicate<VDealOrder>() {
                @Override
                public boolean apply(VDealOrder vDealOrder) {
                    return predicateProductIds.contains(vDealOrder.product.id);
                }
            };

            if (predicate == null) predicate = similarPredicate;
            else predicate = predicate.and(similarPredicate);
        }

        OrderFragment content = Mold.getContentFragment(getActivity());
        content.setListFilter(predicate);
    }
}