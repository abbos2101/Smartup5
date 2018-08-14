package uz.greenwhite.smartup5_trade.m_outlet.ui;// 18.08.2016

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.common.FetchImageJob;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.SmartupApp;
import uz.greenwhite.smartup5_trade.UIUtils;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.OutletLocation;
import uz.greenwhite.smartup5_trade.m_outlet.bean.PersonLastInfo;
import uz.greenwhite.smartup5_trade.m_outlet.row.OutletInfoRow;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;
import uz.greenwhite.smartup5_trade.m_session.bean.BankAccount;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroupValue;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;
import uz.greenwhite.smartup5_trade.m_session.row.OutletRow;
import uz.greenwhite.smartup5_trade.m_tracking.LocationUtil;

public abstract class InfoFragment extends MoldContentFragment {

    private ViewSetup vsRoot;
    private Location thisLocation;

    public abstract Outlet getOutlet();

    public abstract ArgSession getArgument();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.vsRoot = new ViewSetup(inflater, container, R.layout.outlet_info);
        return this.vsRoot.view;
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadContent();
    }

    @Override
    public void reloadContent() {
        final Outlet outlet = getOutlet();
        thisLocation = SmartupApp.getLocation();
        MyArray<OutletInfoRow> outletInfoRows = makeOutletInfo(getArgument().getScope(), outlet);
        ViewGroup vg = vsRoot.viewGroup(R.id.ll_outlet_info_row);
        vg.removeAllViews();
        jobMate.execute(new FetchImageJob(getArgument().accountId, getOutlet().photoSha)).always(new Promise.OnAlways<Bitmap>() {
            @Override
            public void onAlways(boolean resolved, Bitmap result, Throwable throwable) {
                if (resolved) {
                    if (result != null) {
                        vsRoot.imageView(R.id.iv_outlet_photo).setImageBitmap(result);
                    } else {
                        vsRoot.id(R.id.cv_outlet_info).setVisibility(View.GONE);
                    }
                }
            }
        });

        for (final OutletInfoRow item : outletInfoRows) {
            ViewSetup vs = new ViewSetup(getActivity(), R.layout.z_outlet_info_row);
            vg.addView(vs.view);
            vs.textView(R.id.tv_title).setText(item.title);
            vs.textView(R.id.tv_detail).setText(item.detail);

            ImageView icon = vs.imageView(R.id.iv_icon);
            if (item.icon != 0) {
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(item.icon);
            } else {
                icon.setVisibility(View.INVISIBLE);
            }

            ImageView share = vs.imageView(R.id.iv_share);
            share.setVisibility(item.type == OutletInfoRow.LOCATION ? View.VISIBLE : View.INVISIBLE);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LatLng latLng = LocationUtil.convertStringToLatLng((String) item.tag);
                    if (latLng == null) {
                        UI.alertError(getActivity(), "Error on location: " + item.tag);
                        return;
                    }

                    Double latitude = latLng.latitude;
                    Double longitude = latLng.longitude;

                    String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    String address = TextUtils.isEmpty(outlet.address) ? outlet.addressGuide : outlet.address;
                    String shareSubtitle = outlet.name;

                    if (!TextUtils.isEmpty(address)) {
                        shareSubtitle += "\n" + address;
                    }
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubtitle);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                    startActivity(Intent.createChooser(sharingIntent, DS.getString(R.string.share_via)));
                }
            });


            vs.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(item);
                }
            });
        }

        Tuple2 boundOutlets = getBoundOutlets(outlet);
        final Outlet parent = (Outlet) boundOutlets.first;
        MyArray<Outlet> children = (MyArray<Outlet>) boundOutlets.second;

        if (parent != null || !children.isEmpty()) {
            vsRoot.id(R.id.ll_bound_outlets).setVisibility(View.VISIBLE);
            ViewGroup vgParent = vsRoot.viewGroup(R.id.ll_parent);
            vgParent.removeAllViews();
            if (parent != null) {
                vsRoot.id(R.id.tv_parent).setVisibility(View.VISIBLE);
                vsRoot.id(R.id.v_parent).setVisibility(View.VISIBLE);
                vsRoot.id(R.id.ll_parent).setVisibility(View.VISIBLE);

                OutletRow rParent = new OutletRow(parent, new PersonLastInfo("", "", "", ""), null);
                ViewSetup vsParent = new ViewSetup(getActivity(), R.layout.z_outlet_row);
                vsParent.textView(R.id.title).setText(rParent.title);
                vsParent.textView(R.id.detail).setText(rParent.detail);
                vsParent.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getArgument().getScope().ref.getOutlets().find(parent.id, Outlet.KEY_ADAPTER) != null)
                            OutletInfoFragment.open(new ArgOutlet(getArgument(), parent.id));
                    }
                });
                vsParent.id(R.id.lf_state).setVisibility(View.GONE);

                CharSequence distance = rParent.getOutletDistance(thisLocation);
                if (!TextUtils.isEmpty(distance)) {
                    vsParent.textView(R.id.tv_outlet_distance).setText(distance);
                    vsParent.textView(R.id.tv_outlet_distance).setVisibility(View.VISIBLE);
                }

                setOutletImage(vsParent, rParent);
                vgParent.addView(vsParent.view);

            }

            if (!children.isEmpty()) {
                vsRoot.id(R.id.tv_children).setVisibility(View.VISIBLE);
                vsRoot.id(R.id.v_children).setVisibility(View.VISIBLE);
                vsRoot.id(R.id.ll_children).setVisibility(View.VISIBLE);
                ViewGroup vgChild = vsRoot.viewGroup(R.id.ll_children);
                vgChild.removeAllViews();
                for (final Outlet child : children) {
                    OutletRow rChild = new OutletRow(child, new PersonLastInfo("", "", "", ""), null);
                    ViewSetup vsChild = new ViewSetup(getActivity(), R.layout.z_outlet_row);
                    vsChild.id(R.id.lf_state).setVisibility(View.GONE);
                    vsChild.textView(R.id.title).setText(rChild.title);
                    vsChild.textView(R.id.detail).setText(rChild.detail);

                    CharSequence distance = rChild.getOutletDistance(thisLocation);
                    if (!TextUtils.isEmpty(distance)) {
                        vsChild.textView(R.id.tv_outlet_distance).setText(distance);
                        vsChild.textView(R.id.tv_outlet_distance).setVisibility(View.VISIBLE);
                    }

                    setOutletImage(vsChild, rChild);
                    vgChild.addView(vsChild.view);
                }
            }

        }

    }

    public static MyArray<OutletInfoRow> makeOutletInfo(Scope scope, Outlet outlet) {
        MyArray<OutletGroup> groups = scope.ref.getOutletGroups();
        final MyArray<OutletType> types = scope.ref.getOutletTypes();
        MyArray<Region> regions = scope.ref.getRegions();
        OutletLocation location = DSUtil.getOutletLocation(scope, outlet.id);

        SparseArray<MyArray<String>> outletGroup = new SparseArray<>();
        for (OutletGroupValue g : outlet.groupValues) {
            int key = Integer.parseInt(g.groupId);
            MyArray<String> res = outletGroup.get(key, MyArray.<String>emptyArray());
            if (!res.contains(g.typeId, MyMapper.<String>identity())) {
                outletGroup.put(key, res.append(g.typeId));
            }
        }

        String outletLatLng = location != null ? location.location : outlet.latLng;

        List<OutletInfoRow> r = new ArrayList<>();
        r.add(new OutletInfoRow(DS.getString(R.string.outlet_name), outlet.name, OutletInfoRow.STRING));

        r.add(new OutletInfoRow(DS.getString(R.string.outlet_address), outlet.address, outletLatLng,
                TextUtils.isEmpty(outletLatLng) ? OutletInfoRow.STRING : OutletInfoRow.LOCATION));

        if (!TextUtils.isEmpty(outlet.regionId)) {
            Region region = regions.find(outlet.regionId, Region.KEY_ADAPTER);
            r.add(new OutletInfoRow(DS.getString(R.string.region), region.name, region, OutletInfoRow.STRING));
        }
        r.add(new OutletInfoRow(DS.getString(R.string.outlet_address_guide), outlet.addressGuide, outletLatLng,
                TextUtils.isEmpty(outlet.address) && !TextUtils.isEmpty(outletLatLng) ? OutletInfoRow.LOCATION : OutletInfoRow.STRING));

        if (TextUtils.isEmpty(outlet.address) && TextUtils.isEmpty(outlet.addressGuide) && !TextUtils.isEmpty(outletLatLng)) {
            r.add(new OutletInfoRow(DS.getString(R.string.outlet_location), outletLatLng, outletLatLng, OutletInfoRow.LOCATION));
        }

        if (!TextUtils.isEmpty(outlet.ownerName)) {
            r.add(new OutletInfoRow(DS.getString(R.string.outlet_owner), outlet.ownerName, OutletInfoRow.STRING));
        }
        r.add(new OutletInfoRow(DS.getString(R.string.outlet_phone), outlet.phone, OutletInfoRow.PHONE));
        r.add(new OutletInfoRow(DS.getString(R.string.barcode), outlet.barcode, OutletInfoRow.STRING));

        if (!TextUtils.isEmpty(outlet.code)) {
            r.add(new OutletInfoRow(DS.getString(R.string.outlet_code), outlet.code, OutletInfoRow.STRING));
        }

        if (!TextUtils.isEmpty(outlet.inn)) {
            r.add(new OutletInfoRow(DS.getString(R.string.inn), outlet.inn, OutletInfoRow.STRING));
        }

        if (outlet.bankAccounts.nonEmpty()) {
            for (BankAccount account : outlet.bankAccounts) {
                int p = 0;
                r.add(new OutletInfoRow(DS.getString(R.string.outlet_bank_account, ++p), account.bankAccCode, OutletInfoRow.STRING));
            }
        }
        for (int i = 0; i < outletGroup.size(); i++) {
            int key = outletGroup.keyAt(i);
            MyArray<String> result = outletGroup.get(key)
                    .map(new MyMapper<String, String>() {
                        @Override
                        public String apply(String typeId) {
                            OutletType type = types.find(typeId, OutletType.KEY_ADAPTER);
                            if (type == null) return null;
                            return type.name;
                        }
                    }).filterNotNull();
            OutletGroup group = groups.find(String.valueOf(key), OutletGroup.KEY_ADAPTER);
            r.add(new OutletInfoRow(group.name, result.mkString(","), OutletInfoRow.STRING));
        }

        return MyArray.from(r).filter(new MyPredicate<OutletInfoRow>() {
            @Override
            public boolean apply(OutletInfoRow val) {
                return !TextUtils.isEmpty(val.detail);
            }
        });
    }

    protected void onItemClick(final OutletInfoRow item) {
        switch (item.type) {
            case OutletInfoRow.LOCATION:
                if (item.tag != OutletInfoRow.TAG &&
                        item.tag instanceof String &&
                        !TextUtils.isEmpty((String) item.tag)) {
                    UIUtils.openMap(getActivity(), getOutlet(), (String) item.tag);
                }
                break;

            case OutletInfoRow.PHONE:
                UI.confirm(getActivity(), getString(R.string.outlet_call), item.detail, new Command() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void apply() {
                        String phone = item.detail;
                        if (item.detail.length() == 12) {
                            phone = "+" + phone;
                        }
                        Intent i = new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + phone));
                        if (SysUtil.checkSelfPermissionGranted(getActivity(), Manifest.permission.CALL_PHONE)) {
                            getActivity().startActivity(i);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 0);
                        }
                    }
                });
                break;
        }
    }

    private Tuple2 getBoundOutlets(final Outlet outlet) {
        MyArray<Outlet> outlets = getArgument().getScope().ref.getOutlets();
        Outlet parent = outlets.findFirst(new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet v) {
                return outlet.parentId.equalsIgnoreCase(v.id);
            }
        });

        MyArray<Outlet> children = outlets.filter(new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet v) {
                return v.parentId.equalsIgnoreCase(outlet.id);
            }
        });

        return new Tuple2(parent, MyArray.nvl(children));         //<parentId, MyArray<childId>>

    }

    private void setOutletImage(final ViewSetup vsItem, final OutletRow item) {
        jobMate.execute(new FetchImageJob(getArgument().accountId, item.outlet.photoSha))
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
    }

}
