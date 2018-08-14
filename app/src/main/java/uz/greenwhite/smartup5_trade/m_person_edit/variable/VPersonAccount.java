package uz.greenwhite.smartup5_trade.m_person_edit.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditAccount;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.PersonData;
import uz.greenwhite.smartup5_trade.m_session.bean.Currency;

public class VPersonAccount extends VariableLike {

    public ValueArray<VPersonAccountItem> accountItem;

    public VPersonAccount(MyArray<uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount> account) {
        this.accountItem = new ValueArray<>(makeAccountItem(account));
    }

    private MyArray<VPersonAccountItem> makeAccountItem(MyArray<uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount> account) {
        return account.map(new MyMapper<uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount, VPersonAccountItem>() {
            @Override
            public VPersonAccountItem apply(uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount val) {
                return new VPersonAccountItem(val);
            }
        });
    }

    public void makeCurrency(MyArray<Currency> currency) {
        if (currency.isEmpty()) return;
        for (VPersonAccountItem item : accountItem.getItems()) {
            item.setCurrency(currency);
        }
    }

    public void removeAccount(VPersonAccountItem val) {
        accountItem.delete(val);
    }

    public void addNewAccount(PersonData data) {
        VPersonAccountItem item = new VPersonAccountItem(uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount.DEFAULT);
        PersonEditAccount account = data.getAccount();
        item.setCurrency(account.currencies);
        accountItem.append(item);
    }

    public MyArray<uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount> toValue() {
        return accountItem.getItems().map(new MyMapper<VPersonAccountItem, uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount>() {
            @Override
            public uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount apply(VPersonAccountItem item) {
                return item.toValue();
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(accountItem).toSuper();
    }

}
