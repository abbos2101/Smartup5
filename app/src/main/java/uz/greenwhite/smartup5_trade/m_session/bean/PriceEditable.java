package uz.greenwhite.smartup5_trade.m_session.bean;// 15.12.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PriceEditable {

    private static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    public final String priceTypeId;
    public final boolean editable;
    public final BigDecimal min;
    public final BigDecimal max;

    public PriceEditable(String priceTypeId, boolean editable, BigDecimal min, BigDecimal max) {
        this.priceTypeId = priceTypeId;
        this.editable = editable;
        this.min = min;
        this.max = max;
    }

    public static PriceEditable makeDefault(String priceTypeId) {
        return new PriceEditable(priceTypeId, false, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public BigDecimal getMinimumAmount(BigDecimal amount) {
        if (!this.editable) {
            return amount;
        }
        BigDecimal divide = amount.divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal multiply = divide.multiply(this.min);
        BigDecimal r = amount.subtract(multiply);
        if (r.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        } else {
            return r;
        }
    }

    public BigDecimal getMaximumAmount(BigDecimal amount) {
        if (!this.editable) {
            return amount;
        }
        BigDecimal divide = amount.divide(HUNDRED, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal multiply = divide.multiply(this.max);
        return amount.add(multiply);
    }

    public static final MyMapper<PriceEditable, String> KEY_ADAPTER = new MyMapper<PriceEditable, String>() {
        @Override
        public String apply(PriceEditable val) {
            return val.priceTypeId;
        }
    };

    public static final UzumAdapter<PriceEditable> UZUM_ADAPTER = new UzumAdapter<PriceEditable>() {
        @Override
        public PriceEditable read(UzumReader in) {
            return new PriceEditable(in.readString(), in.readBoolean(),
                    in.readBigDecimal(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, PriceEditable val) {
            out.write(val.priceTypeId);
            out.write(val.editable);
            out.write(val.min);
            out.write(val.max);
        }
    };
}
