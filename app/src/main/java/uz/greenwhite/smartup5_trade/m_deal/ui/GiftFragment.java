package uz.greenwhite.smartup5_trade.m_deal.ui;// 25.10.2016

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.EditText;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.common.CustomNumberKeyboard;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGift;
import uz.greenwhite.smartup5_trade.m_deal.variable.gift.VDealGiftForm;

public class GiftFragment extends DealFormRecyclerFragment<VDealGift> {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.gift);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(VDealGift item, String text) {
                boolean contains = CharSequenceUtil.containsIgnoreCase(item.product.name, text);
                if (!contains) {
                    contains = CharSequenceUtil.containsIgnoreCase(item.product.code, text);
                }
                return contains;
            }
        });

        setHeader(R.layout.deal_gift_header);

        reloadContent();

        ViewSetup vsFooter = new ViewSetup(getActivity(), R.layout.z_custom_number_keyboard);
        Mold.makeBottomSheet(getActivity(), vsFooter.view)
                .setState(BottomSheetBehavior.STATE_COLLAPSED);
        CustomNumberKeyboard.init(getActivity(), vsFooter);
    }

    @Override
    public void reloadContent() {
        VDealGiftForm form = DealUtil.getDealForm(this);
        setListItems(form.gifts.getItems());
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new GiftTuningFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_gift_row2;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final VDealGift item) {
        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<VDealGift> filteredItems = adapter.getFilteredItems();
            VDealGift lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }

        vsItem.textView(R.id.tv_name).setText(item.product.name);
        vsItem.textView(R.id.warehouse_avail).setText(item.tWarehouseAvail());

        ModelChange OnChange = new ModelChange() {
            @Override
            public void onChange() {
                UIUtils.showErrorText(vsItem.textView(R.id.tv_error), item.getError());
                item.balanceOfWarehouse.bookQuantity(item.card, item.formKey, item.getQuantity());
            }
        };
        OnChange.onChange();

        EditText quant = vsItem.editText(R.id.et_quant);
        CustomNumberKeyboard.prepare(quant);

        UI.bind(quant, item.quantity);
        UI.getModel(quant).add(OnChange);
    }
}
