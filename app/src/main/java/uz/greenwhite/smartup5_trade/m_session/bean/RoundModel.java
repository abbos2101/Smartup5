package uz.greenwhite.smartup5_trade.m_session.bean;// 01.07.2016


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;

public class RoundModel {

    public final String model;
    private final boolean half;
    private final char roundType;
    private final BigDecimal ten;

    private RoundModel(String model) {
        this.model = model;

        this.half = model.charAt(3) == '5';

        this.roundType = model.charAt(4);

        BigDecimal scale = new BigDecimal(model.substring(0, 4));
        if (this.half) {
            scale = scale.subtract(new BigDecimal("0.5"));
        }

        this.ten = new BigDecimal(BigInteger.TEN).pow(scale.intValue(), MathContext.DECIMAL64);
    }

    private static final ConcurrentHashMap<String, RoundModel> models = new ConcurrentHashMap<>();

    public static RoundModel make(String model) {
        RoundModel rm = models.get(model);
        if (rm == null) {
            rm = new RoundModel(model);
            models.putIfAbsent(model, rm);
            rm = models.get(model);
        }
        return rm;
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private BigDecimal fixAmountHelper(BigDecimal val) {
        switch (roundType) {
            case 'C':
                return val.multiply(ten).setScale(0, RoundingMode.CEILING).divide(ten);
            case 'R':
                return val.multiply(ten).setScale(0, RoundingMode.HALF_UP).divide(ten);
            case 'F':
                return val.multiply(ten).setScale(0, RoundingMode.FLOOR).divide(ten);
        }
        return null;
    }

    @SuppressWarnings({"ConstantConditions", "BigDecimalMethodWithoutRoundingCalled"})
    public BigDecimal fixAmount(BigDecimal val) {
        if (val == null) {
            return null;
        } else if (val.signum() == -1) {
            return val;
        } else if (val.signum() == 0) {
            return BigDecimal.ZERO;
        } else if (half) {
            BigDecimal two = new BigDecimal(2);
            return fixAmountHelper(val.multiply(two)).divide(two);
        } else {
            return fixAmountHelper(val);
        }
    }
}
