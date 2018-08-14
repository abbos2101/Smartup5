package uz.greenwhite.smartup5_trade.m_session.ui.customer.content.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.filter.FilterUtil;
import uz.greenwhite.lib.filter.FilterValue;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.datasource.AnorDS;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerData;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.filter.PersonFilter;

public class PersonFilterDialog extends AppCompatDialogFragment {

    public static PersonFilterDialog show(CustomerFragment fragment) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        PersonFilterDialog bottomSheet = new PersonFilterDialog();
        bottomSheet.show(childFragmentManager, "customer-person-filter");
        return bottomSheet;
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewSetup vsRoot = new ViewSetup(getActivity(), R.layout.z_customer_filter);

        MoldContentFragment fragment = Mold.getContentFragment(getActivity());
        CustomerData data = fragment.getFragmentData();

        LinearLayout vg = vsRoot.viewGroup(R.id.ll_content);
        vg.removeAllViews();

        PersonFilter filter = data.filter.personFilter;

        MyArray<FilterValue> filters = MyArray.from(
                filter.region,
                filter.speciality,
                filter.groupFilter,
                filter.hasDeal,
                filter.lastVisitDate
        ).filterNotNull();

        MyArray<View> views = FilterUtil.addAll(vg, filters);
        FilterUtil.addClearButton(getActivity(), vg, views);
        addApplyButton(vg);


        final BottomSheetDialog d = new BottomSheetDialog(getActivity(), uz.greenwhite.lib.R.style.Theme_Design_BottomSheetDialog);
        d.setContentView(vsRoot.view);
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                FrameLayout frame = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(frame).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        return d;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        CustomerFragment fragment = Mold.getContentFragment(getActivity());
        fragment.notifyFilterChange();

    }

    private void addApplyButton(LinearLayout cnt) {
        LayoutInflater inflater = LayoutInflater.from(cnt.getContext());
        Button button = (Button) inflater.inflate(R.layout.gwslib_filter_button, null);
        button.setText(R.string.task_filter_apply);
        button.setBackground(AnorDS.getDrawable(R.drawable.button_accent_background));
        cnt.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
