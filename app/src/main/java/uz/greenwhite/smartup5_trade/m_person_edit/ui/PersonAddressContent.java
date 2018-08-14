package uz.greenwhite.smartup5_trade.m_person_edit.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import java.util.Comparator;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.mold.MoldPageContentRecycler;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.view_setup.DialogBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonAddress;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonAddressItem;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class PersonAddressContent extends MoldPageContentRecycler<VPersonAddressItem> {

    public static MoldPageContent newInstance(ArgPerson arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER);
        return newContentInstance(PersonAddressContent.class, bundle, title);
    }

    public ArgPerson getArgPerson() {
        return Mold.parcelableArgument(this, ArgPerson.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();

    @Override
    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);
        reloadContent();
    }

    public void reloadContent() {
        Parcelable data = Mold.getData(getActivity());
        if (data instanceof PersonData) {
            PersonData mData = (PersonData) data;
            VPersonAddress adress = mData.info.address;
            adress.makeRegion(mData.getRegions());
            setListItems(adress.personItem.getItems());
        }
    }

    @Override
    public void onContentResume() {
        VPersonAddress address = null;

        Parcelable data = Mold.getData(getActivity());
        if (data instanceof PersonData) {
            address = ((PersonData) data).info.address;
        }

        final VPersonAddress finalAddress = address;
        Mold.makeFloatAction(getActivity(), R.drawable.ic_add_black_24dp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorResult errors = finalAddress.getError();
                if (errors.isError()) {
                    UI.alertError(getActivity(), errors.getErrorMessage());
                    return;
                }
                finalAddress.addNewAddress();
                reloadContent();
            }
        });
    }


    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.oedit_address_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vs, final VPersonAddressItem val) {
        vs.view.setBackgroundColor(DS.getColor(R.color.white));
        UI.bind(vs.spinner(R.id.typeaddress), val.typeAddress);
        vs.bind(R.id.address, val.address);
        vs.bind(R.id.postCode, val.postCode);
        makeRegion(vs.editText(R.id.mkreg), val);
        vs.button(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VPersonAddress address = null;

                Parcelable data = Mold.getData(getActivity());
                if (data instanceof PersonData) {
                    address = ((PersonData) data).info.address;
                }

                assert address != null;
                address.removeAddress(val);

                reloadContent();
            }
        });

    }

    private void makeRegion(final EditText cValue, final VPersonAddressItem vItem) {
        ValueSpinner value = vItem.getRegion();
        cValue.setText(value.getValue().name);
        cValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        UIUtils.editTextTouchListener(cValue, new Command() {
            @Override
            public void apply() {
                ValueSpinner value = vItem.getRegion();
                if (value.options.isEmpty() || value.options.size() <= 2) {
                    loadRegions(cValue, vItem);
                    return;
                }
                showRegionDialog(cValue, getActivity(), vItem);
            }
        });
    }

    private void showRegionDialog(final EditText cValue, FragmentActivity activity, VPersonAddressItem vItem) {
        final ValueSpinner value = vItem.getRegion();
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

    private void loadRegions(final EditText cValue, final VPersonAddressItem vItem) {
        ArgPerson arg = getArgPerson();
        final FragmentActivity activity = getActivity();

        jobMate.executeWithDialog(activity, new ActionJob<>(getArgPerson(), RT.URI_REGION, MyArray.from(arg.roomId)))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            MyArray<Region> regions = Uzum.toValue(result, Region.UZUM_ADAPTER.toArray());
                            regions = regions.sort(new Comparator<Region>() {
                                @Override
                                public int compare(Region l, Region r) {
                                    return CharSequenceUtil.compareToIgnoreCase(l.name, r.name);
                                }
                            });
                            Parcelable data = Mold.getData(getActivity());
                            if (data instanceof PersonData) {
                                PersonData mData = (PersonData) data;
                                mData.setRegions(regions);
                                mData.info.address.makeRegion(regions);
                            }
                            showRegionDialog(cValue, activity, vItem);
                        } else {
                            Mold.makeSnackBar(activity, error.getLocalizedMessage()).show();
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