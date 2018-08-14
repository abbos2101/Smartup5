package uz.greenwhite.smartup5_trade.m_person_edit.ui;// 23.12.2016

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
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
import uz.greenwhite.smartup5_trade.BarcodeUtil;
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
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgRegion;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VLegalPerson;
import uz.greenwhite.smartup5_trade.m_session.bean.Hospital;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;


public class LegalPersonContent extends MoldPageContent {

    public static MoldPageContent newInstance(ArgPerson arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER);
        return newContentInstance(LegalPersonContent.class, bundle, title);
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
        if (getActivity() == null) return;
        PersonData data = Mold.getData(getActivity());
        if (data == null) return;

        VLegalPerson person = data.info.person;

        ArgPerson arg = getArgPerson();
        Scope scope = arg.getScope();

        ViewSetup vsName = !arg.editPerson() || (PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_NAME) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_NAME)) ?
                UIUtils.makeEditText(this, R.drawable.info_1, R.string.outlet_legal_person_required, person.name) : null;

        ViewSetup vsShortName = !arg.editPerson() || (PersonUtil.hasVisible(scope, RoleSetting.KE_PERSON_SHORT_NAME) && PersonUtil.hasEdit(scope, RoleSetting.KE_PERSON_SHORT_NAME)) ?
                UIUtils.makeEditText(this, R.drawable.info_1, R.string.outlet_legal_person_short_name, person.shortName) : null;

        ViewSetup vsAddress = PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_ADDRESS) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_ADDRESS) ?
                UIUtils.makeEditText(this, R.drawable.info_2, R.string.outlet_address, person.address) : null;

        ViewSetup vsAddressGuide = PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_ADDRESS_GUIDE) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_ADDRESS_GUIDE) ?
                UIUtils.makeEditText(this, R.drawable.info_3, R.string.outlet_address_guide, person.addressGuide) : null;

        ViewSetup vsRegion = PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_REGION) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_REGION) ?
                makeRegion(getActivity(), DS.getString(R.string.outlet_region)) : null;

        ViewSetup vsHospital = PersonInfo.K_PHARMACY.equals(arg.personKind) && PersonUtil.hasVisible(scope, RoleSetting.KE_HOSPITAL) && PersonUtil.hasEdit(scope, RoleSetting.KE_HOSPITAL) ?
                makeHospital(getActivity(), DS.getString(R.string.outlet_hospital)) : null;

        ViewSetup vsLocation = PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_LOCATION) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_LOCATION) ?
                makeLocation(getActivity(), DS.getString(R.string.outlet_location), person.location) : null;

        ViewSetup vsPhone = PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_PHONE) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_PHONE) ?
                UIUtils.makeEditText(this, R.drawable.info_6, R.string.outlet_phone, person.phone, InputType.TYPE_CLASS_PHONE) : null;

        ViewSetup vsEmail = PersonUtil.hasVisible(scope, RoleSetting.KE_PERSON_EMAIL) && PersonUtil.hasEdit(scope, RoleSetting.KE_PERSON_EMAIL) ?
                UIUtils.makeEditText(this, R.drawable.ic_email_black_24dp, R.string.outlet_email, person.email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) : null;

        ViewSetup vsCode = PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_CODE) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_CODE) ?
                UIUtils.makeEditText(this, R.drawable.info_7, R.string.outlet_code, person.code) : null;

        ViewSetup vsZipCode = PersonUtil.hasVisible(scope, RoleSetting.KE_ZIP_CODE) && PersonUtil.hasEdit(scope, RoleSetting.KE_ZIP_CODE) ?
                UIUtils.makeEditText(this, R.drawable.info_7, R.string.outlet_zip_code, person.vZipCode) : null;

        ViewSetup vsBarcode = PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_BARCODE) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_BARCODE) ?
                makeBarcode(getActivity(), DS.getString(R.string.outlet_barcode), person.barcode) : null;

        ViewSetup vsParent = PersonUtil.hasVisible(scope, RoleSetting.KE_PERSON_PARENT_ID) && PersonUtil.hasEdit(scope, RoleSetting.KE_PERSON_PARENT_ID) ?
                makeParent(getActivity(), data, DS.getString(R.string.outlet_bound_parent)) : null;

        if (vsShortName != null && PersonUtil.isRequired(scope, RoleSetting.KE_PERSON_SHORT_NAME)) {
            vsShortName.editText(R.id.value).setHint(R.string.outlet_legal_person_short_name_required);
            person.requiredCodes.add(RoleSetting.KE_PERSON_SHORT_NAME);
        }

        if (vsAddress != null && PersonUtil.isRequired(scope, RoleSetting.KE_OUTLET_ADDRESS)) {
            vsAddress.editText(R.id.value).setHint(R.string.outlet_address_required);
            person.requiredCodes.add(RoleSetting.KE_OUTLET_ADDRESS);
        }

        if (vsAddressGuide != null && PersonUtil.isRequired(scope, RoleSetting.KE_OUTLET_ADDRESS_GUIDE)) {
            vsAddressGuide.editText(R.id.value).setHint(R.string.outlet_address_guide_required);
            person.requiredCodes.add(RoleSetting.KE_OUTLET_ADDRESS_GUIDE);
        }

        if (vsRegion != null && PersonUtil.isRequired(scope, RoleSetting.KE_OUTLET_REGION)) {
            vsRegion.editText(R.id.value).setHint(R.string.outlet_region_required);
            person.requiredCodes.add(RoleSetting.KE_OUTLET_REGION);
        }

        if (vsHospital != null && PersonUtil.isRequired(scope, RoleSetting.KE_HOSPITAL)) {
            vsHospital.editText(R.id.value).setHint(R.string.outlet_hospital_required);
            person.requiredCodes.add(RoleSetting.KE_HOSPITAL);
        }

        if (vsLocation != null && PersonUtil.isRequired(scope, RoleSetting.KE_OUTLET_LOCATION)) {
            vsLocation.editText(R.id.value).setHint(R.string.outlet_location_required);
            person.requiredCodes.add(RoleSetting.KE_OUTLET_LOCATION);
        }

        if (vsPhone != null && PersonUtil.isRequired(scope, RoleSetting.KE_OUTLET_PHONE)) {
            vsPhone.editText(R.id.value).setHint(R.string.outlet_phone_required);
            person.requiredCodes.add(RoleSetting.KE_OUTLET_PHONE);
        }

        if (vsCode != null && PersonUtil.isRequired(scope, RoleSetting.KE_OUTLET_CODE)) {
            vsCode.editText(R.id.value).setHint(R.string.outlet_code_required);
            person.requiredCodes.add(RoleSetting.KE_OUTLET_CODE);
        }

        if (vsZipCode != null && PersonUtil.isRequired(scope, RoleSetting.KE_ZIP_CODE)) {
            vsZipCode.editText(R.id.value).setHint(R.string.outlet_zip_code_required);
            person.requiredCodes.add(RoleSetting.KE_ZIP_CODE);
        }

        if (vsBarcode != null && PersonUtil.isRequired(scope, RoleSetting.KE_OUTLET_BARCODE)) {
            vsBarcode.editText(R.id.value).setHint(R.string.outlet_barcode_required);
            person.requiredCodes.add(RoleSetting.KE_OUTLET_BARCODE);
        }

        if (vsEmail != null && PersonUtil.isRequired(scope, RoleSetting.KE_PERSON_EMAIL)) {
            vsEmail.editText(R.id.value).setHint(R.string.outlet_email_required);
            person.requiredCodes.add(RoleSetting.KE_PERSON_EMAIL);
        }

        if (vsParent != null && PersonUtil.isRequired(scope, RoleSetting.KE_PERSON_PARENT_ID)) {
            vsParent.editText(R.id.value).setHint(R.string.outlet_parent_required);
            person.requiredCodes.add(RoleSetting.KE_PERSON_PARENT_ID);
        }

        MyArray<ViewSetup> rows = MyArray.from(
                vsName, vsShortName, vsAddress, vsAddressGuide, vsRegion,
                vsHospital, vsLocation, vsZipCode, vsPhone, vsEmail, vsCode, vsBarcode, vsParent
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

    private ViewSetup makeRegion(final FragmentActivity activity, CharSequence title) {
        final PersonData data = Mold.getData(getActivity());
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_edittext);
        ((MyImageView) vs.imageView(R.id.miv_icon)).setImageResource(R.drawable.info_5, R.color.default_icon);
        final EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        ValueSpinner value = data.info.person.getRegion();
        cValue.setText(value.getValue().name);
        cValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        UIUtils.editTextTouchListener(cValue, new Command() {
            @Override
            public void apply() {
                ValueSpinner value = data.info.person.getRegion();
                if (value.options.isEmpty() || value.options.size() <= 2) {
                    loadRegions(cValue);
                    return;
                }
                showRegionDialog(cValue, activity);
            }
        });
        return vs;
    }

    private ViewSetup makeHospital(final FragmentActivity activity, CharSequence title) {
        final PersonData data = Mold.getData(getActivity());
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_edittext);
        ((MyImageView) vs.imageView(R.id.miv_icon)).setImageResource(R.drawable.building_icons_for_, R.color.default_icon);
        final EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        ValueSpinner value = data.info.person.getHospital();
        cValue.setText(value.getValue().name);
        cValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        UIUtils.editTextTouchListener(cValue, new Command() {
            @Override
            public void apply() {
                ValueSpinner value = data.info.person.getHospital();
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

    private ViewSetup makeBarcode(Activity activity, CharSequence title, final ValueString value) {
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_barcode);
        final EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        UI.bind(cValue, value);
        vs.id(R.id.miv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BarcodeUtil.showBarcodeDialog(getContentFragment());
            }
        });
        return vs;
    }

    private ViewSetup makeParent(final FragmentActivity activity, final PersonData data, String title) {
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_edittext);
        ((MyImageView) vs.imageView(R.id.miv_icon)).setImageResource(R.drawable.store, R.color.default_icon);
        final EditText cValue = vs.id(R.id.value);
        cValue.setHint(title);
        ValueSpinner value = data.info.props.parent;
        cValue.setText(value.getValue().name);
        cValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        UIUtils.editTextTouchListener(cValue, new Command() {
            @Override
            public void apply() {
                showParentDialog(data, cValue, activity);
            }
        });
        return vs;
    }

    private void showRegionDialog(final EditText cValue, FragmentActivity activity) {
        PersonData data = Mold.getData(getActivity());
        final ValueSpinner value = data.info.person.getRegion();
        RegionFragment.Companion.open(activity, new ArgRegion(getArgPerson(), "0"));
//        UI.dialog().title(R.string.select)
//                .option(value.options, new DialogBuilder.CommandFacade<SpinnerOption>() {
//
//                    @Override
//                    public CharSequence getName(SpinnerOption val) {
//                        return val.name;
//                    }
//
//                    @Override
//                    public void apply(SpinnerOption val) {
//                        value.setValue(val);
//                        cValue.setText(val.name);
//                    }
//                }).show(activity);
    }

    private void showHospitalDialog(final EditText cValue, FragmentActivity activity) {
        PersonData data = Mold.getData(getActivity());
        final ValueSpinner value = data.info.person.getHospital();
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

    private void showParentDialog(PersonData data, final EditText cValue, FragmentActivity activity) {
        final ValueSpinner value = data.info.props.parent;
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

    private void loadRegions(final EditText cValue) {
        final FragmentActivity activity = getActivity();
        jobMate.executeWithDialog(activity, new ActionJob<>(getArgPerson(), RT.URI_REGION))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            MyArray<Region> regions = Uzum.toValue(result, Region.UZUM_ADAPTER.toArray());
                            if (regions.nonEmpty()) {
                                PersonData data = Mold.getData(getActivity());
                                data.setRegions(regions);
                                data.info.person.makeRegion(regions);
                                showRegionDialog(cValue, activity);
                            } else
                                Mold.makeSnackBar(getActivity(), R.string.region_not_found).show();
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
                                PersonData data = Mold.getData(getActivity());
                                data.info.person.makeHospital(hospitals);
                                showHospitalDialog(cValue, activity);
                            } else
                                Mold.makeSnackBar(getActivity(), R.string.hospital_not_found).show();
                        } else {
                            Mold.makeSnackBar(activity, ErrorUtil.getErrorMessage(error).message).show();
                        }
                    }
                });
    }


    @Override
    public void onContentPause() {
        super.onContentPause();
        jobMate.stopListening();
    }
}
