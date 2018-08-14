package uz.greenwhite.smartup5_trade.m_tracking.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldTuningSectionFragment;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;

public class TrackingTuningFragment extends MoldTuningSectionFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyArray<Section> section = MyArray.from(getUserSection());
        setSections(section);
    }

    private Section getUserSection() {
        final TrackingIndexFragment trIndex = Mold.getIndexFragment(getActivity());
        final TrackingData data = Mold.getData(getActivity());

        return new Section() {

            @Override
            public View createView(LayoutInflater inflater, ViewGroup parent) {
                final ViewSetup vs = new ViewSetup(inflater, parent, R.layout.tracking_section_menu);
                vs.bind(R.id.s_agent, data.agent);
                vs.makeDatePicker(R.id.date_of_visit);
                vs.bind(R.id.date_of_visit, data.date);
                vs.bind(R.id.user_tracking, data.userTracking);
                vs.bind(R.id.outlet_location, data.outletLocation);
                UI.bind(vs.spinner(R.id.s_map_type), data.mapType,true);

                vs.model(R.id.s_map_type).add(new ModelChange() {
                    @Override
                    public void onChange() {
                        ((TrackingMapFragment) Mold.getContentFragment(getActivity())).prepareMapType();
                        Mold.closeTuningDrawer(getActivity());
                    }
                });

                vs.button(R.id.apply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.date.modified() || data.agent.modified()) {
                            data.date.readyToChange();
                            data.agent.readyToChange();
                            trIndex.request();
                        } else {
                            ((TrackingMapFragment) Mold.getContentFragment(getActivity())).prepareTrackingResult();
                        }
                        Mold.closeDrawers(getActivity());
                    }
                });
                return vs.view;
            }
        };
    }
}