package uz.greenwhite.smartup5_trade.m_deal.variable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class OrderRecom {

    public final int recomDay;
    public final double recomCoef;
    public final long recomData;
    public final long lookUpDay;

    public OrderRecom(int recomDay, double recomCoef, long recomData, long lookUpDay) {
        this.recomDay = recomDay;
        this.recomCoef = recomCoef;
        this.recomData = recomData;
        this.lookUpDay = lookUpDay;
    }

    public OrderRecom(Integer recomDay, String recomCoef, String recomData, String lookUpDay) {
        this(recomDay, Double.parseDouble(recomCoef), Long.parseLong(recomData), Long.parseLong(lookUpDay));
    }

    public String calcRecom(double stock) {
        if (lookUpDay <= 0) {
            return "";
        }
        double tmp = (recomData - stock) * recomDay;
        double result = (double) Math.round(tmp * (recomCoef + 100) / 100 / lookUpDay) - stock;

        if (result < 0) {
            return "";
        } else {
            return FORMAT.get().format(result);
        }
    }

    private static final ThreadLocal<DecimalFormat> FORMAT = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(0);
            df.setMaximumFractionDigits(3);
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            return df;
        }
    };
}
