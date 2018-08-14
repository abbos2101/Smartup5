package uz.greenwhite.smartup5_trade.m_tracking.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.job.JobMate;
import uz.greenwhite.lib.job.Promise;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldIndexFragment;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.m_session.job.ActionJob;
import uz.greenwhite.smartup5_trade.m_tracking.TrackingApi;
import uz.greenwhite.smartup5_trade.m_tracking.arg.ArgTracking;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TROutlet;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TrackingHeader;

public class TrackingIndexFragment extends MoldIndexFragment {

    public static void open(ArgTracking arg) {
        Mold.openIndex(TrackingIndexFragment.class, Mold.parcelableArgument(arg, ArgTracking.UZUM_ADAPTER));
    }

    public ArgTracking getArgTracking() {
        return Mold.parcelableArgument(this, ArgTracking.UZUM_ADAPTER);
    }

    private TrackingAdapter adapter;
    private ViewSetup vsRoot;

    private final JobMate jobMate = new JobMate();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.tracking_menu);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new TrackingAdapter(getActivity());

        ListView lv = vsRoot.id(R.id.list_view);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Mold.closeDrawers(getActivity());

                final TROutlet item = adapter.getItem(position);
                if (!item.isTrackingLocationEmpty()) {
                    TrackingMapFragment content = Mold.getContentFragment(getActivity());
                    content.showInMarker(item.outletId, item.getTrackingLatLng());
                } else {
                    if (!TROutlet.NOT_VISITED.equals(item.state) && item.deals.nonEmpty()) {
                        CharSequence message = UI.html().v(getString(R.string.tracking_map_title,
                                item.name, item.address)).br().v(TrackingApi.getOutletDetail(item)).html();
                        UI.dialog()
                                .title(getString(R.string.tracking_info))
                                .message(message)
                                .positive(R.string.open, new Command() {
                                    @Override
                                    public void apply() {
                                        TrackingApi.showDealInfo(getActivity(),
                                                item.deals, item.latLon, getArgTracking());
                                    }
                                })
                                .show(getActivity());
                    }
                }
            }
        });

        TrackingData data = Mold.getData(getActivity());
        if (data == null) {
            ArgTracking arg = getArgTracking();
            data = new TrackingData(arg.agentId, arg.date, arg.users);
            Mold.setData(getActivity(), data);
        }
        request();
    }

    @Override
    public void onStart() {
        super.onStart();
        TrackingData data = Mold.getData(getActivity());
        if (data == null || data.getTracking() == null) {
            request();
        }
    }

    @Override
    protected void onShowDefaultForm() {
        Mold.replaceContent(getActivity(), TrackingMapFragment.newInstance(getArgTracking()));
    }

    public void request() {
        TrackingData data = Mold.getData(getActivity());
        String visitDate = data.date.getValue();
        String agentId = data.agent.getValue().code;
        jobMate.executeWithDialog(getActivity(), new ActionJob<String>(getArgTracking(),
                RT.URI_SV_USER_TRACKING_PERSON, MyArray.from(visitDate, agentId)))
                .always(new Promise.OnAlways<String>() {
                    @Override
                    public void onAlways(boolean resolved, String result, Throwable error) {
                        if (resolved) {
                            requestResult(Uzum.toValue(result, TrackingHeader.UZUM_ADAPTER));
                        } else {
                            Mold.makeSnackBar(getActivity(), ErrorUtil.getErrorMessage(error).message).show();
                        }
                    }
                });
    }

    private void requestResult(TrackingHeader header) {
        TrackingData data = Mold.getData(getActivity());
        TrackingHeader lastData = data.getTracking();

        data.setTracking(header);
        adapter.setItems(header.outlets);

        TrackingMapFragment map = Mold.getContentFragment(getActivity());
        if (lastData == null || lastData.outletPlan == null) {
            map.init();
        }
        map.prepareTrackingResult();
    }

    @Override
    public void onStop() {
        super.onStop();
        jobMate.stopListening();
    }

    @Override
    public void onDrawerOpened() {
        super.onDrawerOpened();
        TrackingData data = Mold.getData(getActivity());
        TrackingHeader tracking = data.getTracking();
        if (tracking == null || tracking.outlets.isEmpty()) {
            Mold.makeSnackBar(getActivity(), R.string.list_is_empty).show();
            Mold.closeDrawers(getActivity());
        }
    }
}
