package uz.greenwhite.smartup5_trade.m_session.bean.dashboard;

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DashboardProductTypePlan extends DashboardPlan {

    public final String productTypeId;

    public DashboardProductTypePlan(String roomId,
                                    String productTypeId,
                                    String planType,
                                    BigDecimal fact,
                                    BigDecimal plan,
                                    BigDecimal prediction) {
        super(roomId, planType, fact, plan, prediction);
        this.productTypeId = productTypeId;
    }

    public static final UzumAdapter<DashboardProductTypePlan> UZUM_ADAPTER = new UzumAdapter<DashboardProductTypePlan>() {
        @Override
        public DashboardProductTypePlan read(UzumReader in) {
            return new DashboardProductTypePlan(in.readString(), in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readBigDecimal()
            );
        }

        @Override
        public void write(UzumWriter out, DashboardProductTypePlan val) {
            out.write(val.roomId);
            out.write(val.productTypeId);
            out.write(val.planType);
            out.write(val.fact);
            out.write(val.plan);
            out.write(val.prediction);
        }
    };
}
