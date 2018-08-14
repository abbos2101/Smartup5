package uz.greenwhite.smartup5_trade.m_deal.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Map;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverload;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadLoad;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadRule;
import uz.greenwhite.smartup5_trade.m_session.bean.ProductPrice;

public class OverloadFragment extends DealFormRecyclerFragment<VOverload> {


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(uz.greenwhite.lib.R.id.recycler).setBackgroundResource(uz.greenwhite.smartup.anor.R.drawable.header_bg);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.vsRoot.view.setBackgroundResource(uz.greenwhite.smartup.anor.R.color.background);

        DealUtil.makeOverload((DealData) Mold.getData(getActivity()));

        VOverloadForm form = DealUtil.getDealForm(this);
        setListItems(form.overloads.getItems());

        setFilter();

    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    private void setFilter() {
        adapter.predicateOthers = new MyPredicate<VOverload>() {
            @Override
            public boolean apply(VOverload item) {
                return item.isCanUse() && item.hasProduct();
            }
        };
        adapter.filter();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_overload_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup viewSetup, final VOverload item) {
        viewSetup.view.setBackgroundColor(DS.getColor(R.color.alpha));

        TextView actionTitle = viewSetup.textView(R.id.tv_title);
        actionTitle.setText(item.overload.name);

        ViewGroup vgRule = viewSetup.viewGroup(R.id.ll_overload_rule);
        vgRule.removeAllViews();

        MyArray<VOverloadRule> filterRules = item.rules.getItems().filter(new MyPredicate<VOverloadRule>() {
            @Override
            public boolean apply(VOverloadRule val) {
                return val.isCanUse();
            }
        });


        for (final VOverloadRule rule : filterRules) {
            if (!rule.isCanUse()) {
                continue;
            }

            rule.buttons.clear();

            ViewSetup vsRule = new ViewSetup(getActivity(), R.layout.deal_overload_rule_row);

            ViewGroup viewGroupLoad = vsRule.viewGroup(R.id.ll_loads);
            viewGroupLoad.removeAllViews();

            boolean secondIndex = false;
            for (final VOverloadLoad load : rule.loads.getItems()) {

                MyArray<VOverloadProduct> loadProducts = load.getProducts();
                if (loadProducts.isEmpty()) {
                    continue;
                }

                if (secondIndex) {
                    viewGroupLoad.addView(new ViewSetup(getActivity(), R.layout.z_divider).view);
                }

                secondIndex = true;

                ViewSetup vsLoad = new ViewSetup(getActivity(), R.layout.deal_overload_load_row);

                vsLoad.bind(R.id.cb_bonus, load.isTaken);
                rule.buttons.put(load.load.loadId, vsLoad.compoundButton(R.id.cb_bonus));

                ViewGroup vgProduct = vsLoad.viewGroup(R.id.ll_products);
                vgProduct.removeAllViews();

                for (VOverloadProduct ruleProduct : loadProducts) {
                    ProductPrice productPrice = ruleProduct.getProductPrice();

                    ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_overload_product);
                    BigDecimal quantity = ruleProduct.getQuantity();

                    vs.textView(R.id.tv_product_name).setText(ruleProduct.product.name);
                    vs.textView(R.id.tv_price).setText(NumberUtil.formatMoney(productPrice.price));
                    vs.textView(R.id.tv_total_price).setText(NumberUtil.formatMoney(productPrice.price.multiply(quantity)));
                    vs.textView(R.id.tv_quantity).setText(NumberUtil.formatMoney(quantity));

                    vgProduct.addView(vs.view);
                }

                viewGroupLoad.addView(vsLoad.view);

                vsLoad.model(R.id.cb_bonus).add(new ModelChange() {
                    @Override
                    public void onChange() {
                        CompoundButton cb = rule.buttons.get(load.load.loadId);
                        VOverloadForm form = DealUtil.getDealForm(OverloadFragment.this);
                        form.applyOverload();

                        if (cb.isChecked()) {
                            for (Map.Entry<String, CompoundButton> val : rule.buttons.entrySet()) {
                                if (!val.getKey().equals(load.load.loadId)) {
                                    val.getValue().setChecked(false);
                                }
                            }
                        }
                    }
                });
            }
            vgRule.addView(vsRule.view);
        }
    }
}
