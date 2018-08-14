package uz.greenwhite.smartup5_trade.m_session.bean.debtor;

import java.math.BigDecimal;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DebtorDeal {

    public final String dealId;
    public final String roomId;
    public final String contractId;
    public final String paymentTypeId;
    public final String currencyId;
    public final String userName;
    public final String expeditorName;
    public final String expiryDate;
    public final BigDecimal amount;
    public final String dealDeliveryDate;

    public DebtorDeal(String dealId,
                      String roomId,
                      String contractId,
                      String paymentTypeId,
                      String currencyId,
                      String userName,
                      String expeditorName,
                      String expiryDate,
                      BigDecimal amount,
                      String dealDeliveryDate) {
        this.dealId = dealId;
        this.roomId = roomId;
        this.contractId = contractId;
        this.paymentTypeId = paymentTypeId;
        this.currencyId = currencyId;
        this.userName = userName;
        this.expeditorName = expeditorName;
        this.expiryDate = expiryDate;
        this.amount = amount;
        this.dealDeliveryDate = Util.nvl(dealDeliveryDate, expiryDate);
    }

    public static final UzumAdapter<DebtorDeal> UZUM_ADAPTER = new UzumAdapter<DebtorDeal>() {
        @Override
        public DebtorDeal read(UzumReader in) {
            return new DebtorDeal(in.readString(), in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readBigDecimal(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, DebtorDeal val) {
            out.write(val.dealId);
            out.write(val.roomId);
            out.write(val.contractId);
            out.write(val.paymentTypeId);
            out.write(val.currencyId);
            out.write(val.userName);
            out.write(val.expeditorName);
            out.write(val.expiryDate);
            out.write(val.amount);
            out.write(val.dealDeliveryDate);
        }
    };
}
