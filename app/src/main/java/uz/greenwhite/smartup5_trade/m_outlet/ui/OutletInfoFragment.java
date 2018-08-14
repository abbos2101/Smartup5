package uz.greenwhite.smartup5_trade.m_outlet.ui;// 18.08.2016

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.widget.fab.FloatingActionButton;
import uz.greenwhite.lib.widget.fab.FloatingActionsMenu;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.common.MyCommand;
import uz.greenwhite.smartup5_trade.m_location.arg.ArgMap;
import uz.greenwhite.smartup5_trade.m_location.ui.LocationFragment;
import uz.greenwhite.smartup5_trade.m_outlet.OutletApi;
import uz.greenwhite.smartup5_trade.m_outlet.OutletUtil;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletLocation;
import uz.greenwhite.smartup5_trade.m_person_edit.PersonUtil;
import uz.greenwhite.smartup5_trade.m_person_edit.arg.ArgPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.NaturalPersonCreateFragment;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.PersonCreateFragment;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.Room;
import uz.greenwhite.smartup5_trade.m_vp_outlet.arg.ArgVPOutlet;
import uz.greenwhite.smartup5_trade.m_vp_outlet.ui.OutletVisitPlanFragment;

public class OutletInfoFragment extends InfoFragment {

    public static OutletInfoFragment newInstance(ArgOutlet argOutlet) {
        return Mold.parcelableArgumentNewInstance(OutletInfoFragment.class,
                argOutlet, ArgOutlet.UZUM_ADAPTER);
    }

    public static void open(ArgOutlet argOutlet) {
        Mold.openContent(OutletInfoFragment.class, Mold.parcelableArgument(argOutlet, ArgOutlet.UZUM_ADAPTER));
    }

    public ArgOutlet getArgOutlet() {
        return Mold.parcelableArgument(this, ArgOutlet.UZUM_ADAPTER);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ArgOutlet arg = getArgOutlet();
        Mold.setTitle(getActivity(), R.string.info);
        if (PersonUtil.checkRolePersonEditOrVisible(arg.getScope())) {
            addMenu(R.drawable.ic_edit_black_24dp, R.string.edit, new Command() {
                @Override
                public void apply() {
                    Outlet outlet = arg.getOutlet();
                    if (outlet.isDoctor()) {
                        NaturalPersonCreateFragment.open(new ArgPerson(arg, arg.outletId, ""));
                    } else {
                        String personKind = "";
                        if (outlet.isPharm()) {
                            personKind = PersonInfo.K_PHARMACY;
                        }
                        PersonCreateFragment.open(new ArgPerson(arg, arg.outletId, "", personKind));
                    }
                }
            });

            boolean locationEdit = OutletUtil.hasEditLocation(arg.getScope());
            boolean planShow = OutletUtil.hasShowPlan(arg.getScope());

            if (!locationEdit && !planShow) {
                return;
            }

            Drawable iconGPS = UI.changeDrawableColor(getActivity(), R.drawable.ic_map_black_36dp, R.color.white);
            Drawable iconOVPlan = UI.changeDrawableColor(getActivity(), R.drawable.ic_date_range_black_36dp, R.color.white);

            final FloatingActionsMenu menu = Mold.makeFloatActionMenu(getActivity());

            if (locationEdit) {
                menu.addButton(makeButton(iconGPS, R.string.outlet_change_location, new Command() {
                    @Override
                    public void apply() {
                        menu.collapse();
                        ArgOutlet a = getArgOutlet();
                        OutletLocation location = OutletApi.getOutletLocation(a.getScope(), a.outletId);
                        ArgMap arg = new ArgMap(a, location.location);
                        if (!SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            return;
                        }
                        Mold.addContent(getActivity(), LocationFragment.newInstance(arg));
                    }
                }));
            }

            if (planShow) {
                menu.addButton(makeButton(iconOVPlan, R.string.outlet_visit_plan, new Command() {
                    @Override
                    public void apply() {
                        menu.collapse();
                        showRoomDialog();
                    }
                }));
            }
        }
    }

    private FloatingActionButton makeButton(@NonNull Drawable icon,
                                            int titleResId,
                                            @NonNull final Command command) {
        FloatingActionButton fabDeal = new FloatingActionButton(getActivity());
        fabDeal.setSize(FloatingActionButton.SIZE_MINI);
        fabDeal.setIconDrawable(icon);
        fabDeal.setColorNormalResId(R.color.app_color_accent);
        fabDeal.setColorPressedResId(R.color.app_color_accent);
        fabDeal.setStrokeVisible(true);
        fabDeal.setTitle(getString(titleResId));
        fabDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command.apply();
            }
        });
        return fabDeal;
    }

    public void showRoomDialog() {
        final ArgOutlet arg = getArgOutlet();
        final MyArray<Room> rooms = OutletApi.getOutletRooms(arg.getScope(), arg.outletId);
        UIUtils.showRoomDialog(getActivity(), rooms, new MyCommand<Room>() {
            @Override
            public void apply(Room val) {
                OutletVisitPlanFragment.open(new ArgVPOutlet(arg, val.id));
            }
        });
    }


    @Override
    public Outlet getOutlet() {
        ArgOutlet arg = getArgOutlet();
        Outlet outlet = arg.getOutlet();
        if (outlet == null) {
            throw new AppError("outlet not found = " + arg.outletId);
        }
        return outlet;
    }

    @Override
    public ArgSession getArgument() {
        return getArgOutlet();
    }


    @Override
    public void onAboveContentPopped(Object result) {
        if (result != null && result instanceof LatLng) {
            showLocationDialog((LatLng) result);
        }
    }

    private void showLocationDialog(final LatLng location) {
        CharSequence message = UI.html().v(getString(R.string.save_location)).html();
        UI.dialog()
                .title(R.string.warning)
                .message(message)
                .negative(R.string.close, Util.NOOP)
                .positive(R.string.save, new Command() {
                    @Override
                    public void apply() {
                        saveOutletLocation(location);
                    }
                })
                .show(getActivity());
    }

    private void saveOutletLocation(LatLng latLng) {
        try {
            ArgOutlet arg = getArgOutlet();
            String location = latLng.latitude + "," + latLng.longitude;
            OutletLocation val = new OutletLocation(arg.outletId, location);
            OutletApi.saveOutletLocation(arg.getScope(), val);
            reloadContent();
        } catch (Exception e) {
            e.printStackTrace();
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(e).message);
        }
    }
}
