package uz.greenwhite.smartup5_trade.m_session.bean.person;

import java.math.BigDecimal;

import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonLastDebt {

    public final String personId;
    public final String paymentTypeId;
    public final BigDecimal amount;
    public final String expireDate;

    public PersonLastDebt(String personId, String paymentTypeId, BigDecimal amount, String expireDate) {
        this.personId = personId;
        this.paymentTypeId = paymentTypeId;
        this.amount = amount;
        this.expireDate = expireDate;
    }

    public static final UzumAdapter<PersonLastDebt> UZUM_ADAPTER = new UzumAdapter<PersonLastDebt>() {
        @Override
        public PersonLastDebt read(UzumReader in) {
            return new PersonLastDebt(in.readString(), in.readString(),
                    in.readBigDecimal(), in.readString());
        }

        @Override
        public void write(UzumWriter out, PersonLastDebt val) {
            out.write(val.personId);
            out.write(val.paymentTypeId);
            out.write(val.amount);
            out.write(val.expireDate);
        }
    };
}
