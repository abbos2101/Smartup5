package uz.greenwhite.smartup5_trade.m_person_edit.ui;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.view_setup.DialogBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.common.MyImageView;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_location.arg.ArgMap;
import uz.greenwhite.smartup5_trade.m_location.ui.LocationFragment;
import uz.greenwhite.smartup5_trade.m_person_edit.PersonUtil;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VNaturalPersonAdditionally;
import uz.greenwhite.smartup5_trade.m_session.bean.Hospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Specialty;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class NaturalPersonAdditionallyContent extends MoldPageContent {

    public static MoldPageContent newInstance(ArgPerson arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER);
        return newContentInstance(NaturalPersonAdditionallyContent.class, bundle, title);
    }

    public ArgPerson getArgPerson() {
        return Mold.parcelableArgument(this, ArgPerson.UZUM_ADAPTER);
    }

    private ViewSetup vsRoot;
    private final JobMate jobMate = new JobMate();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, int position) {
        this.vsRoot = new ViewSetup(inflater, null, R.layout.oedit_person);
        return this.vsRoot.view;
    }

    @Override
    public void onContentResume() {
        super.onContentResume();
        reloadContent();
    }

    @Override
    public void reloadContent() {
        if (getActivity() == null || vsRoot == null) return;
        NaturalPersonData data = Mold.getData(getActivity());
        if (data == null) return;

        VNaturalPersonAdditionally person = data.info.additionally;

        ArgPerson arg = getArgPerson();
        Scope scope = arg.getScope();

        MyArray<ViewSetup> rows = MyArray.from(

                !arg.editPerson() || PersonUtil.hasEdit(scope, RoleSetting.KE_SPECIALTY) ?
                        makeSpecialty(getActivity(), DS.getString(R.string.outlet_specialty)) : null,

                PersonUtil.hasVisible(scope, RoleSetting.KE_HOSPITAL) && PersonUtil.hasEdit(scope, RoleSetting.KE_HOSPITAL) ?
                        makeHospital(getActivity(), DS.getString(R.string.outlet_hospital)) : null,

                PersonUtil.hasVisible(scope, RoleSetting.KE_CABINET) && PersonUtil.hasEdit(scope, RoleSetting.KE_CABINET) ?
                        UIUtils.makeEditText(this, R.drawable.info_7, R.string.outlet_cabinet, person.cabinet) : null,

                PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_LOCATION) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_LOCATION) ?
                        makeLocation(getActivity(), DS.getString(R.string.outlet_location), person.vLatLng) : null

        ).filterNotNull();

        ViewGroup vg = vsRoot.viewGroup(R.id.ll_content);
        vg.removeAllViews();
        boolean first = true;
        for (ViewSetup vs : rows) {
            if (!first) {
                vg.addView(new ViewSetup(getActivity(), R.layout.z_divider).view);
            }
            vg.addView(vs.view);
            first = false;
        }
    }

    @Override
    public void onContentDestroy(@Nullable Bundle saveInstanceState) {
        super.onContentDestroy(saveInstanceState);
        jobMate.stopListening();
    }

    private ViewSetup makeSpecialty(final FragmentActivity activity, CharSequence title) {
        final NaturalPersonData data = Mold.getData(getActivity());
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_edittext);
        ((MyImageView) vs.imageView(R.id.miv_icon)).setImageResource(R.drawable.building_icons_for_, R.color.default_icon);
        final EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        ValueSpinner value = data.info.additionally.getSpecialty();
        cValue.setText(value.getValue().name);
        cValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        UIUtils.editTextTouchListener(cValue, new Command() {
            @Override
            public void apply() {
                ValueSpinner value = data.info.additionally.getSpecialty();
                if (value.options.isEmpty() || value.options.size() <= 2) {
                    loadSpecialtys(cValue);
                    return;
                }
                showSpecialtyDialog(cValue, activity);
            }
        });
        return vs;
    }

    private ViewSetup makeHospital(final FragmentActivity activity, CharSequence title) {
        final NaturalPersonData data = Mold.getData(getActivity());
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_edittext);
        ((MyImageView) vs.imageView(R.id.miv_icon)).setImageResource(R.drawable.building_icons_for_, R.color.default_icon);
        final EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        ValueSpinner value = data.info.additionally.getHospital();
        cValue.setText(value.getValue().name);
        cValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        UIUtils.editTextTouchListener(cValue, new Command() {
            @Override
            public void apply() {
                ValueSpinner value = data.info.additionally.getHospital();
                if (value.options.isEmpty() || value.options.size() <= 2) {
                    loadHospitals(cValue);
                    return;
                }
                showHospitalDialog(cValue, activity);
            }
        });
        return vs;
    }


    private ViewSetup makeLocation(final Activity activity, CharSequence title, final ValueString value) {
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_gps_location);
        final EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        UI.bind(cValue, value);
        UIUtils.editTextTouchListener(cValue, null);
        vs.id(R.id.miv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArgMap arg = new ArgMap(getArgPerson(), value.getText());
                if (!SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getContentFragment().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                Mold.addContent(activity, LocationFragment.newInstance(arg));
            }
        });
        return vs;
    }

    private void loadSpecialtys(final EditText cValue) {
        final FragmentActivity activity = getActivity();
        jobMate.executeWithDialog(activity, new ActionJob<>(getArgPerson(), RT.URI_LOAD_SPECIALTY))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            MyArray<Specialty> hospitals = Uzum.toValue(result, Specialty.UZUM_ADAPTER.toArray());
                            if (hospitals.nonEmpty()) {
                                NaturalPersonData data = Mold.getData(getActivity());
                                data.info.additionally.makeSpecialty(hospitals);
                                showSpecialtyDialog(cValue, activity);
                            } else
                                Mold.makeSnackBar(getActivity(), R.string.hospital_not_found).show();
                        } else {
                            Mold.makeSnackBar(activity, ErrorUtil.getErrorMessage(error).message).show();
                        }
                    }
                });
    }

    private void loadHospitals(final EditText cValue) {
        final FragmentActivity activity = getActivity();
        jobMate.executeWithDialog(activity, new ActionJob<>(getArgPerson(), RT.URI_LOAD_HOSPITALS))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            MyArray<Hospital> hospitals = Uzum.toValue(result, Hospital.UZUM_ADAPTER.toArray());
                            if (hospitals.nonEmpty()) {
                                NaturalPersonData data = Mold.getData(getActivity());
                                data.info.additionally.makeHospital(hospitals);
                                showHospitalDialog(cValue, activity);
                            } else
                                Mold.makeSnackBar(getActivity(), R.string.hospital_not_found).show();
                        } else {
                            Mold.makeSnackBar(activity, ErrorUtil.getErrorMessage(error).message).show();
                        }
                    }
                });
    }

    private void showHospitalDialog(final EditText cValue, FragmentActivity activity) {
        NaturalPersonData data = Mold.getData(getActivity());
        final ValueSpinner value = data.info.additionally.getHospital();
        UI.dialog().title(R.string.select)
                .option(value.options, new DialogBuilder.CommandFacade<SpinnerOption>() {

                    @Override
                    public CharSequence getName(SpinnerOption val) {
                        return val.name;
                    }

                    @Override
                    public void apply(SpinnerOption val) {
                        value.setValue(val);
                        cValue.setText(val.name);
                    }
                }).show(activity);
    }

    private void showSpecialtyDialog(final EditText cValue, FragmentActivity activity) {
        NaturalPersonData data = Mold.getData(getActivity());
        final ValueSpinner value = data.info.additionally.getSpecialty();
        UI.dialog().title(R.string.select)
                .option(value.options, new DialogBuilder.CommandFacade<SpinnerOption>() {

                    @Override
                    public CharSequence getName(SpinnerOption val) {
                        return val.name;
                    }

                    @Override
                    public void apply(SpinnerOption val) {
                        value.setValue(val);
                        cValue.setText(val.name);
                    }
                }).show(activity);
    }
}
