package uz.greenwhite.smartup5_trade.m_deal.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Map;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealAction;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionBonusProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionCondition;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionConditionBonus;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionForm;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;

public class ActionFragment extends DealFormRecyclerFragment<VDealAction> {


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(uz.greenwhite.lib.R.id.recycler).setBackgroundResource(uz.greenwhite.smartup.anor.R.drawable.header_bg);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.vsRoot.view.setBackgroundResource(uz.greenwhite.smartup.anor.R.color.background);

        DealUtil.makeAction((DealData) Mold.getData(getActivity()));

        VDealActionForm form = DealUtil.getDealForm(this);
        setListItems(form.actions.getItems());

        setFilter();

    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    private void setFilter() {
        adapter.predicateOthers = new MyPredicate<VDealAction>() {
            @Override
            public boolean apply(VDealAction vDealAction) {
                return vDealAction.isCanUse();
            }
        };
        adapter.filter();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_action_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup viewSetup, final VDealAction vDealAction) {
        viewSetup.view.setBackgroundColor(DS.getColor(R.color.alpha));

        TextView actionTitle = viewSetup.textView(R.id.tv_title);
        actionTitle.setText(vDealAction.action.name);

        ViewGroup vgCondition = viewSetup.viewGroup(R.id.ll_condition);
        vgCondition.removeAllViews();

        MyArray<VDealActionCondition> filteredActionConditions = vDealAction.conditions.getItems().filter(new MyPredicate<VDealActionCondition>() {
            @Override
            public boolean apply(VDealActionCondition val) {
                return val.isCanUse();
            }
        });

        for (final VDealActionCondition condition : filteredActionConditions) {
            ViewSetup vsCondition = new ViewSetup(getActivity(), R.layout.deal_action_condition_row);
            ViewGroup vgBonus = vsCondition.viewGroup(R.id.ll_bonuses);

            condition.buttons.clear();

            for (final VDealActionConditionBonus bonus : condition.conditionBonus.getItems()) {
                ViewSetup vsBonus = new ViewSetup(getActivity(), R.layout.deal_action_condition_bonus_row);
                vsBonus.bind(R.id.cb_bonus, bonus.isTaken);
                vgBonus.addView(vsBonus.view);

                ViewGroup vgProduct = vsBonus.viewGroup(R.id.ll_bonus_product);
                for (VDealActionBonusProduct bonusProduct : bonus.bonusProducts.getItems()) {
                    String productName = bonusProduct.product.name + " (" + bonusProduct.getQuantity().toPlainString() +
                            (bonusProduct.bonusProduct.bonusKind.equals(BonusProduct.K_DISCOUNT) ? "%"
                                    : Util.nvl(bonusProduct.product.measureName)) + ")";

                    ViewSetup vsProduct = new ViewSetup(getActivity(), android.R.layout.simple_list_item_1);
                    vsProduct.textView(android.R.id.text1).setText(Html.fromHtml(productName));
                    vgProduct.addView(vsProduct.view);
                }

                condition.buttons.put(bonus.bonusId, vsBonus.compoundButton(R.id.cb_bonus));

                vsBonus.model(R.id.cb_bonus).add(new ModelChange() {
                    @Override
                    public void onChange() {
                        CompoundButton cb = condition.buttons.get(bonus.bonusId);
                        VDealActionForm form = DealUtil.getDealForm(ActionFragment.this);
                        form.applyAction((DealData) Mold.getData(getActivity()));

                        if (cb.isChecked()) {
                            for (Map.Entry<String, CompoundButton> val : condition.buttons.entrySet()) {
                                if (!val.getKey().equals(bonus.bonusId)) {
                                    val.getValue().setChecked(false);
                                }
                            }
                        }
                    }
                });
            }
            vgCondition.addView(vsCondition.view);

        }
    }
}
