package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.arg.ArgDeal;
import uz.greenwhite.smartup5_trade.m_session.bean.violation.Violation;

public class ViolationFragment extends MoldContentRecyclerFragment<Violation> {

    public static ViolationFragment newInstance(ArgDeal arg) {
        return Mold.parcelableArgumentNewInstance(ViolationFragment.class,
                arg, ArgDeal.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.deal_violation);

        DealData data = Mold.getData(getActivity());
        setListItems(data.vDeal.dealRef.allViolations);
    }


    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.deal_violation_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, Violation item) {
        DealData data = Mold.getData(getActivity());

        vsItem.textView(R.id.tv_title).setText(item.name);
        vsItem.textView(R.id.tv_detail).setText(item.getBanDetails(data.vDeal.dealRef));


    }
}
