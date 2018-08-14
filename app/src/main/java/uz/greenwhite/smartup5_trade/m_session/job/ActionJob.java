package uz.greenwhite.smartup5_trade.m_session.job;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.http.HttpTextRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.lib.util.IOUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ActionJob<D> extends HttpTextRequest<String> implements ShortJob<String> {

    private final Account account;
    private final String filialId;
    private final String action;

    private final D data;
    private final UzumAdapter<D> adapter;

    public ActionJob(ArgSession arg, String action, D data, UzumAdapter<D> adapter) {
        super(IOUtil.CODE_PAGE);
        this.account = arg.getAccount();
        this.filialId = arg.filialId;
        this.action = action;
        this.data = data;
        this.adapter = adapter;
    }

    @SuppressWarnings("unchecked")
    public ActionJob(ArgSession arg, String action, MyArray<String> data) {
        this(arg, action, (D) data, (UzumAdapter<D>) UzumAdapter.STRING_ARRAY);
    }

    public ActionJob(ArgSession arg, String action) {
        this(arg, action, null);
    }

    @Override
    public String execute() throws Exception {
        return HttpUtil.post(account.server.url + action, this);
    }

    @Override
    public void header(HttpURLConnection conn) throws Exception {
        super.header(conn);
        conn.setRequestProperty("token", account.uc.token);
        conn.setRequestProperty("user_id", account.uc.userId);
        conn.setRequestProperty("filial_id", filialId);
    }

    @Override
    public void send(PrintWriter out) throws Exception {
        if (data != null && adapter != null) {
            out.println(Uzum.toJson(data, adapter));
        }
    }

    @Override
    public String receive(BufferedReader in) throws Exception {
        return IOUtil.readToEnd(in);
    }
}
