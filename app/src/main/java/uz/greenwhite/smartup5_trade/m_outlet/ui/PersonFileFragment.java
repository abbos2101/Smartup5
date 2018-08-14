package uz.greenwhite.smartup5_trade.m_outlet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.io.File;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobApi;
import uz.greenwhite.lib.job.LongJobListener;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentRecyclerFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup5_trade.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_file_manager.job.SendFileJob;
import uz.greenwhite.smartup5_trade.m_outlet.OutletApi;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.file.PersonFileDetail;
import uz.greenwhite.smartup5_trade.m_outlet.job.DownloadPersonFileJob;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

import static android.app.Activity.RESULT_OK;
import static uz.greenwhite.smartup5_trade.m_file_manager.FileManagerUtil.getPath;

public class PersonFileFragment extends MoldContentRecyclerFragment<PersonFileDetail> {

    public static void open(Activity activity, ArgOutlet arg) {
        Mold.openContent(activity, PersonFileFragment.class,
                Mold.parcelableArgument(arg, ArgOutlet.UZUM_ADAPTER));
    }

    private static final int FILE_SELECT_CODE = 1;

    private ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.outlet_person_file);

        Mold.makeFloatAction(getActivity(), R.drawable.ic_add_black_24dp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, FILE_SELECT_CODE);

                    }
                });

        reloadContent();
    }

    @Override
    public void reloadContent() {
        super.reloadContent();
        setListItems(OutletApi.getPersonFileDetails(getArgOutlet()));
    }

    @Override
    protected void onItemClick(final RecyclerAdapter.ViewHolder holder, final PersonFileDetail item) {
        final FragmentActivity activity = getActivity();
        ArgOutlet arg = getArgOutlet();
        if (!OutletUtil.fileExistsInDownloadFolder(arg.accountId, item.fileName)) {
            jobMate.executeWithDialog(activity, new DownloadPersonFileJob(arg.accountId, item.fileSha, item.fileName))
                    .done(new Promise.OnDone<String>() {
                        @Override
                        public void onDone(final String s) {
                            OutletUtil.openFile(activity, getArgOutlet(), item);
                        }
                    })
                    .fail(new Promise.OnFail() {
                        @Override
                        public void onFail(Throwable throwable) {
                            Mold.makeSnackBar(activity, R.string.f_m_file_not_downloaded).setAction(R.string.repeat, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onItemClick(holder, item);
                                }
                            }).show();
                        }
                    });
        } else {
            OutletUtil.openFile(activity, getArgOutlet(), item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    sendFile(getPath(getContext(), data.getData()));
                    break;
            }
        }
    }

    private void sendFile(String path) {
        ArgOutlet arg = getArgOutlet();
        Account account = arg.getAccount();
        final File file = new File(path);

        jobMate.executeWithDialog(getActivity(), new SendFileJob(account, RT.URI_FILE, file))
                .done(new Promise.OnDone<String>() {
                    @Override
                    public void onDone(String s) {
                        sendFileData(s, file);
                    }
                })
                .fail(new Promise.OnFail() {
                    @Override
                    public void onFail(Throwable throwable) {
                        UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                    }
                });
    }

    private void sendFileData(final String fileSha, final File file) {
        ArgOutlet arg = getArgOutlet();
        MyArray<String> data = MyArray.from(fileSha, file.getName(), arg.outletId, "");
        jobMate.executeWithDialog(getActivity(),
                new ActionJob<>(getArgOutlet(), RT.URI_UPLOAD_PERSON_FILE,
                        data, UzumAdapter.STRING_ARRAY))
                .done(new Promise.OnDone<String>() {
                    @Override
                    public void onDone(String result) {
                        sync();
                    }
                })
                .fail(new Promise.OnFail() {
                    @Override
                    public void onFail(Throwable error) {
                        Mold.makeSnackBar(getActivity(), ErrorUtil.getErrorMessage(error).message)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendFileData(fileSha, file);
                                    }
                                }).show();
                    }
                });
    }

    private void sync() {
        ArgOutlet arg = getArgOutlet();
        jobMate.stopListening();
        jobMate.listenKey(TapeSyncJob.key(arg.accountId), new SyncListener());
        if (!JobApi.isRunning(TapeSyncJob.key(arg.accountId))) {
            jobMate.execute(new TapeSyncJob(AdminApi.getAccount(arg.accountId)));
        }
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.person_file_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vsItem, PersonFileDetail item) {
        vsItem.imageView(R.id.iv_file_img).setImageDrawable(UI.changeDrawableColor(getActivity(), item.getMimiTypeIcon(), R.color.black));
        vsItem.textView(R.id.tv_filename).setText(item.fileName);
        vsItem.textView(R.id.tv_detail).setText(item.note);
    }

    private class SyncListener implements LongJobListener<ProgressValue> {

        private final ProgressDialog pd;

        SyncListener() {
            pd = new ProgressDialog(getActivity());
        }

        @Override
        public void onStart() {
            pd.setMessage(DS.getString(R.string.outlet_run_full_sync));
            pd.show();
        }

        @Override
        public void onStop(Throwable error) {
            pd.dismiss();
            if (error != null) {
                UI.alertError(getActivity(), (String) uz.greenwhite.smartup.anor.ErrorUtil.getErrorMessage(error).message);
            } else {
                reloadContent();
            }
        }

        @Override
        public void onProgress(ProgressValue progress) {
        }
    }
}
