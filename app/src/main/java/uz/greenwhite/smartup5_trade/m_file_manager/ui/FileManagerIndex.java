package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;

import java.io.File;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageTabFragment;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgFileManager;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.UserFile;
import uz.greenwhite.smartup5_trade.m_file_manager.job.SendFileJob;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

import static uz.greenwhite.smartup5_trade.m_file_manager.FileManagerUtil.getPath;

public class FileManagerIndex extends MoldPageTabFragment {

    public static void open(ArgSession arg) {
        Mold.openContent(FileManagerIndex.class, Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    private JobMate jobMate = new JobMate();

    SharedFilesFragment sharedFilesContent;
    UserFilesFragment myFilesContent;


    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTabLayout().setTabMode(TabLayout.MODE_FIXED);
        setSections(MyArray.from(
                UserFilesFragment.newInstance(new ArgFileManager(getArgSession(), "1", "1", false), R.string.f_manager_myFiles_title),
                SharedFilesFragment.newInstance(new ArgFileManager(getArgSession(), "", "", true), R.string.f_manager_sharedFiles_title)
        ));

        sharedFilesContent = (SharedFilesFragment) adapter.getItem(1);
        myFilesContent = (UserFilesFragment) adapter.getItem(0);

        setTabIcons(
                R.drawable.ic_folder_open_black_24dp,
                R.drawable.ic_folder_shared_black_24dp
        );
    }

    @Override
    public void reloadContent() {
        sharedFilesContent.onRefresh();
        myFilesContent.onRefresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(),true);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case OpenFolderFragment.COPY_REQUEST_CODE:
                    Mold.makeSnackBar(getActivity(), R.string.f_m_file_copied).show();
                    myFilesContent.onRefresh();
                    break;
                case OpenFolderFragment.MOVE_REQUEST_CODE:
                    Mold.makeSnackBar(getActivity(), R.string.f_m_file_moved).show();
                    myFilesContent.onRefresh();
                    break;
                case OpenFolderFragment.FILE_SELECT_CODE:
                    UI.dialog()
                            .title(R.string.f_m_upload_download_file)
                            .message(R.string.f_m_want_upload_download)
                            .positive(R.string.yes, new Command() {
                                @Override
                                public void apply() {
                                    //noinspection ConstantConditions
                                    try {
                                        String path = getPath(getContext(), data.getData());
                                        if (TextUtils.isEmpty(path)) {
                                            UI.alertError(getActivity(), DS.getString(R.string.file_manager_dir_is_empty));
                                            return;
                                        }
                                        File file = new File(path);
                                        fileUpload(getActivity(), file, "1");
                                    } catch (Exception e) {
                                        UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(e).message);
                                    }
                                }
                            })
                            .negative(R.string.no, Util.NOOP).show(getActivity());
                    break;

            }
        }
    }

    public void fileUpload(final Activity activity, final File file, final String folderId) {
        boolean fileExists = myFilesContent.getListFilteredItems().contains(new MyPredicate<UserFile>() {
            @Override
            public boolean apply(UserFile myFile) {
                return myFile.fileName.equals(file.getName());
            }
        });

        if (fileExists) {
            UI.alert(activity, DS.getString(R.string.warning), DS.getString(R.string.f_m_duplicate_name_error));
        } else {
            jobMate.executeWithDialog(activity, new SendFileJob(getArgSession().getAccount(), RT.URI_PHOTO, file))
                    .done(new Promise.OnDone<String>() {
                        @Override
                        public void onDone(String s) {
                            sendFileInfo(activity, folderId, s);
                            Mold.makeSnackBar(activity, R.string.f_m_file_uploaded).show();
                        }
                    }).fail(new Promise.OnFail() {
                @Override
                public void onFail(Throwable throwable) {
                    UI.alertError(activity, ErrorUtil.getErrorMessage(throwable).message.toString());
                }
            });
        }

    }

    private void sendFileInfo(final Activity activity, final String folderId, final String sha) {
        jobMate.executeWithDialog(activity, new ActionJob<>(getArgSession(), RT.URI_UPLOAD_FILE, MyArray.from(folderId, sha)))
                .done(new Promise.OnDone<String>() {
                    @Override
                    public void onDone(String s) {
                        Mold.makeSnackBar(activity, R.string.f_m_file_saved_to_server).show();
                        myFilesContent.onRefresh();
                    }
                })
                .fail(new Promise.OnFail() {
                    @Override
                    public void onFail(Throwable throwable) {
                        Mold.makeSnackBar(activity, ErrorUtil.getErrorMessage(throwable).message.toString())
                                .setAction(R.string.message_retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendFileInfo(activity, folderId, sha);
                                    }
                                }).show();
                    }
                });
    }
}
