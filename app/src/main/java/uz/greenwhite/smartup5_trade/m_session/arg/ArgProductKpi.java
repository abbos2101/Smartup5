package uz.greenwhite.smartup5_trade.m_session.arg;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.row.DashboardProductKpiRow;

public class ArgProductKpi extends ArgSession {

    public final String roomId;
    public final String planType;
    public final String id;
    public final MyArray<DashboardProductKpiRow> kpiRows;

    public ArgProductKpi(ArgSession arg,
                         String roomId,
                         String planType,
                         String id,
                         MyArray<DashboardProductKpiRow> kpiRows) {
        super(arg.accountId, arg.filialId);
        this.roomId = roomId;
        this.planType = planType;
        this.id = id;
        this.kpiRows = kpiRows;
    }

    public ArgProductKpi(UzumReader in) {
        super(in);
        this.roomId = in.readString();
        this.planType = in.readString();
        this.id = in.readString();
        this.kpiRows = in.readArray(DashboardProductKpiRow.UZUM_ADAPTER);
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(roomId);
        w.write(planType);
        w.write(id);
        w.write(kpiRows, DashboardProductKpiRow.UZUM_ADAPTER);
    }

    public static final UzumAdapter<ArgProductKpi> UZUM_ADAPTER = new UzumAdapter<ArgProductKpi>() {
        @Override
        public ArgProductKpi read(UzumReader in) {
            return new ArgProductKpi(in);
        }

        @Override
        public void write(UzumWriter out, ArgProductKpi val) {
            val.write(out);
        }
    };
}
