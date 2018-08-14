package uz.greenwhite.smartup5_trade.m_session.ui.person;// 14.09.2016

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.LocationUtil;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningFragment;
import uz.greenwhite.lib.mold.RecyclerAdapter;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.view_setup.DialogBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.BarcodeUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.common.scope.OnScopeReadyCallback;
import uz.greenwhite.smartup5_trade.common.scope.ScopeUtil;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;
import uz.greenwhite.smartup5_trade.m_session.SessionUtil;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_session.bean.RoomOutletIds;
import uz.greenwhite.smartup5_trade.m_session.filter.ShippedFilter;
import uz.greenwhite.smartup5_trade.m_session.filter.ShippedFilterBuilder;
import uz.greenwhite.smartup5_trade.m_session.filter.ShippedFilterValue;
import uz.greenwhite.smartup5_trade.m_session.row.Customer;
import uz.greenwhite.smartup5_trade.m_session.row.OutletRow;
import uz.greenwhite.smartup5_trade.m_take_location.ui.MyTakeLocationDialog;
import uz.greenwhite.smartup5_trade.m_take_location.ui.TakeLocationListener;

public class ShippedFragment extends PersonFragment<Customer> implements TakeLocationListener {

    public static ShippedFragment newInstance(ArgSession argSession) {
        return Mold.parcelableArgumentNewInstance(ShippedFragment.class,
                argSession, ArgSession.UZUM_ADAPTER);
    }

    public ArgSession getArgSession() {
        return Mold.parcelableArgument(this, ArgSession.UZUM_ADAPTER);
    }

    public ShippedFilter filter;

    @SuppressWarnings("MissingPermission")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setSearchMenu(new MoldSearchListQuery() {
            @Override
            public boolean filter(Customer item, String text) {
                return CharSequenceUtil.containsIgnoreCase(item.title, text);
            }
        });

        addSubMenu(getString(R.string.filter), new Command() {
            @Override
            public void apply() {
                Mold.openTuningDrawer(getActivity());
            }
        });

        addSubMenu(getString(R.string.map), new Command() {
            @Override
            public void apply() {
                if (adapter != null && !UIUtils.showAlertIsEmpty(getActivity(), adapter.getFilteredItems())) {
                    MyTakeLocationDialog.show(getActivity(), getArgSession());
                }
            }
        });

        addSubMenu(getString(R.string.barcode), new Command() {
            @Override
            public void apply() {
                BarcodeUtil.showBarcodeDialog(ShippedFragment.this);
            }
        });

