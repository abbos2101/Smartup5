package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.ContextThemeWrapper;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldDialogFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgAccess;

public class AccessDialog extends MoldDialogFragment {

    public static void show(FragmentActivity activity, ArgAccess arg) {
        AccessDialog dialog = Mold.parcelableArgumentNewInstance(AccessDialog.class, arg, ArgAccess.UZUM_ADAPTER);
        dialog.show(activity.getSupportFragmentManager(), "file_add_access_dialog");
    }

    private ArgAccess getArg() {
        return Mold.parcelableArgument(this, ArgAccess.UZUM_ADAPTER);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final ArgAccess arg = getArg();
        ViewSetup vsRoot = new ViewSetup(getActivity(), R.layout.file_access_dialog);
        final VAccess vAccess = new VAccess(arg.fileAccesses, arg.users, arg.userId);

        vsRoot.bind(R.id.sp_user_name, vAccess.user);
        vsRoot.bind(R.id.sp_access_type, vAccess.accessType);

        @SuppressLint("RestrictedApi") final AlertDialog d = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.Dialog))
                .setTitle(R.string.f_m_access)
                .setView(vsRoot.view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccessFragment content = Mold.getContentFragment(getActivity());
                        content.addItem(vAccess.toValue(), arg.userId);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();


        return d;
    }
}
