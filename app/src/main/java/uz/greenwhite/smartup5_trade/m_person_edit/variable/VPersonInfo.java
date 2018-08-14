package uz.greenwhite.smartup5_trade.m_person_edit.variable;


import java.util.ArrayList;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_person_edit.PersonUtil;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.GroupType;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.LegalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAddress;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonProps;

public class VPersonInfo extends VariableLike {

    public final String filialId;
    public final String roomId;
    public final VLegalPerson person;
    public final VCharactsGroup personCharacts;
    public final VPersonAddress address;
    public final VPersonAccount account;
    public final VOutletProps props;
    public final String personKind;

    public VPersonInfo(PersonInfo info,
                       VLegalPerson person,
                       VCharactsGroup personCharacts,
                       VPersonAddress address,
                       VPersonAccount account,
                       VOutletProps props,
                       String personKind) {
        this.filialId = info.filialId;
        this.roomId = info.roomId;
        this.person = person;
        this.personCharacts = personCharacts;
        this.address = address;
        this.account = account;
        this.props = props;
        this.personKind = personKind;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(person, personCharacts, address, account, props).toSuper();
    }

    public PersonInfo toValue() {
        LegalPerson person = this.person.toValue();
        PersonProps props = this.props.toValue();
        MyArray<GroupType> personCharacts = this.personCharacts.toValue();
        MyArray<PersonAddress> personsAddress = this.address.toValue();
        MyArray<PersonAccount> personAccounts = this.account.toValue();
        return new PersonInfo(this.filialId, this.roomId, person, props,
                personCharacts, personsAddress, personAccounts, personKind);
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) return error;

        error = address.getError();
        if (error.isError()) return error;
        error = account.getError();
        if (error.isError()) return error;

        return error;
    }
}