        setEmptyIcon(R.drawable.empty);
        setEmptyText(R.string.session_today_empty);
    }

    @Override
    public boolean hasItemDivider() {
        return false;
    }

    @Override
    public void reloadContent() {
        final ShippedFilterValue filterValue = filter != null ?
                filter.toValue() : ShippedFilterValue.makeDefault();
        ScopeUtil.execute(jobMate, getArgSession(), new OnScopeReadyCallback<Tuple2>() {
            @Override
            public Tuple2 onScopeReady(final Scope scope) {
                MyArray<SDeal> sDeals = scope.ref.getSDeals();
                MyArray<Customer> result = SessionUtil.getCustomersShipped(scope);
                MyArray<OutletGroup> groups = scope.ref.getOutletGroups();
                MyArray<OutletType> types = scope.ref.getOutletTypes();

                final SparseBooleanArray deals = new SparseBooleanArray();
                MyArray<Outlet> outlets = result.map(new MyMapper<Customer, Outlet>() {
                    @Override
                    public Outlet apply(Customer val) {
                        deals.put(Integer.parseInt(val.param.outletId), val.param.visited);
                        return DSUtil.getOutlet(scope, val.param.outletId);
                    }
                }).filterNotNull();

                MyArray<Room> rooms = scope.ref.getRooms();
                MyArray<RoomOutletIds> roomOutletIds = scope.ref.getRoomOutletIds();
                MyArray<Region> regions = scope.ref.getRegions();
                ShippedFilter filter = ShippedFilterBuilder.build(filterValue, sDeals,
                        outlets, groups, types, rooms, roomOutletIds, regions);
                return new Tuple2(filter, result);
            }

            @Override
            public void onDone(Tuple2 result) {
                ShippedFragment.this.filter = (ShippedFilter) result.first;
                setListItems((MyArray<Customer>) result.second);
                setFilterValues();
                makeFooter();
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(throwable).message);
            }
        });
    }

    @Override
    public void onLocationToken(Location location) {
        MyArray<String> outletIds = adapter.getFilteredItems().map(new MyMapper<Customer, String>() {
            @Override
            public String apply(Customer outletRow) {
                return outletRow.param.outletId;
            }
        }).filter(new MyPredicate<String>() {
            @Override
            public boolean apply(String s) {
                return !TextUtils.isEmpty(s);
            }
        });
        UIUtils.openNearOutletIndexFragment(getActivity(), getArgSession(), location, outletIds);
    }

    public void setFilterValues() {
        if (filter == null) {
            return;
        }
        final MyPredicate<Outlet> predicate = filter.getPredicate();
        if (predicate != null) {
            adapter.predicateOthers = new MyPredicate<Customer>() {
                @Override
                public boolean apply(final Customer outletRow) {
                    return outletRow.outlet != null && predicate.apply(outletRow.outlet);
                }
            };
        } else {
            adapter.predicateOthers = null;
        }
        adapter.filter();
    }

    @Override
    protected void onItemClick(RecyclerAdapter.ViewHolder holder, Customer item) {
        OutletIndexFragment.open(new ArgOutlet(getArgSession(), item.param.outletId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationUtil.REQUEST_CHECK_SETTINGS) {
            Fragment fragment = getFragmentManager()
                    .findFragmentByTag(MyTakeLocationDialog.TAKE_LOCATION_DIALOG);
            if (fragment != null && fragment instanceof MyTakeLocationDialog) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

        } else {
            final String barcode = BarcodeUtil.getBarcodeInActivityResult(getActivity(), requestCode, resultCode, data);
            if (!TextUtils.isEmpty(barcode)) {
                MyArray<Customer> listItems = getListItems();
                if (listItems == null) return;
                MyArray<Customer> filterResult = listItems.filter(new MyPredicate<Customer>() {
                    @Override
                    public boolean apply(Customer outletRow) {
                        return barcode.equals(outletRow.param.outletBarcode);
                    }
                });
                if (filterResult.isEmpty()) {
                    Mold.makeSnackBar(getActivity(), R.string.outlet_not_found).show();
                    return;
                }

                if (filterResult.size() > 1) {
                    UI.dialog()
                            .title(R.string.select_outlet)
                            .option(filterResult, new DialogBuilder.CommandFacade<Customer>() {
                                @Override
                                public CharSequence getName(Customer val) {
                                    return val.title;
                                }

                                @Override
                                public void apply(Customer val) {
                                    OutletIndexFragment.open(new ArgOutlet(getArgSession(), val.param.outletId));
                                }
                            })
                            .show(getActivity());

                    return;
                }
                OutletIndexFragment.open(new ArgOutlet(getArgSession(), filterResult.get(0).param.outletId));
            }
        }
    }

    private void makeFooter() {
        final ViewSetup vsFooter = new ViewSetup(getActivity(), R.layout.person_count_foter);
        vsFooter.textView(R.id.tv_persons).setText(DS.getString(R.string.session_shipped));
        vsFooter.textView(R.id.tv_person_count).setText("" + adapter.getFilteredItems().size());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            private void populate() {
                vsFooter.textView(R.id.tv_person_count).setText("" + adapter.getFilteredItems().size());
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                this.populate();
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                this.populate();
            }

            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                this.populate();
            }

            public void onChanged() {
                super.onChanged();
                this.populate();
            }
        });
        BottomSheetBehavior bottomSheet = Mold.makeBottomSheet(getActivity(), vsFooter.view);
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public MoldTuningFragment getTuningFragment() {
        return new ShippedTuningFragment();
    }

    @Override
    protected int adapterGetLayoutResource() {
        return R.layout.z_outlet_row;
    }

    @Override
    protected void adapterPopulate(final ViewSetup vsItem, final Customer item) {
        vsItem.id(R.id.v_bottom_padding).setVisibility(View.GONE);
        if (!adapter.isEmpty() && adapter.getFilteredItems().size() > 1) {
            MyArray<Customer> filteredItems = adapter.getFilteredItems();
            Customer lastItem = filteredItems.get(filteredItems.size() - 1);
            if (item == lastItem) {
                vsItem.id(R.id.v_bottom_padding).setVisibility(View.VISIBLE);
            }
        }

        if (item.icon != null) {
            vsItem.viewGroup(R.id.lf_state).setBackground((Drawable) item.icon.second);
            vsItem.imageView(R.id.state).setImageDrawable((Drawable) item.icon.first);
            vsItem.id(R.id.lf_state).setVisibility(View.VISIBLE);
        } else {
            vsItem.id(R.id.lf_state).setVisibility(View.GONE);
        }
        vsItem.textView(R.id.title).setText(item.title);
        vsItem.textView(R.id.detail).setText(item.detail);

        jobMate.execute(new FetchImageJob(getArgSession().accountId, item.param.outletPhotoSha))
                .always(new Promise.OnAlways<Bitmap>() {
                    @Override
                    public void onAlways(boolean resolved, Bitmap result, Throwable error) {
                        if (resolved) {
                            if (result != null) {
                                vsItem.imageView(R.id.iv_avatar).setImageBitmap(result);
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.GONE);
                            } else {
                                vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                                vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                            }
                        } else {
                            vsItem.imageView(R.id.iv_icon).setVisibility(View.VISIBLE);
                            vsItem.imageView(R.id.iv_avatar).setBackgroundResource(item.image);
                            if (error != null) error.printStackTrace();
                        }
                    }
                });


        Location mLocation = SmartupApp.getLocation();
        if (mLocation != null) {
            CharSequence outletDistance = item.getOutletDistance(mLocation);
            vsItem.id(R.id.tv_outlet_distance).setVisibility(TextUtils.isEmpty(outletDistance) ? View.GONE : View.VISIBLE);
            vsItem.textView(R.id.tv_outlet_distance).setText(outletDistance);
        } else {
            vsItem.id(R.id.tv_outlet_distance).setVisibility(View.GONE);
        }
    }
}
