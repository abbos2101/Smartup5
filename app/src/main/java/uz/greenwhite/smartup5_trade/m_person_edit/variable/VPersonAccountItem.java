package uz.greenwhite.smartup5_trade.m_person_edit.variable;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.Bank;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;

public class VPersonAccountItem extends VariableLike {

    private String bankAccountId;
    private Bank bank;
    private ValueSpinner currency;
    public final ValueString personAccount;

    public VPersonAccountItem(PersonAccount account) {
        this.bankAccountId = account.bankAccountId;
        this.bank = account.bank;
        this.currency = makeCurrency(account.currency);
        this.personAccount = new ValueString(50, account.personAccount);
    }

    private ValueSpinner makeCurrency(Currency currency) {
        MyArray<SpinnerOption> currencys = MyArray.from(DEFAULT);
        SpinnerOption option = currencys.get(0);

        if (!TextUtils.isEmpty(currency.currencyId) && !currency.currencyId.equals(DEFAULT.code)) {
            currencys = currencys.append(new SpinnerOption(currency.currencyId, currency.getName(), currency));
            option = currencys.get(1);
        }
        return new ValueSpinner(currencys, option);
    }

    public String getBankName() {
        return bank != null && !TextUtils.isEmpty(bank.bankId) ? bank.toString() : "";
    }

    public ValueSpinner getCurrency() {
        return currency;
    }

    public void setCurrency(MyArray<Currency> currencys) {
        MyArray<SpinnerOption> options = currencys.map(new MyMapper<Currency, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Currency currency) {
                return new SpinnerOption(currency.currencyId, currency.getName(), currency);
            }
        });
        if (!currencys.contains(DEFAULT.code, Currency.KEY_ADAPTER)) {
            options = options.prepend(DEFAULT);
        }

        SpinnerOption value = currency.getValue();
        SpinnerOption find;
        if ((find = options.find(value.code, SpinnerOption.KEY_ADAPTER)) == null) {
            options = options.append(value);
        }
        this.currency = new ValueSpinner(options, Util.nvl(find, value));
    }

    public void setBank(Bank bank) {
        if (bank == null) throw AppError.NullPointer();
        this.bank = bank;
    }

    public PersonAccount toValue() {
        return new PersonAccount(bankAccountId, bank,
                (Currency) currency.getValue().tag, personAccount.getText());
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(personAccount, currency).toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }
        if (bank == null || TextUtils.isEmpty(bank.bankId)) {
            return ErrorResult.make(DS.getString(R.string.fill_in_required_fields));
        }
        if (personAccount.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_in_required_fields));
        }
        return ErrorResult.NONE;
    }

    public final SpinnerOption DEFAULT = new SpinnerOption("0", DS.getString(R.string.basic));

}
