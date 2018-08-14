package uz.greenwhite.smartup5_trade.m_shipped;// 09.09.2016

import android.os.Bundle;
import android.support.v4.app.Fragment;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.smartup5_trade.m_shipped.arg.ArgSDeal;
import uz.greenwhite.smartup5_trade.m_shipped.ui.SDealData;
import uz.greenwhite.smartup5_trade.m_shipped.variable.VSDealForm;

public class ShippedUtil {

    private static final String ARG_FORM_CODE = "arg_form_code";

    public static MoldContentFragment newInstance(Class<? extends MoldContentFragment> cls, String formCode) {
        try {
            MoldContentFragment f = cls.newInstance();

            Bundle arg = new Bundle();
            arg.putString(ARG_FORM_CODE, formCode);
            f.setArguments(arg);

            return f;
        } catch (Exception e) {
            throw new AppError(e);
        }
    }

    public static MoldContentFragment newInstance(ArgSDeal arg, Class<? extends MoldContentFragment> cls, String formCode) {
        try {
            MoldContentFragment f = cls.newInstance();

            Bundle bundle = Mold.parcelableArgument(arg, ArgSDeal.UZUM_ADAPTER);
            bundle.putString(ARG_FORM_CODE, formCode);
            f.setArguments(bundle);

            return f;
        } catch (Exception e) {
            throw new AppError(e);
        }
    }

    public static String getFormCode(Fragment fragment) {
        return fragment.getArguments().getString(ARG_FORM_CODE);
    }

    public static <T extends VSDealForm> T getDealForm(Fragment fragment) {
        SDealData dealData = Mold.getData(fragment.getActivity());
        return dealData.vDeal.findForm(getFormCode(fragment));
    }
}
