package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.comment.VDealComment;
import uz.greenwhite.smartup5_trade.m_deal.variable.comment.VDealCommentForm;

public class CommentFragment extends DealFormRecyclerFragment<VDealComment> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.deal_comment);
        VDealCommentForm form = DealUtil.getDealForm(this);
        setListItems(form.items.getItems());
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_comment_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, VDealComment item) {
        vsItem.textView(R.id.tv_comment_text).setText(item.comment.name);
        vsItem.bind(R.id.cb_comment, item.check);
    }
}
