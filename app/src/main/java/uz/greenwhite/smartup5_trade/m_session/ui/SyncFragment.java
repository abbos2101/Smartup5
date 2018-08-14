package uz.greenwhite.smartup5_trade.m_session.ui;// 07.07.2016

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.TextView;

import uz.greenwhite.lib.Setter;
import uz.greenwhite.lib.job.JobApi;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.LongJobListener;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.job.SyncJob;

public class SyncFragment extends MoldContentFragment {

    public static SyncFragment newInstance(ArgSession arg) {
        return Mold.parcelableArgumentNewInstance(SyncFragment.class, arg, ArgSession.UZUM_ADAPTER);
    }


    public static void open(Activity activity, ArgSession arg) {
        Mold.openContent(activity, SyncFragment.class, Mold.parcelableArgument(arg, ArgSession.UZUM_ADAPTER));
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.session_sync);
        return this.vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.admin_sync);

        ArgSession arg = getArgSession();
        Account a = arg.getAccount();

        try {
            vsRoot.textView(R.id.tv_user).setText(a.uc.userName);
            vsRoot.textView(R.id.tv_server).setText(a.server.code);
            vsRoot.textView(R.id.tv_last_sync).setText(AdminApi.getLastSync(arg.accountId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        final CompoundButton cbSyncShort = vsRoot.compoundButton(R.id.cb_sync_short);
        final CompoundButton cbSyncLong = vsRoot.compoundButton(R.id.cb_sync_long);

        final Setter<Boolean> syncShort = new Setter<>();
        final Setter<Boolean> syncLong = new Setter<>();
        syncShort.value = cbSyncShort.isChecked();
        syncLong.value = cbSyncLong.isChecked();

        cbSyncShort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean check) {
                syncShort.value = check;
                if (!check && !syncLong.value) {
                    vsRoot.compoundButton(R.id.cb_send_photo).setChecked(false);
                } else if (syncShort.value && syncLong.value) {
                    cbSyncLong.setChecked(false);
                }
            }
        });

        cbSyncLong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean check) {
                syncLong.value = check;
                if (!check && !syncShort.value) {
                    vsRoot.compoundButton(R.id.cb_send_photo).setChecked(false);
                } else if (syncLong.value && syncShort.value) {
                    cbSyncShort.setChecked(false);
                }
            }
        });

        vsRoot.compoundButton(R.id.cb_send_photo).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean check) {
                if (check) {
                    cbSyncShort.setChecked(true);
                }
            }
        });

        vsRoot.id(R.id.btn_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSync();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ArgSession arg = getArgSession();
        jobMate.listenKey(TapeSyncJob.key(arg.accountId), new SyncListener(vsRoot));
    }

    private void startSync() {
        try {
            ArgSession arg = getArgSession();
            if (!JobApi.isRunning(TapeSyncJob.key(arg.accountId))) {

                boolean syncShort = vsRoot.compoundButton(R.id.cb_sync_short).isChecked();
                boolean syncLong = vsRoot.compoundButton(R.id.cb_sync_long).isChecked();
                boolean sendPhoto = vsRoot.compoundButton(R.id.cb_send_photo).isChecked();
                boolean downloadPhoto = vsRoot.compoundButton(R.id.cb_download_photo).isChecked();
                boolean downloadFile = vsRoot.compoundButton(R.id.cb_download_file).isChecked();

                jobMate.execute(new SyncJob(arg.getAccount(), arg.filialId, syncShort,
                        syncShort || syncLong, sendPhoto, downloadPhoto, downloadFile));
            }
        } catch (Exception e) {
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(e).message);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    private class SyncListener implements LongJobListener<ProgressValue> {

        private final Animation anim;
        private final ViewSetup vs;

        SyncListener(ViewSetup vs) {
            this.vs = vs;
            this.anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
            this.anim.setRepeatCount(Animation.INFINITE);
        }

        private void showError(CharSequence error) {
            TextView tvError = vs.textView(R.id.tv_error);
            tvError.setText(error);
            tvError.setVisibility(TextUtils.isEmpty(error) ? View.GONE : View.VISIBLE);
        }

        private void setEnable(boolean enable) {
            vs.id(R.id.btn_sync).setEnabled(enable);
            vs.id(R.id.cb_sync_short).setEnabled(enable);
            vs.id(R.id.cb_sync_long).setEnabled(enable);
            vs.id(R.id.cb_send_photo).setEnabled(enable);
            vs.id(R.id.cb_download_photo).setEnabled(enable);
            vs.id(R.id.cb_download_file).setEnabled(enable);
        }

        @Override
        public void onStart() {
            showError("");
            setEnable(false);
            vs.id(R.id.iv_sync_status).startAnimation(anim);
            vs.imageView(R.id.iv_sync_status).setImageResource(R.drawable.sync_active);
            vs.textView(R.id.tv_sync_entry_error).setVisibility(View.GONE);
        }

        @Override
        public void onStop(Throwable error) {
            setEnable(true);
            boolean hasError = error != null;
            CharSequence message = ErrorUtil.getErrorMessage(error).message;
            showError(hasError ? message : "");
            vsRoot.id(R.id.iv_sync_status).clearAnimation();
            if (hasError) {
                vs.imageView(R.id.iv_sync_status).setImageResource(R.drawable.sync_error);
                vs.id(R.id.tv_sync_success_complete).setVisibility(View.GONE);
            } else {
                vs.imageView(R.id.iv_sync_status).setImageResource(R.drawable.sync_done);
                vs.id(R.id.tv_sync_success_complete).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onProgress(ProgressValue progress) {
            if (progress.sync.error > 0) {
                vs.textView(R.id.tv_sync_entry_error).setText(
                        DS.getString(R.string.session_sync_error_sending, progress.sync.error));
            }
            String syncLogs = progress.toString(false);
            vs.textView(R.id.tv_sync_logs).setText(Html.fromHtml(syncLogs));

            vs.textView(R.id.tv_sync_logs).setVisibility(!TextUtils.isEmpty(syncLogs) ? View.VISIBLE : View.GONE);
            vs.textView(R.id.tv_sync_entry_error).setVisibility(progress.sync.error > 0 ? View.VISIBLE : View.GONE);
        }
    }
}
