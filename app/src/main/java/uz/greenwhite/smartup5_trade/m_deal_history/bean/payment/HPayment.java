package uz.greenwhite.smartup5_trade.m_deal_history.bean.payment;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class HPayment {

    public final String currencyId;
    public final String paymentTypeId;
    public final String amount;
    public final String consignAmount;
    public final String consignDate;

    public HPayment(String currencyId, String paymentTypeId, String amount, String consignAmount, String consignDate) {
        this.currencyId = currencyId;
        this.paymentTypeId = paymentTypeId;
        this.amount = amount;
        this.consignAmount = consignAmount;
        this.consignDate = consignDate;
    }

    public static final UzumAdapter<HPayment> UZUM_ADAPTER = new UzumAdapter<HPayment>() {
        @Override
        public HPayment read(UzumReader in) {
            return new HPayment(in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter uzumWriter, HPayment hPayment) {
            uzumWriter.write(hPayment.currencyId);
            uzumWriter.write(hPayment.paymentTypeId);
            uzumWriter.write(hPayment.amount);
            uzumWriter.write(hPayment.consignAmount);
            uzumWriter.write(hPayment.consignDate);
        }
    };
}
