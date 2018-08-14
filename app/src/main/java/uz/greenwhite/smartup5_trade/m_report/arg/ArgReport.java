package uz.greenwhite.smartup5_trade.m_report.arg;// 05.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgReport extends ArgSession {

    public final String code;
    public final MyArray<String> data;
    public final boolean withSpinner;
    public final boolean prepaymentStatus;

    public ArgReport(ArgSession arg, String code, MyArray<String> data, boolean withSpinner, boolean prepaymentStatus) {
        super(arg.accountId, arg.filialId);
        this.code = code;
        this.data = data;
        this.withSpinner = withSpinner;
        this.prepaymentStatus = prepaymentStatus;
    }

    public ArgReport(ArgSession arg, String code, MyArray<String> data, boolean withSpinner) {
        this(arg, code, data, withSpinner, false);
    }

    public ArgReport(ArgSession arg, String code, MyArray<String> data) {
        this(arg, code, data, false, false);
    }

    public ArgReport(UzumReader in) {
        super(in);
        this.code = in.readString();
        this.data = in.readValue(UzumAdapter.STRING_ARRAY);
        this.withSpinner = in.readBoolean();
        this.prepaymentStatus = in.readBoolean();
    }

    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.code);
        w.write(this.data, UzumAdapter.STRING_ARRAY);
        w.write(this.withSpinner);
        w.write(this.prepaymentStatus);
    }

    public static final UzumAdapter<ArgReport> UZUM_ADAPTER = new UzumAdapter<ArgReport>() {
        @Override
        public ArgReport read(UzumReader in) {
            return new ArgReport(in);
        }

        @Override
        public void write(UzumWriter out, ArgReport val) {
            val.write(out);
        }
    };
}
