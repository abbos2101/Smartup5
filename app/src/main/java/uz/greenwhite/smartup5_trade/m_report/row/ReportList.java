package uz.greenwhite.smartup5_trade.m_report.row;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class ReportList {

    public final String name;
    public final String uri;

    public ReportList(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public static final UzumAdapter<ReportList> UZUM_ADAPTER = new UzumAdapter<ReportList>() {
        @Override
        public ReportList read(UzumReader in) {
            return new ReportList(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, ReportList val) {
            out.write(val.name);
            out.write(val.uri);
        }
    };
}
