package uz.greenwhite.smartup5_trade.m_deal.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.datasource.AnorDS;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealAction;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionBonusProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionCondition;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionConditionBonus;
import uz.greenwhite.smartup5_trade.m_deal.variable.action2.VDealActionModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGift;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrder;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.order.VDealOrderModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverload;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadLoad;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadProduct;
import uz.greenwhite.smartup5_trade.m_deal.variable.overload.VOverloadRule;
import uz.greenwhite.smartup5_trade.m_deal.variable.total.VDealTotalForm;
import uz.greenwhite.smartup5_trade.m_session.bean.action.BonusProduct;

public class TotalFragment extends MoldContentFragment {

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.deal_total);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addMenu(R.drawable.ic_done_black_24dp, "", new Command() {
            @Override
            public void apply() {
                DealData data = Mold.getData(getActivity());
                Mold.popContent(getActivity(), AdminApi.alwaysShowDealTotal(data.accountId));
            }
        });

        VDealTotalForm form = DealUtil.getDealForm(this);
        MyArray<VDealOrderModule> orders = form.orders;

        ViewGroup vgOrders = vsRoot.id(R.id.ll_orders);
        View frameOrder = vsRoot.id(R.id.frame_order);
        vgOrders.removeAllViews();

        if (!orders.isEmpty()) {
            int position = 0;
            for (final VDealOrderModule orderModule : orders) {
                for (VDealOrderForm dealForm : orderModule.orderForms.getItems()) {
                    for (final VDealOrder order : dealForm.orders.getItems()) {
                        if (order.hasValue()) {

                            frameOrder.setVisibility(View.VISIBLE);
                            vgOrders.setVisibility(View.VISIBLE);
                            ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_total_row);
                            vs.textView(R.id.tv_no).setText(String.valueOf(++position).concat("."));
                            vs.textView(R.id.tv_product_name).setText(order.product.name);
                            vs.textView(R.id.tv_amount).
                                    setText(AnorDS.getString(R.string.deal_amount_scale, NumberUtil.formatMoney(order.getQuantity()), order.product.measureName));
                            vgOrders.addView(vs.view);

                        }
                    }
                }
            }
        }

        MyArray<VDealActionModule> actions = form.actions;

        ViewGroup vgActions = vsRoot.id(R.id.ll_actions);
        View frameActions = vsRoot.id(R.id.frame_action);
        vgActions.removeAllViews();

        if (!actions.isEmpty()) {
            int position = 0;
            for (final VDealActionModule actionModule : actions) {
                if (actionModule != null)
                    for (VDealAction action : actionModule.form.actions.getItems()) {
                        for (VDealActionCondition condition : action.conditions.getItems()) {
                            for (VDealActionConditionBonus bonus : condition.conditionBonus.getItems()) {
                                if (bonus.isTaken.getValue()) {
                                    for (VDealActionBonusProduct product : bonus.bonusProducts.getItems()) {
                                        if (product.bonusProduct.bonusKind.equalsIgnoreCase(BonusProduct.K_QUANTITY)) {

                                            vgActions.setVisibility(View.VISIBLE);
                                            frameActions.setVisibility(View.VISIBLE);
                                            ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_total_row);
                                            vs.textView(R.id.tv_no).setText(String.valueOf(++position).concat("."));
                                            vs.textView(R.id.tv_product_name).setText(product.product.name);
                                            vs.textView(R.id.tv_amount).
                                                    setText(AnorDS.getString(R.string.deal_amount_scale,
                                                            NumberUtil.formatMoney(product.getQuantity()), product.product.measureName));
                                            vgActions.addView(vs.view);

                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        } else {
            vgActions.setVisibility(View.GONE);
        }

        MyArray<VDealGiftModule> promos = form.gifts;

        ViewGroup vgPromo = vsRoot.id(R.id.ll_promo);
        View framePromo = vsRoot.id(R.id.frame_promo);
        vgPromo.removeAllViews();

        if (!promos.isEmpty()) {
            int position = 0;
            for (VDealGiftModule module : promos) {
                if (module != null) {
                    for (VDealGiftForm giftForm : module.forms.getItems()) {
                        for (VDealGift gift : giftForm.gifts.getItems()) {
                            if (gift.hasValue()) {

                                framePromo.setVisibility(View.VISIBLE);
                                vgPromo.setVisibility(View.VISIBLE);
                                ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_total_row);
                                vs.textView(R.id.tv_no).setText(String.valueOf(++position).concat("."));
                                vs.textView(R.id.tv_product_name).setText(gift.product.name);
                                vs.textView(R.id.tv_amount).
                                        setText(AnorDS.getString(R.string.deal_amount_scale,
                                                NumberUtil.formatMoney(gift.getQuantity()),
                                                gift.product.measureName));
                                vgPromo.addView(vs.view);

                            }
                        }
                    }
                }
            }
        } else {
            vgPromo.setVisibility(View.GONE);
        }

        MyArray<VOverloadModule> overloads = form.overloads;

        ViewGroup vgOverloads = vsRoot.id(R.id.ll_overloads);
        View frameOverload = vsRoot.id(R.id.frame_overload);
        vgOverloads.removeAllViews();

        if (!overloads.isEmpty()) {
            int position = 0;
            for (final VOverloadModule module : overloads) {
                if (module != null)
                    for (VOverload overload : module.form.overloads.getItems()) {
                        for (VOverloadRule rules : overload.rules.getItems()) {
                            for (VOverloadLoad load : rules.loads.getItems()) {
                                if (load.isTaken.getValue()) {
                                    for (VOverloadProduct product : load.getProducts()) {

                                        vgOverloads.setVisibility(View.VISIBLE);
                                        frameOverload.setVisibility(View.VISIBLE);
                                        ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_total_row);
                                        vs.textView(R.id.tv_no).setText(String.valueOf(++position).concat("."));
                                        vs.textView(R.id.tv_product_name).setText(product.product.name);
                                        vs.textView(R.id.tv_amount).
                                                setText(AnorDS.getString(R.string.deal_amount_scale,
                                                        NumberUtil.formatMoney(product.getQuantity()), product.product.measureName));
                                        vgOverloads.addView(vs.view);

                                    }
                                }
                            }
                        }
                    }
            }
        } else {
            vgOverloads.setVisibility(View.GONE);
        }
    }
}
