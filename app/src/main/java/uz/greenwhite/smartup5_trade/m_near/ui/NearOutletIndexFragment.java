package uz.greenwhite.smartup5_trade.m_near.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.NavigationFragment;
import uz.greenwhite.lib.mold.NavigationItem;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_near.arg.ArgNearOutlet;

public class NearOutletIndexFragment extends NavigationFragment {

    public static void open(ArgNearOutlet args) {
        Bundle bundle = Mold.parcelableArgument(args, ArgNearOutlet.UZUM_ADAPTER);
        Mold.openNavigation(NearOutletIndexFragment.class, bundle);
    }

    public ArgNearOutlet getArgNearOutlet() {
        return Mold.parcelableArgument(this, ArgNearOutlet.UZUM_ADAPTER);
    }

    public static final int LIST = 0;
    public static final int MAP = 1;

    private final MyArray<NavigationItem> FORMS = MyArray.from(
            new NavigationItem(MAP, DS.getString(R.string.outlet_neer_map)),
            new NavigationItem(LIST, DS.getString(R.string.outlet_neer_list))
    );

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setItems(FORMS);
    }

    @Override
    public boolean showForm(NavigationItem form) {
        MoldContentFragment fragment = make(form.id, getArgNearOutlet());
        if (fragment != null) {
            Mold.replaceContent(getActivity(), fragment, form);
            return true;
        }
        return false;
    }

    public MoldContentFragment make(int id, ArgNearOutlet argNearOutlet) {
        switch (id) {
            case LIST:
                return NearOutletListFragment.newInstance(argNearOutlet);
            case MAP:
                return NearOutletFragment.newInstance(argNearOutlet);
            default:
                throw AppError.Unsupported();
        }
    }
}
