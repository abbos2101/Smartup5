package uz.greenwhite.smartup5_trade.m_near.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_near.arg.ArgNearOutlet;

public class NearOutletFragment extends MoldContentFragment {

    public static NearOutletFragment newInstance(ArgNearOutlet arg) {
        return Mold.parcelableArgumentNewInstance(NearOutletFragment.class,
                arg, ArgNearOutlet.UZUM_ADAPTER);
    }

    public ArgNearOutlet getArgNearOutlet() {
        return Mold.parcelableArgument(this, ArgNearOutlet.UZUM_ADAPTER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new ViewSetup(inflater, container, R.layout.near_outlet).view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getChildFragmentManager()
                .beginTransaction().replace(R.id.map_view, MapFragment.newInstance(getArgNearOutlet())).commit();

        getChildFragmentManager()
                .beginTransaction().replace(R.id.map_list, MapListFragment.newInstance(getArgNearOutlet())).commit();
    }

    public MoldContentFragment getContentFragment(int resId) {
        return (MoldContentFragment) getChildFragmentManager().findFragmentById(resId);
    }
}
