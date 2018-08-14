package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Parcelable;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;

public abstract class DealFormContentFragment extends MoldContentFragment {

    @Override
    public void onResume() {
        super.onResume();

        Parcelable data = Mold.getData(getActivity());
        if (data != null && data instanceof DealData) {
            ((DealData) data).vDeal.start();
        }
    }

}

