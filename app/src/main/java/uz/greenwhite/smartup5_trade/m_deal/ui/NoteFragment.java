package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.note.VDealNoteForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.note.VDealNoteType;

public class NoteFragment extends DealFormRecyclerFragment<VDealNoteType> {


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.note);
        VDealNoteForm form = DealUtil.getDealForm(this);
        setListItems(form.noteTypes.getItems());
        setEmptyText(R.string.list_is_empty);
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_note_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup viewSetup, VDealNoteType val) {
        viewSetup.textView(R.id.tv_title).setText(val.noteType.name);
        viewSetup.bind(R.id.et_note, val.note);
    }
}