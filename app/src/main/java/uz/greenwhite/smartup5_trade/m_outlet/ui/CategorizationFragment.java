package uz.greenwhite.smartup5_trade.m_outlet.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.BuildConfig;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.categorization.CatApi;
import uz.greenwhite.smartup5_trade.m_outlet.categorization.CatData;
import uz.greenwhite.smartup5_trade.m_outlet.variable.VCategorizationRow;

public class CategorizationFragment extends MoldContentRecyclerFragment<VCategorizationRow> {

    public static void open(ArgOutlet arg) {
        Mold.openContent(CategorizationFragment.class, Mold.parcelableArgument(arg, ArgOutlet.UZUM_ADAPTER));
    }

    private ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArgOutlet arg = getArgOutlet();
        Mold.setTitle(getActivity(), arg.getOutlet().name);
        CatData data = Mold.getData(getActivity());
        if (data == null) {
            data = new CatData(arg.getScope(), arg.outletId);
            Mold.setData(getActivity(), data);
        }

        setEmptyIcon(R.drawable.ic_assignment_black_48dp);
        setEmptyText(R.string.categorization_empty_text);
        setListItems(data.vCategorization.catRows.getItems());

        ViewSetup vs = new ViewSetup(getActivity(), R.layout.display_footer);
        if (data.hasEdit()) {
            vs.id(R.id.ll_save_or_ready).setVisibility(View.VISIBLE);
            vs.id(R.id.btn_edit).setVisibility(View.GONE);
            vs.id(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save(false);
                }
            });
            vs.id(R.id.btn_ready).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save(true);
                }
            });
            BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vs.view);
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (data.vCategorization.entryState.isReady()) {
            vs.id(R.id.ll_save_or_ready).setVisibility(View.GONE);
            vs.id(R.id.btn_edit).setVisibility(View.VISIBLE);
            vs.id(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeEdit();
                }
            });

            BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vs.view);
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if (!TextUtils.isEmpty(data.vCategorization.entryState.serverResult)) {
            CharSequence error = data.vCategorization.entryState.getErrorText();
            vs = setHeader(R.layout.display_header);
            vs.textView(R.id.tv_error).setText(error);
        }

    }

    private void makeEdit() {
        try {
            CatApi.catMakeEdit(getArgOutlet(), (CatData) Mold.getData(getActivity()));
            CategorizationFragment.open(getArgOutlet());
            getActivity().finish();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            uz.greenwhite.smartup.anor.ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), e);
        }
    }

    private void save(boolean isReady) {
        FragmentActivity activity = getActivity();
        try {
            CatData data = Mold.getData(activity);
            ErrorResult error = data.vCategorization.getError();
            if (error.isError()) {
                UI.alert(activity, DS.getString(R.string.error), error.getErrorMessage());
                return;
            }
            CatApi.saveCategories(getArgOutlet(), data.vCategorization, isReady);
            getActivity().finish();
        } catch (Exception ex) {
            UI.alertError(activity, ex);
        }
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.categorization_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vs, VCategorizationRow item) {
        vs.textView(R.id.tv_cat_name).setText(item.catQuiz.name);
        vs.bind(R.id.sp_cat, item.answer);
        vs.spinner(R.id.sp_cat).setEnabled(((CatData) Mold.getData(getActivity())).hasEdit());
    }
}