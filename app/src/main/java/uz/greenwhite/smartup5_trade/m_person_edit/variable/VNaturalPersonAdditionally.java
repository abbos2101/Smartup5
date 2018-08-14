package uz.greenwhite.smartup5_trade.m_person_edit.variable;// 23.12.2016

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

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
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPersonAdditionally;
import uz.greenwhite.smartup5_trade.m_session.bean.Hospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Specialty;

public class VNaturalPersonAdditionally extends VariableLike {

    private ValueSpinner specialty;
    private ValueSpinner hospital;
    public final ValueString cabinet;
    public final ValueString vLatLng;

    public VNaturalPersonAdditionally(NaturalPersonAdditionally additionally) {
        this.specialty = makeSpecialty(additionally.specialty);
        this.hospital = makeHospital(additionally.hospital);
        this.cabinet = new ValueString(100, additionally.cabinet);
        this.vLatLng = new ValueString(100, "-1".equals(additionally.latLng) ? "" : additionally.latLng);
    }

    public NaturalPersonAdditionally toValue() {
        SpinnerOption specialtyValue = this.specialty.getValue();
        SpinnerOption hospitalValue = this.hospital.getValue();
        Specialty specialty = specialtyValue == null ? null : (Specialty) specialtyValue.tag;
        Hospital hospital = hospitalValue == null ? null : (Hospital) hospitalValue.tag;
        String latLng = vLatLng.getText();
        if (TextUtils.isEmpty(latLng)) {
            latLng = "-1";
        }
        return new NaturalPersonAdditionally(specialty, hospital, cabinet.getText(), latLng);
    }

    private ValueSpinner makeSpecialty(Specialty specialty) {
        MyArray<SpinnerOption> options = MyArray.from(new SpinnerOption("", ""));

        SpinnerOption option = options.get(0);
        if (specialty != null && specialty != Specialty.DEFAULT && !TextUtils.isEmpty(specialty.id)) {
            options = options.append(new SpinnerOption(specialty.id, specialty.name, specialty));
            option = options.get(1);
        }
        return new ValueSpinner(options, option);
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

    public ValueSpinner getSpecialty() {
        return specialty;
    }

    public ValueSpinner getHospital() {
        return hospital;
    }

    public void makeSpecialty(MyArray<Specialty> results) {
        MyArray<SpinnerOption> options = results.map(new MyMapper<Specialty, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Specialty specialty) {
                return new SpinnerOption(specialty.id, specialty.name, specialty);
            }
        });
        options = options.prepend(new SpinnerOption("", ""));

        SpinnerOption value = specialty.getValue();
        SpinnerOption find;
        if ((find = options.find(value.code, SpinnerOption.KEY_ADAPTER)) == null) {
            options = options.append(value);
        }

        this.specialty = new ValueSpinner(options, Util.nvl(find, value));
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
        return MyArray.from(specialty, hospital, cabinet, vLatLng).toSuper();
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = super.getError();
        if (error.isError()) return error;

        SpinnerOption value = specialty.getValue();
        if (TextUtils.isEmpty(value.code)) {
            return ErrorResult.make(DS.getString(R.string.person_label_speciality_is_required));
        }

        try {
            String latLng = vLatLng.getText();
            if (!TextUtils.isEmpty(latLng)) {
                String[] split = latLng.split(",");
                new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
            }
        } catch (Exception e) {
            return ErrorResult.make(DS.getString(R.string.person_location_format_incorrect));
        }

        return error;
    }
}
