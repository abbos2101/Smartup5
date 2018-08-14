package uz.greenwhite.smartup5_trade.m_vp;// 23.09.2016

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgVisitPlan extends ArgSession {

    public final String roomId;
    public final String date;

    public ArgVisitPlan(String accountId, String filialId, String roomId, String date) {
        super(accountId, filialId);
        this.roomId = roomId;
        this.date = date;
    }

    public ArgVisitPlan(ArgSession arg, String roomId, String date) {
        this(arg.accountId, arg.filialId, roomId, date);
    }

    public ArgVisitPlan(UzumReader in) {
        super(in);
        this.roomId = in.readString();
        this.date = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.roomId);
        w.write(this.date);
    }

    public static final UzumAdapter<ArgVisitPlan> UZUM_ADAPTER = new UzumAdapter<ArgVisitPlan>() {
        @Override
        public ArgVisitPlan read(UzumReader in) {
            return new ArgVisitPlan(in);
        }

        @Override
        public void write(UzumWriter out, ArgVisitPlan val) {
            val.write(out);
        }
    };
}
