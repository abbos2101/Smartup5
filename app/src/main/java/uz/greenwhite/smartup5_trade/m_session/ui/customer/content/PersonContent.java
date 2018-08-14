package uz.greenwhite.smartup5_trade.m_session.ui.customer.content;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyRecyclerAdapter;
import uz.greenwhite.lib.mold.LocationUtil;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.common.MyCommand;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.NaturalPersonCreateFragment;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.PersonCreateFragment;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.setting.Setting;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerData;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerFragment;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.CustomerUtil;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.arg.ArgPersonCustomer;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.content.dialog.PersonFilterDialog;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.row.ChipRow;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.row.CustomerRow;
import uz.greenwhite.smartup5_trade.m_session.ui.customer.row.PersonCustomerRow;
import uz.greenwhite.smartup5_trade.m_take_location.ui.MyTakeLocationDialog;
import uz.greenwhite.smartup5_trade.m_take_location.ui.TakeLocationListener;

public class PersonContent extends CustomerContent implements TakeLocationListener {

    public static PersonContent newInstance(ArgPersonCustomer arg, CharSequence title) {
        return newCustomerInstance(PersonContent.class, arg, title);
    }

    @Override
    public ArgPersonCustomer getArgSession() {
        return Mold.parcelableArgument(this, ArgPersonCustomer.UZUM_ADAPTER);
    }

    private ChipAdapter chipAdapter;

    @Override
    public void onContentCreated(@Nullable Bundle saveInstanceState) {
        super.onContentCreated(saveInstanceState);
        this.chipAdapter = new ChipAdapter(getActivity(), this);

        addMenu(R.drawable.ic_filter_list_black_24dp, R.string.filter, new Command() {
            @Override
            public void apply() {
                PersonFilterDialog.show((CustomerFragment) getContentFragment());
            }
        });

        addSubMenu(DS.getString(R.string.map), new Command() {
            @Override
            public void apply() {
                Location location = SmartupApp.getLocation();

                if (location == null && adapter != null && !UIUtils.showAlertIsEmpty(getActivity(), adapter.getFilteredItems())) {
                    MyTakeLocationDialog.show(getActivity(), getArgSession());

                } else if (location != null) {
                    onLocationToken(location);
                }
            }
        });

        ScopeUtil.execute(jobMate, getArgSession(), new OnScopeReadyCallback<MyArray<PersonCustomerRow>>() {
            @Override
            public MyArray<PersonCustomerRow> onScopeReady(Scope scope) {
                return CustomerUtil.getPersonCustomerRows(scope, false);
            }

            @Override
            public void onDone(MyArray<PersonCustomerRow> personCustomerRows) {
                setListItems(personCustomerRows.<CustomerRow>toSuper());
                notifyFilterChange();
            }
        });
    }

    @Override
    public void onContentResume() {
        super.onContentResume();

        final ArgPersonCustomer arg = getArgSession();
        Setting setting = arg.getSetting();

        if ((arg.isOutlet() || arg.isPharm() || arg.isDoctor()) && setting.person.createLegalPerson) {

            Mold.makeFloatAction(getActivity(), R.drawable.ic_add_black_24dp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UIUtils.showRoomDialog(getActivity(), DSUtil.getFilialRooms(arg.getScope()), new MyCommand<Room>() {
                        @Override
                        public void apply(Room val) {
                            if (arg.isDoctor()) {
                                NaturalPersonCreateFragment.open(new ArgPerson(arg, "", val.id));

                            } else {
                                String personKind = "";
                                if (arg.isPharm()) {
                                    personKind = PersonInfo.K_PHARMACY;
                                }
                                PersonCreateFragment.open(new ArgPerson(arg, "", val.id, personKind));
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void notifyFilterChange() {
        MoldContentFragment fragment = Mold.getContentFragment(getActivity());
        CustomerData data = fragment.getFragmentData();

        if (data != null && adapter != null) {
            final MyPredicate<Outlet> predicate = data.filter.personFilter.getPredicate();
            if (predicate != null) {
                adapter.predicateOthers = new MyPredicate<CustomerRow>() {
                    @Override
                    public boolean apply(final CustomerRow item) {
                        return predicate.apply(item.outlet);
                    }
                };
            } else {
                adapter.predicateOthers = null;
            }
            adapter.filter();

            if (getHeader() == null) {
                ViewSetup vsHeader = setHeader(R.layout.z_customer_header);
                RecyclerView recyclerView = (RecyclerView) vsHeader.view;
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(chipAdapter);
            }

            chipAdapter.setItems(data.filter.personFilter.getFilterChip());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AdminApi.saveLocaleCode(AdminApi.getLocaleCode(), true);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LocationUtil.REQUEST_CHECK_SETTINGS) {
            Fragment fragment = getContentFragment().getFragmentManager()
                    .findFragmentByTag(MyTakeLocationDialog.TAKE_LOCATION_DIALOG);
            if (fragment != null && fragment instanceof MyTakeLocationDialog) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onLocationToken(Location location) {
        MyArray<String> outletIds = adapter.getFilteredItems().map(CustomerRow.KEY_ADAPTER);
        UIUtils.openNearOutletIndexFragment(getActivity(), getArgSession(), location, outletIds);
    }

    class ChipAdapter extends MyRecyclerAdapter<ChipRow> {

        PersonContent content;

        ChipAdapter(Context context, PersonContent content) {
            super(context);
            this.content = content;
        }

        @Override
        protected void itemClick(ViewHolder holder, ChipRow item) {
            PersonFilterDialog.show((CustomerFragment) content.getContentFragment());
        }

        @Override
        protected int getLayoutResource() {
            return R.layout.z_chip;
        }

        @Override
        protected void populate(ViewSetup vs, final ChipRow item) {
            vs.textView(R.id.tv_chip_titile).setText(((String) item.title).trim());

            vs.id(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.command.apply();
                    content.notifyFilterChange();
                }
            });
        }
    }
}
