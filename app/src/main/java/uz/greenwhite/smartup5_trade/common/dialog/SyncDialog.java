package uz.greenwhite.smartup5_trade.common.dialog;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import uz.greenwhite.lib.job.JobApi;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.LongJobListener;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldDialogFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.m_admin.job.ProgressValue;
import uz.greenwhite.smartup.anor.m_admin.job.TapeSyncJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSync;
import uz.greenwhite.smartup5_trade.m_session.job.SyncJob;

public class SyncDialog extends MoldDialogFragment implements Runnable {

    public static void show(FragmentActivity activity, ArgSync arg) {
        SyncDialog d = Mold.parcelableArgumentNewInstance(SyncDialog.class, arg, ArgSync.UZUM_ADAPTER);
        d.show(activity.getSupportFragmentManager(), "session:sync_dialog");
    }

    public ArgSync getArgSession() {
        return Mold.parcelableArgument(this, ArgSync.UZUM_ADAPTER);
    }

    private JobMate jobMate = new JobMate();
    private Handler handler = new Handler();

    @NonNull
    @SuppressLint("RestrictedApi")
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewSetup vs = new ViewSetup(getActivity(), R.layout.z_sync_dialog);
        final ArgSync arg = getArgSession();

        if (!JobApi.isRunning(TapeSyncJob.key(arg.accountId))) {
            jobMate.execute(new SyncJob(arg.getAccount(), arg.filialId, arg.shortSync,
                    true, false, false, false));
        }

        jobMate.listenKey(TapeSyncJob.key(arg.accountId), new SyncListener(vs));

        return new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.Dialog))
                .setView(vs.view)
                .setPositiveButton(DS.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!JobApi.isRunning(arg.accountId)) {
                            handler.removeCallbacks(SyncDialog.this);
                            SyncDialog.this.run();
                        }
                    }
                })
                .setTitle(DS.getString(R.string.admin_sync))
                .create();
    }

    @Override
    public void run() {
        dismiss();
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(this);
        super.onStop();
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

        @Override
        public void onStart() {
            showError("");
            vs.id(R.id.iv_sync_status).startAnimation(anim);
            vs.imageView(R.id.iv_sync_status).setImageResource(R.drawable.sync_active);
            vs.textView(R.id.tv_sync_entry_error).setVisibility(View.GONE);
        }

        @Override
        public void onStop(Throwable error) {
            boolean hasError = error != null;
            showError(hasError ? ErrorUtil.getErrorMessage(error).message : "");
            vs.id(R.id.iv_sync_status).clearAnimation();
            if (hasError) {
                vs.imageView(R.id.iv_sync_status).setImageResource(R.drawable.sync_error);
                vs.id(R.id.tv_sync_success_complete).setVisibility(View.GONE);
            } else {
                vs.imageView(R.id.iv_sync_status).setImageResource(R.drawable.sync_done);
                vs.id(R.id.tv_sync_success_complete).setVisibility(View.VISIBLE);
            }

            handler.postDelayed(SyncDialog.this, 2 * 1000); // 2 second hang on
        }

        @Override
        public void onProgress(ProgressValue progress) {
            if (progress.sync.error > 0) {
                vs.textView(R.id.tv_sync_entry_error).setText(DS.getString(R.string.session_sync_error_sending, progress.sync.error));
            }

            String syncLogs = progress.toString(false);
            vs.textView(R.id.tv_sync_logs).setText(Html.fromHtml(syncLogs));

            vs.textView(R.id.tv_sync_logs).setVisibility(!TextUtils.isEmpty(syncLogs) ? View.VISIBLE : View.GONE);
            vs.textView(R.id.tv_sync_entry_error).setVisibility(progress.sync.error > 0 ? View.VISIBLE : View.GONE);
        }
    }
}
