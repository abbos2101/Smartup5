package uz.greenwhite.smartup5_trade.schedule;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.http.HttpTextRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.LongJob;
import uz.greenwhite.lib.job.Progress;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.Entry;

public class TrackingJob extends LongJob<Void> {

    public static String key(String accountId) {
        return "tracking:" + accountId;
    }

    private final Account account;

    protected TrackingJob(Account account) {
        super(key(account.accountId));
        this.account = account;
    }

    @Override
    public void execute(Progress<Void> progress) throws Exception {
        DS.initScope(account.accountId, null);
        final Scope scope = DS.getScope(account.accountId, null);
        final MyArray<Entry> entries = scope.db.entryLoadAll(RT.GT);
        if (entries.isEmpty()) return;
        HttpUtil.post(account.server.url + RT.URI_SAVE_TRACKING, new HttpTextRequest<Void>() {

            @Override
            public void header(HttpURLConnection conn) throws Exception {
                super.header(conn);
                conn.setRequestProperty("token", account.uc.token);
                conn.setRequestProperty("user_id", account.uc.userId);
            }

            @Override
            public void send(PrintWriter writer) throws Exception {
                for (Entry e : entries) {
                    String data = Uzum.toJson(e.val);
                    String json = "[\"" + e.entryId + "\"," + data + "]";
                    writer.println(json);
                }
            }

            @Override
            public Void receive(BufferedReader reader) throws Exception {
                for (Entry e : entries) {
                    scope.db.entryDelete(e.entryId);
                }
                return null;
            }
        });
    }
}
