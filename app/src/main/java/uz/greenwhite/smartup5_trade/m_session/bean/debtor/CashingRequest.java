package uz.greenwhite.smartup5_trade.m_session.bean.debtor;


import java.math.BigDecimal;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CashingRequest {

    public static final String K_WAITING = "W";
    public static final String K_POSTED = "P";
    public static final String K_ABORT = "A";

    public final String outletId;
    public final String dealId;
    public final String currencyId;
    public final String state;
    public final BigDecimal amount;
    public final String requestDate;

    public CashingRequest(String outletId, String dealId, String currencyId, String state, BigDecimal amount, String requestDate) {
        this.outletId = outletId;
        this.dealId = dealId;
        this.currencyId = currencyId;
        this.state = state;
        this.amount = amount;
        this.requestDate = Util.nvl(requestDate);
    }


    public static final UzumAdapter<CashingRequest> UZUM_ADAPTER = new UzumAdapter<CashingRequest>() {
        @Override
        public CashingRequest read(UzumReader in) {
            return new CashingRequest(
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readBigDecimal(), in.readString());
        }

        @Override
        public void write(UzumWriter out, CashingRequest val) {
            out.write(val.outletId);
            out.write(val.dealId);
            out.write(val.currencyId);
            out.write(val.state);
            out.write(val.amount);
            out.write(val.requestDate);
        }
    };
}
