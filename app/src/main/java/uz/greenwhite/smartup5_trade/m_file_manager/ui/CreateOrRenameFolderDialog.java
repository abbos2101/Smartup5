package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.mold.MoldDialogFragment;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgCreateOrRenameDialog;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class CreateOrRenameFolderDialog extends MoldDialogFragment {

    public static void show(FragmentActivity activity, ArgCreateOrRenameDialog arg) {
        CreateOrRenameFolderDialog d = Mold.parcelableArgumentNewInstance(CreateOrRenameFolderDialog.class,
                arg, ArgCreateOrRenameDialog.UZUM_ADAPTER);
        d.show(activity.getSupportFragmentManager(), "create_or_rename_folder_dialog");
    }

    public ArgCreateOrRenameDialog getArgDialog() {
        return Mold.parcelableArgument(this, ArgCreateOrRenameDialog.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;
    private final JobMate jobMate = new JobMate();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(getActivity(), R.layout.f_manager_dialog_name);
        final ArgCreateOrRenameDialog arg = getArgDialog();

        final EditText etFolderName = this.vsRoot.editText(R.id.et_folder_name);
        etFolderName.setText(arg.fileName);

        final AlertDialog d = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.Dialog))
                .setView(vsRoot.view)
                .setTitle(R.string.f_m_enter_name)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                d.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createOrRename();
                            }
                        });
            }
        });
        return d;

    }

    //--------------------------------------------------------------------------------------------------
    private void createOrRename() {
        EditText etFolderName = this.vsRoot.editText(R.id.et_folder_name);
        String folderName = etFolderName.getText().toString();

        ArgCreateOrRenameDialog arg = getArgDialog();

        if (arg.folderAndFileName.contains(folderName, MyMapper.<String>identity())) {
            vsRoot.textView(R.id.tv_error).setText(R.string.f_m_duplicate_name_error);
            return;
        }

        final MoldContentFragment contentFragment = Mold.getContentFragment(getActivity());

        MyArray<String> data = MyArray.from(arg.fileId, folderName, arg.folderId);
        jobMate.executeWithDialog(getActivity(), new ActionJob<>(arg, RT.URI_CREATE_OR_RENAME_FOLDER, data))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolve, String s, Throwable throwable) {
                        if (resolve) {
                            contentFragment.reloadContent();
                            Mold.makeSnackBar(getActivity(), R.string.f_m_folder_created).show();
                            dismiss();
                        } else
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                    }
                });
    }
}