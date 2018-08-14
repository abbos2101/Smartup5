package uz.greenwhite.smartup5_trade.m_person_edit.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_person_edit.PersonUtil;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VNaturalPerson;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;

public class NaturalPersonContent extends MoldPageContent {

    public static MoldPageContent newInstance(ArgPerson arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER);
        return newContentInstance(NaturalPersonContent.class, bundle, title);
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
        NaturalPersonData data = Mold.getData(getActivity());
        if (data == null) return;

        VNaturalPerson person = data.info.person;

        ArgPerson arg = getArgPerson();
        Scope scope = arg.getScope();

        MyArray<ViewSetup> rows = MyArray.from(

                !arg.editPerson() || (PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_NAME) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_NAME)) ?
                        UIUtils.makeEditText(this, R.string.outlet_natural_person_name_required, person.name) : null,

                !arg.editPerson() || (PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_NAME) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_NAME)) ?
                        UIUtils.makeEditText(this, R.string.outlet_natural_person_surname, person.surname) : null,

                !arg.editPerson() || (PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_NAME) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_NAME)) ?
                        UIUtils.makeEditText(this, R.string.outlet_natural_person_patronymic, person.patronymic) : null,

                makeGender(getActivity()),

                PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_PHONE) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_PHONE) ?
                        UIUtils.makeEditText(this, R.drawable.info_6, R.string.outlet_phone, person.phone, InputType.TYPE_CLASS_PHONE) : null,

                makeBirthday(getActivity()),

                UIUtils.makeEditText(this, R.drawable.ic_email_black_24dp, R.string.outlet_email, person.email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),

                PersonUtil.hasVisible(scope, RoleSetting.KE_OUTLET_CODE) && PersonUtil.hasEdit(scope, RoleSetting.KE_OUTLET_CODE) ?
                        UIUtils.makeEditText(this, R.drawable.info_7, R.string.outlet_code, person.code) : null

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

    private ViewSetup makeGender(final FragmentActivity activity) {
        final NaturalPersonData data = Mold.getData(getActivity());
        ViewSetup vs = new ViewSetup(activity, R.layout.person_gender);
        RadioGroup rg = vs.id(R.id.rg_gender);
        rg.check(NaturalPerson.K_G_FEMALE.equals(data.info.person.gender.getText()) ? R.id.rb_female : R.id.rb_male);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.rb_male:
                        data.info.person.gender.setValue(NaturalPerson.K_G_MALE);
                        break;
                    case R.id.rb_female:
                        data.info.person.gender.setValue(NaturalPerson.K_G_FEMALE);
                        break;
                }
            }
        });

        return vs;
    }

    private ViewSetup makeBirthday(final FragmentActivity activity) {
        final NaturalPersonData data = Mold.getData(getActivity());
        ViewSetup vs = new ViewSetup(activity, R.layout.oedit_edittext);
        vs.bind(R.id.value, data.info.person.birthday);
        vs.makeDatePicker(R.id.value, true);
        return vs;
    }


    @Override
    public void onContentDestroy(@Nullable Bundle saveInstanceState) {
        super.onContentDestroy(saveInstanceState);
        jobMate.stopListening();
    }
}
