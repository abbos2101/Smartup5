package uz.greenwhite.smartup5_trade.m_person_edit.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.GroupType;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.LegalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPersonAdditionally;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAccount;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAddress;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonProps;

public class VNaturalPersonInfo extends VariableLike {

    public final String filialId;
    public final String roomId;
    public final VNaturalPerson person;
    public final VNaturalPersonAdditionally additionally;
    public final VCharactsGroup personCharacts;

    public VNaturalPersonInfo(NaturalPersonInfo info,
                              VNaturalPerson person,
                              VNaturalPersonAdditionally additionally,
                              VCharactsGroup personCharacts) {
        this.filialId = info.filialId;
        this.roomId = info.roomId;
        this.person = person;
        this.additionally = additionally;
        this.personCharacts = personCharacts;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(person, additionally, personCharacts).toSuper();
    }

    public NaturalPersonInfo toValue() {
        NaturalPerson person = this.person.toValue();
        NaturalPersonAdditionally additionally = this.additionally.toValue();
        MyArray<GroupType> personCharacts = this.personCharacts.toValue();
        return new NaturalPersonInfo(this.filialId, this.roomId, person, additionally, personCharacts);
    }
}
