package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;


import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DashboardProductPlan extends DashboardPlan {

    public final String productId;

    public DashboardProductPlan(String roomId,
                                String productId,
                                String planType,
                                BigDecimal fact,
                                BigDecimal plan,
                                BigDecimal prediction) {
        super(roomId, planType, fact, plan, prediction);
        this.productId = productId;
    }


    public static final UzumAdapter<DashboardProductPlan> UZUM_ADAPTER = new UzumAdapter<DashboardProductPlan>() {
        @Override
        public DashboardProductPlan read(UzumReader in) {
            return new DashboardProductPlan(
                    in.readString(), in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, DashboardProductPlan val) {
            out.write(val.roomId);
            out.write(val.productId);
            out.write(val.planType);
            out.write(val.fact);
            out.write(val.plan);
            out.write(val.prediction);
        }
    };
}
