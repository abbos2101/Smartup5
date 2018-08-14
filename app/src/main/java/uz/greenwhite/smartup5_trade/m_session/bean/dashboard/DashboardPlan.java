package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;


import java.math.BigDecimal;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DashboardPlan {

    public static final String K_PLAN_SUM = "S";
    public static final String K_PLAN_QUANT = "Q";
    public static final String K_PLAN_AKB = "A";
    public static final String K_PLAN_SUCCESS_VISIT = "R";
    public static final String K_PLAN_WEIGHT = "W";

    public final String roomId;
    public final String planType;
    public final BigDecimal fact, plan, prediction;

    protected DashboardPlan(String roomId,
                            String planType,
                            BigDecimal fact,
                            BigDecimal plan,
                            BigDecimal prediction) {
        this.roomId = roomId;
        this.planType = planType;
        this.fact = Util.nvl(fact, BigDecimal.ZERO);
        this.plan = Util.nvl(plan, BigDecimal.ZERO);
        this.prediction = Util.nvl(prediction, BigDecimal.ZERO);
    }


    public boolean hasPlan() {
        return plan.compareTo(BigDecimal.ZERO) != 0;
    }

    public static final UzumAdapter<DashboardPlan> UZUM_ADAPTER = new UzumAdapter<DashboardPlan>() {
        @Override
        public DashboardPlan read(UzumReader in) {
            return new DashboardPlan(
                    in.readString(), in.readString(),
                    in.readBigDecimal(), in.readBigDecimal(), in.readBigDecimal()
            );
        }

        @Override
        public void write(UzumWriter out, DashboardPlan val) {
            out.write(val.roomId);
            out.write(val.planType);
            out.write(val.fact);
            out.write(val.plan);
            out.write(val.prediction);
        }
    };

}
