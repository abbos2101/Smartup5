package uz.greenwhite.smartup5_trade.m_file_manager.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;

import java.util.HashMap;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.mold.MoldSwipeRecyclerContent;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_file_manager.FileManagerApi;
import uz.greenwhite.smartup5_trade.m_file_manager.FileManagerUtil;
import uz.greenwhite.smartup5_trade.m_file_manager.arg.ArgFileManager;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.FileAccess;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.SharedFile;
import uz.greenwhite.smartup5_trade.m_file_manager.bean.UserFile;
import uz.greenwhite.smartup5_trade.m_file_manager.job.DownloadFileJob;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

import static uz.greenwhite.smartup5_trade.datasource.DS.getString;

public class SharedFilesFragment extends MoldSwipeRecyclerContent<SharedFile> {


    public static MoldPageContent newInstance(ArgFileManager arg, @StringRes int titleResId) {
        return newContentInstance(SharedFilesFragment.class, Mold.parcelableArgument(arg,
                ArgFileManager.UZUM_ADAPTER), DS.getString(titleResId));
    }

    private ArgFileManager getArgFileManager() {
        return Mold.parcelableArgument(this, ArgFileManager.UZUM_ADAPTER);
    }

    private JobMate jobMate = new JobMate();
    private HashMap<String, MyArray<FileAccess>> fileAccessesByFiles;

    @Override

    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);
        setEmptyIcon(R.drawable.box_empty);
        setEmptyText(R.string.list_is_empty);
        setListItems(MyArray.<SharedFile>emptyArray());
        fileAccessesByFiles = new HashMap<>();
        onRefresh();
    }

    @Override
    public void onContentResume() {
        super.onContentResume();
        Mold.setTitle(getActivity(), R.string.f_manager_sharedFiles_title);
    }

    @Override
    public void onRefresh() {
        startRefresh();

        final Scope scope = getArgFileManager().getScope();
        final MyArray<SharedFile> sharedFiles = FileManagerApi.getSharedFiles(scope)
                .filter(new MyPredicate<SharedFile>() {
                    @Override
                    public boolean apply(SharedFile sharedFile) {
                        return sharedFile.level.equals("1");
                    }
                });
        setListItems(sharedFiles);

        jobMate.execute(new ActionJob<>(getArgFileManager(), RT.URI_LOAD_SHARED_FILES))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean b, String s, Throwable throwable) {
                        stopRefresh();
                        if (b) {
                            FileManagerApi.saveSharedFiles(scope, s);
                            setListItems(Uzum.toValue(s, SharedFile.UZUM_ADAPTER.toArray()));
                        } else {
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                        }

                    }
                });


    }

    @Override
    protected void onListItemChanged() {
        adapter.predicateOthers = new MyPredicate<SharedFile>() {
            @Override
            public boolean apply(SharedFile sharedFile) {
                return sharedFile.level.equals("1");
            }
        };
    }

    @Override
    public void onContentDestroy(@Nullable Bundle saveInstanceState) {
        super.onContentDestroy(saveInstanceState);
        jobMate.stopListening();
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, final SharedFile item) {
        if (item.isFolder()) {
            OpenFolderFragment.open(getActivity(), new ArgFileManager(getArgFileManager(), item.fileId, item.fileId, item.fileName, true, ArgFileManager.EXPLORE, MyArray.<String>emptyArray()));

        } else {
            UI.dialog()
                    .message(item.getFileInfo(DS.getString(R.string.file_manager_info_title)))
                    .negative(R.string.no, Util.NOOP)
                    .positive(R.string.yes, new Command() {
                        @Override
                        public void apply() {
                            downloadFile(getActivity(), item, true);
                        }
                    }).show(getActivity());
        }
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.f_manager_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, final SharedFile sharedFile) {
        vsItem.textView(R.id.tv_filename).setText(sharedFile.fileName);
        vsItem.textView(R.id.tv_modify_date).setText(getString(R.string.f_manager_modify_date, sharedFile.modifiedOn + "\n| " + sharedFile.ownerName));
        vsItem.id(R.id.iv_file_img).setBackgroundResource(sharedFile.getIconResId(getArgFileManager().isShared));

        View more = vsItem.id(R.id.iv_more);

        if (sharedFile.isFolder() || TextUtils.isEmpty(sharedFile.extension))
            more.setVisibility(View.INVISIBLE);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UI.popup().option(R.string.f_m_upload_download_file, new Command() {
                    @Override
                    public void apply() {
                        downloadFile(getActivity(), sharedFile, false);
                    }
                }).show(v);
            }
        });
    }

    //--------------------------------------------------------------------------------------------------

    private void downloadFile(final Activity activity, final UserFile item, final boolean open) {
        ArgFileManager arg = getArgFileManager();
        if (!FileManagerUtil.fileExistsInDownloadFolder(arg.accountId, item.fileName)) {
            jobMate.executeWithDialog(activity, new DownloadFileJob(arg.accountId, item.sha, item.fileName))
                    .done(new Promise.OnDone<String>() {
                        @Override
                        public void onDone(final String s) {
                            if (open) FileManagerUtil.openFile(activity, getArgFileManager(), item);
                            Mold.makeSnackBar(activity, R.string.f_m_file_downloaded).setAction(R.string.open, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FileManagerUtil.openFile(activity, getArgFileManager(), item);
                                }
                            }).show();
                        }
                    })
                    .fail(new Promise.OnFail() {
                        @Override
                        public void onFail(Throwable throwable) {
                            Mold.makeSnackBar(activity, R.string.f_m_file_not_downloaded).setAction(R.string.repeat, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downloadFile(activity, item, open);
                                }
                            }).show();
                        }
                    });
        } else {
            Mold.makeSnackBar(activity, R.string.f_m_file_downloaded).setAction(R.string.open, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileManagerUtil.openFile(activity, getArgFileManager(), item);
                }
            }).show();
        }

    }

}
