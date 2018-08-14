package uz.greenwhite.smartup5_trade.m_report.job;// 05.09.2016

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.http.HttpTextRequest;
import uz.greenwhite.lib.http.HttpUtil;
import uz.greenwhite.lib.job.ShortJob;
import uz.greenwhite.lib.util.IOUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.bean.admin.Account;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_report.arg.ArgReport;

public class ReportJob implements ShortJob<String> {

    private final Account account;
    private final String filialId;
    private final String code;
    private final MyArray<String> data;

    public ReportJob(ArgReport arg) {
        this.account = arg.getAccount();
        this.filialId = arg.filialId;
        this.code = arg.code;
        this.data = arg.data;
    }

    @Override
    public String execute() throws Exception {
        String url = account.server.url + RT.URI_REPORT;
        ReportRequest request = new ReportRequest(this.code, this.data);
        final String json = Uzum.toJson(request, ReportRequest.UZUM_ADAPTER);
        return HttpUtil.post(url, new HttpTextRequest<String>() {
            @Override
            public void header(HttpURLConnection conn) throws Exception {
                super.header(conn);
                conn.setRequestProperty("token", account.uc.token);
                conn.setRequestProperty("user_id", account.uc.userId);
                conn.setRequestProperty("filial_id", filialId);
            }

            @Override
            public void send(PrintWriter writer) throws Exception {
                writer.println(json);
            }

            @Override
            public String receive(BufferedReader reader) throws Exception {
                return IOUtil.readToEnd(reader);
            }
        });
    }

    static class ReportRequest {

        public final String reportType;
        public final MyArray<String> reportData;

        public ReportRequest(String reportType, MyArray<String> reportData) {
            this.reportType = reportType;
            this.reportData = reportData;
        }

        public static final UzumAdapter<ReportRequest> UZUM_ADAPTER = new UzumAdapter<ReportRequest>() {
            @Override
            public ReportRequest read(UzumReader in) {
                throw AppError.Unsupported();
            }

            @Override
            public void write(UzumWriter out, ReportRequest val) {
                out.write(val.reportType);
                out.write(val.reportData, STRING_ARRAY);
            }
        };
    }
}
