package uz.greenwhite.smartup5_trade.m_shipped.bean;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class SService {

    public final String currencyId;
    public final BigDecimal amount;

    public SService(String currencyId, BigDecimal amount) {
        this.currencyId = currencyId;
        this.amount = amount;
    }

    public static final MyMapper<SService, String> KEY_ADAPTER = new MyMapper<SService, String>() {
        @Override
        public String apply(SService val) {
            return val.currencyId;
        }
    };

    public static final UzumAdapter<SService> UZUM_ADAPTER = new UzumAdapter<SService>() {
        @Override
        public SService read(UzumReader in) {
            return new SService(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, SService val) {
            out.write(val.currencyId);
            out.write(val.amount);
        }
    };
}
