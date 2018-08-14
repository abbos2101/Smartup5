
package uz.greenwhite.smartup5_trade.m_person_edit.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;

public class PersonEditAccount {

    public final MyArray<Bank> banks;
    public final MyArray<Currency> currencies;

    public PersonEditAccount(MyArray<Bank> banks, MyArray<Currency> currencies) {
        this.banks = MyArray.nvl(banks);
        this.currencies = MyArray.nvl(currencies);
    }

    public static final PersonEditAccount DEFAULT = new PersonEditAccount(null, null);

    public static final UzumAdapter<PersonEditAccount> UZUM_ADAPTER = new UzumAdapter<PersonEditAccount>() {
        @Override
        public PersonEditAccount read(UzumReader in) {
            return new PersonEditAccount(in.readArray(Bank.UZUM_ADAPTER), in.readArray(Currency.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PersonEditAccount val) {
            out.write(val.banks, Bank.UZUM_ADAPTER);
            out.write(val.currencies, Currency.UZUM_ADAPTER);
        }
    };

}
