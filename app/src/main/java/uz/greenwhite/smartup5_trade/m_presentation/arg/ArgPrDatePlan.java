package uz.greenwhite.smartup5_trade.m_presentation.arg;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgPrDatePlan extends ArgSession {

    public final String planType;

    public ArgPrDatePlan(ArgSession arg, String planType) {
        super(arg.accountId, arg.filialId);
        this.planType = planType;
    }

    protected ArgPrDatePlan(UzumReader in) {
        super(in);
        this.planType = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(planType);
    }

    public static final UzumAdapter<ArgPrDatePlan> UZUM_ADAPTER = new UzumAdapter<ArgPrDatePlan>() {
        @Override
        public ArgPrDatePlan read(UzumReader in) {
            return new ArgPrDatePlan(in);
        }

        @Override
        public void write(UzumWriter out, ArgPrDatePlan val) {
            val.write(out);
        }
    };
}
