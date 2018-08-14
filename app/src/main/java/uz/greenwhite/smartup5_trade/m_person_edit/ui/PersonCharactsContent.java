package uz.greenwhite.smartup5_trade.m_person_edit.ui;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.view_setup.PopupBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditGroup;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VCharactItem;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class PersonCharactsContent extends MoldPageContent {

    public static MoldPageContent newInstance(ArgPerson arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER);
        return newContentInstance(PersonCharactsContent.class, bundle, title);
    }

    public ArgPerson getArgPerson() {
        return Mold.parcelableArgument(this, ArgPerson.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private ViewSetup vsRoot;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, int position) {
        this.vsRoot = new ViewSetup(inflater, null, R.layout.person_charact);
        return vsRoot.view;
    }

    @Override
    public void onContentResume() {
        super.onContentResume();
        reloadContent();
    }

    @Override
    public void reloadContent() {
        super.reloadContent();
        Parcelable data = Mold.getData(getActivity());
        if (data instanceof PersonData) {
            if (((PersonData) data).getPersonGroups().isEmpty()) {
                onRefresh();
                return;
            }
            prepareView(((PersonData) data).info.personCharacts.getGroups());
        } else if (data instanceof NaturalPersonData) {
            if (((NaturalPersonData) data).getPersonGroups().isEmpty()) {
                onRefresh();
                return;
            }
            prepareView(((NaturalPersonData) data).info.personCharacts.getGroups());
        }
    }

    private void prepareView(ValueArray<VCharactItem> list) {
        MyArray<VCharactItem> items = list.getItems();
        if (vsRoot == null) {
            return;
        }
        ViewGroup vg = vsRoot.viewGroup(R.id.ll_row);
        vg.removeAllViewsInLayout();
        for (final VCharactItem item : items) {
            final ViewSetup vs = new ViewSetup(getActivity(), R.layout.person_group_type);
            vs.view.setBackgroundColor(DS.getColor(R.color.white));
            vs.textView(R.id.name).setText(item.group.name);
            vs.textView(R.id.value).setText(item.spinner.getValue().name);
            vs.id(R.id.value).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UI.popup()
                            .option(item.spinner.options, new PopupBuilder.CommandFacade<SpinnerOption>() {
                                @NonNull
                                @Override
                                public CharSequence getName(SpinnerOption val) {
                                    return val.name;
                                }

                                @Override
                                public void apply(SpinnerOption val) {
                                    item.spinner.setValue(val);
                                    vs.textView(R.id.value).setText(item.spinner.getValue().name);
                                }
                            }).show(v);
                }
            });

//            UI.bind(vs.spinner(R.id.value), item.spinner);
            vg.addView(vs.view);
        }
    }

    private void onRefresh() {
        jobMate.execute(new ActionJob<String>(getArgPerson(), RT.URI_LOAD_PERSON_GROUP))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolve, String json, Throwable throwable) {
                        if (resolve) {
                            MyArray<PersonEditGroup> chGroups = Uzum.toValue(json, PersonEditGroup.UZUM_ADAPTER.toArray());
                            if (chGroups.nonEmpty()) {
                                Parcelable data = Mold.getData(getActivity());
                                if (data instanceof PersonData) {
                                    PersonData mData = (PersonData) data;
                                    mData.setPersonGroups(chGroups);
                                    mData.info.personCharacts.makeGroups(chGroups);
                                } else if (data instanceof NaturalPersonData) {
                                    NaturalPersonData mData = (NaturalPersonData) data;
                                    mData.setPersonGroups(chGroups);
                                    mData.info.personCharacts.makeGroups(chGroups);
                                }
                                reloadContent();
                            } else {
                                Mold.makeSnackBar(getActivity(), R.string.list_is_empty).show();
                            }
                        } else {
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
                        }
                    }
                });
    }

    @Override
    public void onContentDestroy(@Nullable Bundle saveInstanceState) {
        super.onContentDestroy(saveInstanceState);
        jobMate.stopListening();
    }
}



