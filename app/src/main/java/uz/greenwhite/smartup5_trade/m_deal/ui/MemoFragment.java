package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.memo.VDealMemoForm;
import uz.greenwhite.smartup5_trade.m_outlet.bean.Memo;

public class MemoFragment extends DealFormRecyclerFragment<Memo> {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.memo);
        VDealMemoForm form = DealUtil.getDealForm(this);

        ViewSetup vs = new ViewSetup(getActivity(), R.layout.deal_memo);
        vs.bind(R.id.memo, form.memo);
        BottomSheetBehavior sheetBehavior = Mold.makeBottomSheet(getActivity(), vs.view);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        setListItems(form.memos);
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return android.R.layout.simple_list_item_2;
    }

    @Override
    protected void adapterPopulate(ViewSetup viewSetup, Memo memo) {
        viewSetup.textView(android.R.id.text1).setText(memo.memo);
        viewSetup.textView(android.R.id.text2).setText(memo.date);

    }
}
