package uz.greenwhite.smartup5_trade.common.calculator;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.MoldDialogFragment;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.OnTryCatchCallback;
import uz.greenwhite.smartup5_trade.R;

public abstract class CalcDialog extends MoldDialogFragment {

    @NonNull
    protected abstract View onCreateHeaderView();

    protected abstract void onKeyListener(@NonNull CalcKey key);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewSetup vsRoot = new ViewSetup(getActivity(), R.layout.z_calculator_dialog);

        ViewGroup vg = vsRoot.viewGroup(R.id.ll_calculator_header);
        vg.removeAllViews();
        vg.addView(onCreateHeaderView());

        vsRoot.imageView(R.id.iv_remove).setImageDrawable(UI.changeDrawableColor(getActivity(),
                R.drawable.ic_backspace_black_24dp, R.color.text_color_light));

        View.OnClickListener KEY_LISTENER = getKeyClickListener();

        vsRoot.id(R.id.tv_1).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_2).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_3).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_4).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_5).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_6).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_7).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_8).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_9).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_0).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_00).setOnClickListener(KEY_LISTENER);

        vsRoot.id(R.id.tv_dot).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.ll_key_remove).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_plus_1).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_subtract_1).setOnClickListener(KEY_LISTENER);
        vsRoot.id(R.id.tv_ok).setOnClickListener(KEY_LISTENER);

        return new AlertDialog.Builder(getActivity())
                .setView(vsRoot.view)
                .create();
    }

    private View.OnClickListener getKeyClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ErrorUtil.tryCatch(new OnTryCatchCallback() {
                    @Override
                    public void onTry() throws Exception {
                        CalcKey key = getKey(view);
                        onKeyListener(key);
                    }

                    @Override
                    public void onCatch(Exception ex) throws Exception {
                        UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(ex).message);
                    }
                });
            }
        };
    }

    private static CalcKey getKey(View view) {
        switch (view.getId()) {
            case R.id.tv_1:
                return CalcKey.KEY_1;
            case R.id.tv_2:
                return CalcKey.KEY_2;
            case R.id.tv_3:
                return CalcKey.KEY_3;
            case R.id.tv_4:
                return CalcKey.KEY_4;
            case R.id.tv_5:
                return CalcKey.KEY_5;
            case R.id.tv_6:
                return CalcKey.KEY_6;
            case R.id.tv_7:
                return CalcKey.KEY_7;
            case R.id.tv_8:
                return CalcKey.KEY_8;
            case R.id.tv_9:
                return CalcKey.KEY_9;
            case R.id.tv_0:
                return CalcKey.KEY_0;
            case R.id.tv_00:
                return CalcKey.KEY_00;

            case R.id.tv_dot:
                return CalcKey.KEY_DOT;
            case R.id.ll_key_remove:
                return CalcKey.KEY_REMOVE;
            case R.id.tv_plus_1:
                return CalcKey.KEY_PLUS_ONE;
            case R.id.tv_subtract_1:
                return CalcKey.KEY_SUBTRACT_ONE;
            case R.id.tv_ok:
                return CalcKey.KEY_OK;

            default:
                throw AppError.Unsupported();
        }
    }


}
