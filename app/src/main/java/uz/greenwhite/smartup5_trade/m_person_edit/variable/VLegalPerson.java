package uz.greenwhite.smartup5_trade.m_person_edit.variable;// 23.12.2016

import android.text.TextUtils;

import java.util.ArrayList;

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
import uz.greenwhite.smartup5_trade.m_session.bean.Hospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;

public class VLegalPerson extends VariableLike {

    public final ArrayList<String> requiredCodes = new ArrayList<>();

    private final String personId;

    public final ValueString name;
    public final ValueString shortName;
    public final ValueString address;
    public final ValueString addressGuide;
    public final ValueString code;
    public final ValueString phone;
    public final ValueString email;
    public final ValueString location;
    public final ValueString barcode;
    private ValueSpinner region;
    private ValueSpinner hospital;
    public final ValueString vZipCode;

    public VLegalPerson(LegalPerson person) {
        this.personId = person.personId;
        this.name = new ValueString(300, person.name);
        this.shortName = new ValueString(300, person.shortName);
        this.address = new ValueString(300, person.address);
        this.addressGuide = new ValueString(300, person.addressGuide);
        this.code = new ValueString(20, person.code);
        this.phone = new ValueString(15, person.phone);
        this.email = new ValueString(100, person.email);
        this.location = new ValueString(50, person.location);
        this.barcode = new ValueString(50, person.barcode);
        this.vZipCode = new ValueString(50, person.zipCode);
        this.region = makeRegion(person.region);
        this.hospital = makeHospital(person.hospital);
    }

    public LegalPerson toValue() {
        String personPhone = phone.getText();
        SpinnerOption regionValue = this.region.getValue();
        SpinnerOption hospitalValue = this.hospital.getValue();
        Region region = regionValue == null ? null : (Region) regionValue.tag;
        Hospital hospital = hospitalValue == null ? null : (Hospital) hospitalValue.tag;
        return new LegalPerson(personId,
                name.getText(),
                code.getText(),
                address.getText(),
                addressGuide.getText(),
                personPhone,
                email.getText(),
                location.getText(),
                barcode.getText(),
                region,
                hospital,
                shortName.getText(),
                vZipCode.getText());
    }

    private ValueSpinner makeRegion(Region region) {
        MyArray<SpinnerOption> regions = MyArray.from(new SpinnerOption("", ""));

        SpinnerOption option = regions.get(0);
        if (region != null && region != Region.DEFAULT && !TextUtils.isEmpty(region.regionId)) {
            regions = regions.append(new SpinnerOption(region.regionId, region.name, region));
            option = regions.get(1);
        }
        return new ValueSpinner(regions, option);
    }

    private ValueSpinner makeHospital(Hospital hospital) {
        MyArray<SpinnerOption> options = MyArray.from(new SpinnerOption("", ""));

        SpinnerOption option = options.get(0);
        if (hospital != null && hospital != Hospital.DEFAULT && !TextUtils.isEmpty(hospital.id)) {
            options = options.append(new SpinnerOption(hospital.id, hospital.name, hospital));
            option = options.get(1);
        }
        return new ValueSpinner(options, option);
    }

    public ValueSpinner getRegion() {
        return region;
    }

    public ValueSpinner getHospital() {
        return hospital;
    }

    public void makeRegion(MyArray<Region> regions) {
        MyArray<SpinnerOption> options = regions.map(new MyMapper<Region, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Region region) {
                return new SpinnerOption(region.regionId, region.name, region);
            }
        });
        options = options.prepend(new SpinnerOption("", ""));

        SpinnerOption value = region.getValue();
        SpinnerOption find;
        if ((find = options.find(value.code, SpinnerOption.KEY_ADAPTER)) == null) {
            options = options.append(value);
        }

        this.region = new ValueSpinner(options, Util.nvl(find, value));
    }

    public void makeHospital(MyArray<Hospital> items) {
        MyArray<SpinnerOption> options = items.map(new MyMapper<Hospital, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Hospital item) {
                return new SpinnerOption(item.id, item.name, item);
            }
        });
        options = options.prepend(new SpinnerOption("", ""));

        SpinnerOption value = hospital.getValue();
        SpinnerOption find;
        if ((find = options.find(value.code, SpinnerOption.KEY_ADAPTER)) == null) {
            options = options.append(value);
        }

        this.hospital = new ValueSpinner(options, Util.nvl(find, value));
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(name, code, phone, email, location).toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) {
            return error;
        }

        if (name.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_name)));
        }

//        if (requiredCodes.contains(RoleSetting.KE_OUTLET_NAME) && name.isEmpty()) {
//            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_name)));
//        }

        if (requiredCodes.contains(RoleSetting.KE_PERSON_SHORT_NAME) && shortName.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_short_name)));
        }

        if (requiredCodes.contains(RoleSetting.KE_OUTLET_ADDRESS) && address.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_address)));
        }

        if (requiredCodes.contains(RoleSetting.KE_OUTLET_ADDRESS_GUIDE) && addressGuide.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_address_guide)));
        }

        if (requiredCodes.contains(RoleSetting.KE_OUTLET_CODE) && code.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_code)));
        }

        if (requiredCodes.contains(RoleSetting.KE_OUTLET_PHONE) && phone.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_phone)));
        }

        if (requiredCodes.contains(RoleSetting.KE_OUTLET_LOCATION) && location.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_location)));
        }

        if (requiredCodes.contains(RoleSetting.KE_OUTLET_BARCODE) && barcode.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.barcode)));
        }

        if (requiredCodes.contains(RoleSetting.KE_OUTLET_REGION) && region.getText().isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_this_required_field, DS.getString(R.string.outlet_region)));
        }

        return ErrorResult.NONE;
    }
}
