package uz.greenwhite.smartup5_trade.m_person_edit.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldPageContent;
import uz.greenwhite.lib.mold.MoldPageContentRecycler;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.common.CompleteAdapterBank;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.Bank;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditAccount;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonAccountItem;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;

public class PersonAccountContent extends MoldPageContentRecycler<VPersonAccountItem> {

    public static MoldPageContent newInstance(ArgPerson arg, CharSequence title) {
        Bundle bundle = Mold.parcelableArgument(arg, ArgPerson.UZUM_ADAPTER);
        return newContentInstance(PersonAccountContent.class, bundle, title);
    }

    public ArgPerson getArgPerson() {
        return Mold.parcelableArgument(this, ArgPerson.UZUM_ADAPTER);
    }

    private final JobMate jobMate = new JobMate();
    private CompleteAdapterBank adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, int position) {
        View view = super.onCreateView(inflater, position);
        view.setBackgroundResource(R.color.background);
        return view;
    }

    @Override
    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);
        this.adapter = new CompleteAdapterBank(getActivity());
    }

    public void onContentResume() {
        final PersonData data = Mold.getData(getActivity());
        Mold.makeFloatAction(getActivity(), R.drawable.ic_add_black_24dp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorResult errors = data.info.account.getError();
                if (errors.isError()) {
                    UI.alertError(getActivity(), errors.getErrorMessage());
                    return;
                }
                PersonData data = Mold.getData(getActivity());
                data.info.account.addNewAccount(data);
                reloadContent();
            }
        });
        reloadContent();
    }

    @Override
    public void reloadContent() {
        PersonData data = Mold.getData(getActivity());
        PersonEditAccount account = data.getAccount();
        adapter.setItems(account.banks);
        MyArray<VPersonAccountItem> items = data.info.account.accountItem.getItems();
//
        setListItems(items);
        if (account.banks.isEmpty()) request();
    }

    private void request() {
        this.jobMate.execute(new ActionJob<String>(getArgPerson(), RT.URI_LOAD_ACCOUNT))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolve, String result, Throwable error) {
                        if (resolve) {
                            PersonEditAccount account = Uzum.toValue(result, PersonEditAccount.UZUM_ADAPTER);
                            PersonData data = Mold.getData(getActivity());
                            data.setAccount(account);
                            data.info.account.makeCurrency(account.currencies);
                            if (account.banks.isEmpty()) {
                                Mold.makeSnackBar(getActivity(), R.string.oedit_bank_list_is_empty).show();
                            } else reloadContent();
                        } else {
                            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(error).message);
                        }
                    }
                });
    }


    // -------------------------------------------------------------------------------------------------

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.oedit_account_row;
    }

    @Override
    protected void adapterPopulate(ViewSetup vs, final VPersonAccountItem val) {
        vs.bind(R.id.schet, val.personAccount);
        UI.bind(vs.spinner(R.id.spn), val.getCurrency());

        final AutoCompleteTextView completeBank = vs.id(R.id.bank);
        completeBank.setText(val.getBankName());
        completeBank.setAdapter(adapter);
        completeBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bank item = adapter.getItem(position);
                if (!TextUtils.isEmpty(item.mfo)) {
                    val.setBank(item);
                } else {
                    completeBank.setText("");
                }
            }
        });

        vs.button(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonData data = Mold.getData(getActivity());
                data.info.account.removeAccount(val);
                reloadContent();
            }
        });

    }


    @Override
    public void onContentPause() {
        super.onContentPause();
        jobMate.stopListening();
    }

}
