package uz.greenwhite.smartup5_trade.m_vp_outlet.arg;// 12.12.2016

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_vp_outlet.bean.OutletVisitPlan;

public class ArgVPOutlet extends ArgOutlet {

    public final String roomId;

    public ArgVPOutlet(ArgOutlet arg, String roomId) {
        super(arg, arg.outletId);
        this.roomId = roomId;
    }

    public ArgVPOutlet(UzumReader in) {
        super(in);
        this.roomId = in.readString();
    }

    public OutletVisitPlan getOutletVisitPlan() {
        Scope scope = getScope();
        OutletVisitPlan visitPlan = DSUtil.getOutletVisitPlan(scope, this.filialId, this.roomId, this.outletId);
        if (visitPlan == null) {
            visitPlan = OutletVisitPlan.makeDefault(this.filialId, this.roomId, this.outletId);
        }
        return visitPlan;
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(roomId);
    }

    public static final UzumAdapter<ArgVPOutlet> UZUM_ADAPTER = new UzumAdapter<ArgVPOutlet>() {
        @Override
        public ArgVPOutlet read(UzumReader in) {
            return new ArgVPOutlet(in);
        }

        @Override
        public void write(UzumWriter out, ArgVPOutlet val) {
            val.write(out);
        }
    };
}
