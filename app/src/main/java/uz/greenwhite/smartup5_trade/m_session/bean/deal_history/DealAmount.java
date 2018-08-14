package uz.greenwhite.smartup5_trade.m_session.bean.deal_history;


import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealAmount {

    public final String currencyId;
    public final BigDecimal totalAmount;

    DealAmount(String currencyId, BigDecimal totalAmount) {
        this.currencyId = currencyId;
        this.totalAmount = totalAmount;
    }

    public static final UzumAdapter<DealAmount> UZUM_ADAPTER = new UzumAdapter<DealAmount>() {
        @Override
        public DealAmount read(UzumReader in) {
            return new DealAmount(in.readString(), in.readBigDecimal());
        }

        @Override
        public void write(UzumWriter out, DealAmount val) {
            out.write(val.currencyId);
            out.write(val.totalAmount);
        }
    };
}
