package uz.greenwhite.smartup5_trade.m_session.row;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.NumberUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.lib.view_setup.ShortHtml;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.widget.ChartLine;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DashboardProductTypePlan;

public class DashboardProductKpiRow {

    public final Room room;
    public final String planType;
    public final ArrayList<Detail> details;
    public final int orderNo;

    public DashboardProductKpiRow(Room room, String planType) {
        if (room != null) {
            this.room = room;
        } else {
            this.room = Room.DEFAULT;
        }
        this.planType = planType;
        this.orderNo = getOrderNo(planType);

        this.details = new ArrayList<>();
    }

    public DashboardProductKpiRow(Room room, String planType, MyArray<Detail> details) {
        if (room != null) {
            this.room = room;
        } else {
            this.room = Room.DEFAULT;
        }
        this.planType = planType;
        this.details = new ArrayList<>(details.asList());
        this.orderNo = getOrderNo(planType);
    }

    public CharSequence getTitle() {
        ShortHtml html = UI.html();
        switch (planType) {
            case DashboardProductTypePlan.K_PLAN_SUM:
                html.v(DS.getString(R.string.session_dashboard_by_money));
                break;
            case DashboardProductTypePlan.K_PLAN_QUANT:
                html.v(DS.getString(R.string.session_dashboard_by_quantity));
                break;
            case DashboardProductTypePlan.K_PLAN_AKB:
                html.v(DS.getString(R.string.session_dashboard_by_akb));
                break;
            case DashboardProductTypePlan.K_PLAN_SUCCESS_VISIT:
                html.v(DS.getString(R.string.session_dashboard_by_visit));
                break;
            case DashboardProductTypePlan.K_PLAN_WEIGHT:
                html.v(DS.getString(R.string.session_dashboard_by_weight));
                break;
        }
        return html.i().v(" (").v(!TextUtils.isEmpty(room.id) ? room.name : DS.getString(R.string.all)).v(")").i().html();
    }

    private int getOrderNo(String planType) {
        switch (planType) {
            case DashboardProductTypePlan.K_PLAN_SUM:
                return 0;
            case DashboardProductTypePlan.K_PLAN_QUANT:
                return 1;
            case DashboardProductTypePlan.K_PLAN_AKB:
                return 2;
            case DashboardProductTypePlan.K_PLAN_SUCCESS_VISIT:
                return 3;
            case DashboardProductTypePlan.K_PLAN_WEIGHT:
                return 4;
            default:
                return 9999;
        }
    }

    public static String getKey(String roomId, String planType) {
        return roomId + "#" + planType;
    }

    public static final MyMapper<DashboardProductKpiRow, String> KEY_ADAPTER = new MyMapper<DashboardProductKpiRow, String>() {
        @Override
        public String apply(DashboardProductKpiRow val) {
            return getKey(val.room.id, val.planType);
        }
    };

    public static final UzumAdapter<DashboardProductKpiRow> UZUM_ADAPTER = new UzumAdapter<DashboardProductKpiRow>() {
        @Override
        public DashboardProductKpiRow read(UzumReader in) {
            return new DashboardProductKpiRow(in.readValue(Room.UZUM_ADAPTER),
                    in.readString(), in.readArray(Detail.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DashboardProductKpiRow val) {
            out.write(val.room, Room.UZUM_ADAPTER);
            out.write(val.planType);
            out.write(MyArray.from(val.details), Detail.UZUM_ADAPTER);
        }
    };

    public static class Detail {

        public final String id;
        public final CharSequence name;
        public final BigDecimal fact;
        public final BigDecimal plan;
        public final BigDecimal prediction;

        public Detail(String id, CharSequence name, BigDecimal fact, BigDecimal plan, BigDecimal prediction) {
            this.id = id;
            this.name = name;
            this.fact = fact;
            this.plan = plan;
            this.prediction = prediction;
        }

        public MyArray<ChartLine> generateChartLine(String planType) {
            float planOnePercent = plan.floatValue() / 100F;
            float factPercent = fact.floatValue() / planOnePercent;
            float predictTotalPercent = prediction.floatValue() / planOnePercent;
            float predictionPercent = predictTotalPercent - factPercent;

            float planPercent = 0;
            if ((predictionPercent + factPercent) < 100) {
                planPercent = 100 - (predictionPercent + factPercent);
            }

            float predictionResult = Math.min(predictionPercent, 100);

            if (DashboardProductTypePlan.K_PLAN_AKB.equals(planType)) {
                planPercent = 0;
            }
            return MyArray.from(
                    new ChartLine(Math.round(factPercent),
                            NumberUtil.formatMoney(fact) +
                                    "(" + NumberUtil.formatMoney(new BigDecimal(Math.round(factPercent))) + "%)",
                            DS.getColor(R.color.dashboard_green)),

                    new ChartLine(Math.round(predictionResult),
                            NumberUtil.formatMoney(prediction) +
                                    "(" + NumberUtil.formatMoney(new BigDecimal(Math.round(predictTotalPercent))) + "%)",
                            DS.getColor(R.color.dashboard_prediction)),

                    new ChartLine(Math.round(planPercent),
                            NumberUtil.formatMoney(plan),
                            DS.getColor(R.color.dashboard_plan))

            ).filterNotNull();
        }

        public MyArray<ChartLine> generateChartLineForDialog() {
            float planOnePercent = plan.floatValue() / 100F;
            float factPercent = fact.floatValue() / planOnePercent;
            float predictTotalPercent = prediction.floatValue() / planOnePercent;
            float predictionResult = Math.min(predictTotalPercent, 100);

            return MyArray.from(
                    new ChartLine(Math.round(factPercent),
                            NumberUtil.formatMoney(fact) +
                                    "(" + NumberUtil.formatMoney(new BigDecimal(Math.round(factPercent))) + "%)",
                            DS.getColor(R.color.dashboard_green)),

                    new ChartLine(Math.round(predictionResult),
                            NumberUtil.formatMoney(prediction) +
                                    "(" + NumberUtil.formatMoney(new BigDecimal(Math.round(predictTotalPercent))) + "%)",
                            DS.getColor(R.color.dashboard_prediction)),

                    new ChartLine(Math.round(100F),
                            NumberUtil.formatMoney(plan),
                            DS.getColor(R.color.dashboard_plan))

            ).filterNotNull();
        }

        public static final UzumAdapter<Detail> UZUM_ADAPTER = new UzumAdapter<Detail>() {
            @Override
            public Detail read(UzumReader in) {
                return new Detail(in.readString(),
                        in.readString(), in.readBigDecimal(),
                        in.readBigDecimal(), in.readBigDecimal());
            }

            @Override
            public void write(UzumWriter out, Detail val) {
                out.write(val.id);
                out.write((String) val.name);
                out.write(val.fact);
                out.write(val.plan);
                out.write(val.prediction);
            }
        };
    }

}
