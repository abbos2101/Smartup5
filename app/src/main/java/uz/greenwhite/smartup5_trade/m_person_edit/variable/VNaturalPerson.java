package uz.greenwhite.smartup5_trade.m_person_edit.variable;// 23.12.2016

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.LegalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPerson;
import uz.greenwhite.smartup5_trade.m_session.bean.Hospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class VNaturalPerson extends VariableLike {

    private final String personId;

    public final ValueString name;
    public final ValueString surname;
    public final ValueString patronymic;
    public final ValueString gender;
    public final ValueString phone;
    public final ValueString birthday;
    public final ValueString email;
    public final ValueString address;
    public final ValueString code;

    public VNaturalPerson(NaturalPerson person) {
        this.personId = person.personId;
        this.name = new ValueString(100, person.name);
        this.surname = new ValueString(50, person.surname);
        this.patronymic = new ValueString(50, person.patronymic);
        this.gender = new ValueString(1, person.gender);
        this.phone = new ValueString(15, person.phone);
        this.birthday = new ValueString(10, person.birthday);
        this.email = new ValueString(100, person.email);
        this.address = new ValueString(100, person.address);
        this.code = new ValueString(50, person.code);
    }

    public NaturalPerson toValue() {
        return new NaturalPerson(personId,
                name.getText(),
                surname.getText(),
                patronymic.getText(),
                gender.getText(),
                phone.getText(),
                birthday.getText(),
                email.getText(),
                address.getText(),
                code.getText());
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(name, surname, patronymic, gender, phone, birthday,
                email, address, code).toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }

        if ((name.isEmpty())) {
            return ErrorResult.make(DS.getString(R.string.fill_in_required_fields));
        }
        return ErrorResult.NONE;
    }
}
