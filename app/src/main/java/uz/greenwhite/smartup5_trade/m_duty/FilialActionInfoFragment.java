package uz.greenwhite.smartup5_trade.m_duty;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_duty.arg.ArgCondition;
import uz.greenwhite.smartup5_trade.m_duty.bean.ActionConditionRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.BonusProductRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.ConditionBonusRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.ConditionRuleRow;
import uz.greenwhite.smartup5_trade.m_duty.bean.FilialActionRow;
import uz.greenwhite.smartup5_trade.m_session.bean.Product;

public class FilialActionInfoFragment extends MoldContentRecyclerFragment<ActionConditionRow> {


    public static void open(Activity activity, ArgCondition arg) {
        Mold.openContent(activity, FilialActionInfoFragment.class,
                Mold.parcelableArgument(arg, ArgCondition.UZUM_ADAPTER));
    }

    public ArgCondition getArgCondition() {
        return Mold.parcelableArgument(this, ArgCondition.UZUM_ADAPTER);
    }


    private final JobMate jobMate = new JobMate();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArgCondition arg = getArgCondition();
        final FilialActionRow action = arg.getFilialActionRow();

        Mold.setTitle(getActivity(), action.action.name);

        ViewSetup vsHeader = setHeader(R.layout.duty_action_header);
        vsHeader.textView(R.id.tv_warehouse).setText(DS.getString(R.string.duty_action_warehouse, action.warehouse.name));
        vsHeader.textView(R.id.tv_action_kind).setText(DS.getString(R.string.duty_action_kind, action.getActionKind()));
        vsHeader.textView(R.id.tv_date).setText(DS.getString(R.string.duty_action_date, action.action.startDate, action.action.endDate));

        ScopeUtil.execute(jobMate, getArgCondition(), new OnScopeReadyCallback<MyArray<ActionConditionRow>>() {
            @Override
            public MyArray<ActionConditionRow> onScopeReady(Scope scope) {
                return DutyApi.getActionConditionRows(scope, action);
            }

            @Override
            public void onDone(MyArray<ActionConditionRow> actionConditionRows) {
                setListItems(actionConditionRows);
            }
        });
    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        jobMate.stopListening();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.duty_action_condition;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, ActionConditionRow row) {
        vsItem.textView(R.id.tv_title).setText(row.title);

        ViewGroup vgRule = vsItem.viewGroup(R.id.ll_condition_rule);
        vgRule.removeAllViews();
        populateRule(vgRule, row.rules);

        ViewGroup vgBonus = vsItem.viewGroup(R.id.ll_condition_bonuce);
        vgBonus.removeAllViews();
        populateBonuse(vgBonus, row.bonuces);

    }

    private void populateBonuse(ViewGroup viewGroup, MyArray<ConditionBonusRow> items) {
        int bonuce = 1;
        for (ConditionBonusRow row : items) {
            final ViewSetup vs = new ViewSetup(getActivity(), R.layout.duty_action_bonuse);
            vs.textView(R.id.tv_bonuce_title).setText(DS.getString(R.string.duty_action_bonus, String.valueOf(bonuce++)));

            vs.id(R.id.tv_bonuce_title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vs.id(R.id.ll_product).setVisibility(vs.id(R.id.ll_product).getVisibility() == View.VISIBLE
                            ? View.GONE : View.VISIBLE);
                }
            });

            int productCount = 1;
            ViewGroup vgProduct = vs.viewGroup(R.id.ll_product);
            vgProduct.removeAllViews();
            for (BonusProductRow product : row.item) {
                ViewSetup vsProduct = new ViewSetup(getActivity(), android.R.layout.simple_list_item_2);
                vsProduct.textView(android.R.id.text1).setText(String.format("%s) %s", String.valueOf(productCount++), product.product.name));
                vsProduct.textView(android.R.id.text2).setText(product.getDetail());
                vgProduct.addView(vsProduct.view);
            }
            viewGroup.addView(vs.view);
        }
    }

    private void populateRule(ViewGroup viewGroup, MyArray<ConditionRuleRow> items) {
        int rule = 1;
        for (ConditionRuleRow row : items) {
            final ViewSetup vs = new ViewSetup(getActivity(), R.layout.duty_action_rule);
            vs.textView(R.id.tv_rule_title).setText(DS.getString(R.string.duty_action_rule, String.valueOf(rule++)));

            vs.id(R.id.tv_rule_title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vs.id(R.id.ll_rule).setVisibility(vs.id(R.id.ll_rule).getVisibility() == View.VISIBLE
                            ? View.GONE : View.VISIBLE);
                }
            });

            vs.textView(R.id.tv_rule_values).setText(row.getRuleValue());

            ViewGroup vgProduct = vs.viewGroup(R.id.ll_rule_product);
            int productNo = 1;
            for (Product product : row.products) {
                ViewSetup vsProduct = new ViewSetup(getActivity(), android.R.layout.simple_list_item_1);
                vsProduct.textView(android.R.id.text1).setText(String.format("%s) %s", String.valueOf(productNo++), product.name));
                vgProduct.addView(vsProduct.view);
            }
            viewGroup.addView(vs.view);
        }
    }
}
