package uz.greenwhite.smartup5_trade.m_deal.ui.agree;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.CustomNumberKeyboard;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgree;
import uz.greenwhite.smartup5_trade.m_deal.variable.agree.VDealAgreeForm;
import uz.greenwhite.smartup5_trade.m_product.arg.ArgProduct;
import uz.greenwhite.smartup5_trade.m_product.ui.ProductInfoFragment;

public class AgreeFragment extends MoldContentRecyclerFragment<VDealAgree> {
    public ArgDeal getArgDeal() {
        return Mold.parcelableArgument(this, ArgDeal.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.deal_agree);
        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VDealAgree item, String text) {
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

        setHeader(R.layout.deal_agree_header);

        ViewSetup vsFooter = new ViewSetup(getActivity(), R.layout.z_custom_number_keyboard);
        Mold.makeBottomSheet(getActivity(), vsFooter.view)
                .setState(BottomSheetBehavior.STATE_COLLAPSED);
        CustomNumberKeyboard.init(getActivity(), vsFooter);

        VDealAgreeForm form = DealUtil.getDealForm(this);
        setListItems(form.agrees.getItems());
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    public MoldTuningSectionFragment getTuningFragment() {
        return new AgreeMenuFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_agree_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, final VDealAgree item) {

        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<VDealAgree> filteredItems = adapter.getFilteredItems();
            VDealAgree lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }

        vsItem.textView(R.id.name).setText(item.product.name);
        vsItem.textView(R.id.old_cur_value).setText(item.oldCurValue);
        vsItem.textView(R.id.old_new_value).setText(item.oldNewValue);
        vsItem.textView(R.id.old_period).setText(item.oldPeriod);

        vsItem.bind(R.id.period, item.period);
        vsItem.bind(R.id.et_cur_value, item.curValue);
        vsItem.bind(R.id.et_new_value, item.newValue);

        vsItem.imageView(R.id.product_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductInfoFragment.open(new ArgProduct(getArgDeal(), item.product.id));
            }
        });
        CustomNumberKeyboard.prepare(vsItem.editText(R.id.et_cur_value));
        CustomNumberKeyboard.prepare(vsItem.editText(R.id.et_new_value));
    }
}
