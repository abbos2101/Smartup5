package uz.greenwhite.smartup5_trade.m_tracking.arg;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TrackingUser;

public class ArgTracking extends ArgSession {

    public final String agentId;
    public final String date;
    public final MyArray<TrackingUser> users;

    public ArgTracking(ArgSession argSession,
                       String agentId,
                       String date,
                       MyArray<TrackingUser> users) {
        super(argSession.accountId, argSession.filialId);
        this.agentId = agentId;
        this.date = date;
        this.users = users;
    }

    public ArgTracking(UzumReader in) {
        super(in);
        this.agentId = in.readString();
        this.date = in.readString();
        this.users = Uzum.toValue(in.readString(), TrackingUser.UZUM_ADAPTER.toArray());
    }

    public void write(UzumWriter w) {
        super.write(w);
        w.write(this.agentId);
        w.write(this.date);
        w.write(Uzum.toJson(this.users, TrackingUser.UZUM_ADAPTER.toArray()));
    }

    public static final UzumAdapter<ArgTracking> UZUM_ADAPTER = new UzumAdapter<ArgTracking>() {

        @Override
        public ArgTracking read(UzumReader in) {
            return new ArgTracking(in);
        }

        @Override
        public void write(UzumWriter out, ArgTracking val) {
            val.write(out);
        }
    };
}
