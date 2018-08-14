package uz.greenwhite.smartup5_trade.m_file_manager;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;

public class SDownloadManager {

    private final String downloadFileUrl;
    private final String accountId;
    private final String fileName;

    private DownloadManager downloadManager;
    private long downloadReference;
    private BroadcastReceiver receiverDownloadClicked;

    public SDownloadManager(Account account, String fileName) {
        this.downloadFileUrl = account.server.url + RT.URI_FILE_DOWNLOAD;
        this.accountId = account.accountId;
        this.fileName = fileName;
    }

    public void downloadFile(Context context) {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadFileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription(fileName)
                .setTitle(DS.getString(R.string.app_name))
                .setDestinationUri(FileManagerUtil.getDownloadToDestinationUri(accountId, fileName))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        downloadReference = downloadManager.enqueue(request);

        IntentFilter intentForClick = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        receiverDownloadClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraId = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraId);
                for (long ref : references) {
                    if (ref == downloadReference) {
                        Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }
            }
        };

        context.registerReceiver(receiverDownloadClicked, intentForClick);

        IntentFilter intentForFinish = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (reference == downloadReference) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);

//                    int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
//                    String savedPath = cursor.getString(fileNameIndex);
//
//                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
//                    int reason = cursor.getInt(columnReason);

                    switch (status) {
                        case DownloadManager.STATUS_SUCCESSFUL:
//                            ACTION WHEN DOWNLOAD COMPLETED SUCCESSFULLY
                            Toast.makeText(context, DS.getString(R.string.file_manager_download_complete), Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_FAILED:
                            Toast.makeText(context, DS.getString(R.string.file_manager_failed_to_download), Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Toast.makeText(context, DS.getString(R.string.file_manager_download_paused), Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Toast.makeText(context, DS.getString(R.string.file_manager_download_pending), Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Toast.makeText(context, DS.getString(R.string.file_manager_download_is_running), Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        };

        context.registerReceiver(receiverDownloadComplete, intentForFinish);
    }

    public void cancelDownload(long... references) {
        downloadManager.remove(references);
    }
}
