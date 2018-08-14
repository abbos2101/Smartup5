package uz.greenwhite.smartup5_trade.m_near.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup.anor.ErrorUtil;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_near.arg.ArgNearOutlet;
import uz.greenwhite.smartup5_trade.m_near.bean.MapItem;
import uz.greenwhite.smartup5_trade.m_near.util.CustomListView;
import uz.greenwhite.smartup5_trade.m_near.util.InterceptListener;
import uz.greenwhite.smartup5_trade.m_near.util.NearMapUtil;
import uz.greenwhite.smartup5_trade.m_near.util.SlideLayout;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.ui.OutletIndexFragment;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class MapListFragment extends MoldContentFragment {

    public static MapListFragment newInstance(ArgNearOutlet arg) {
        return Mold.parcelableArgumentNewInstance(MapListFragment.class,
                arg, ArgNearOutlet.UZUM_ADAPTER);
    }

    public ArgNearOutlet getArgNearOutlet() {
        return Mold.parcelableArgument(this, ArgNearOutlet.UZUM_ADAPTER);
    }

    private SlideLayout layout;
    private CustomListView listView;
    private ViewGroup panel;
    private TextView tvEmpty;

    private float sY, eY;
    private boolean open_map = false;
    private int swipe_Min_Distance = 50;
    private boolean lastSwipe = true;

    private MapListAdapter adapter;
    private List<MapItem> nearMapList;
    private MapItem selectNearMap;

    private MapFragment mapFragment;

    private ViewSetup vsRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.near_map_list);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = vsRoot.id(R.id.customListView);
        layout = vsRoot.id(R.id.ListFrame);
        panel = vsRoot.id(R.id.panel);
        tvEmpty = vsRoot.textView(R.id.emptyText);
        tvEmpty.setVisibility(View.GONE);
        panel.setVisibility(View.GONE);
        layout.setExpanded();
        listView.setDisableScroll(false);

        panel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (open_map) {
                    switch (event.getAction()) {
                        case ACTION_DOWN:
                            sY = event.getY();
                            break;
                        case ACTION_MOVE:
                            eY = event.getY();
                            break;
                        case ACTION_UP: {
                            final float yDistance = Math.abs(sY - eY);
                            if (yDistance > swipe_Min_Distance) {
                                if (sY > eY) {
                                    setMapScreen(false, selectNearMap);
                                }
                            }
                            break;
                        }
                    }
                }
                return false;
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        listView.startScrollerTask();
                        break;
                    }
                }
                return false;
            }
        });

        listView.setOnScrollStoppedListener(new CustomListView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped() {
                onSwipeListBottom();
            }
        });

        layout.setOnInterceptListener(new InterceptListener<Void>() {
            @Override
            public void intercept(int key, Void result) {
                if (key != 1) {
                    panel.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            NearOutletFragment contentFragment = Mold.getContentFragment(getActivity());
            mapFragment = (MapFragment) contentFragment.getContentFragment(R.id.map_view);
            showResult(NearMapUtil.nearOutletList(getArgNearOutlet()));
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.saveThrowable(e);
            UI.alertError(getActivity(), (String) ErrorUtil.getErrorMessage(e).message);
        }
    }

    private void showResult(MyArray<MapItem> argNearMaps) {
        this.nearMapList = argNearMaps.asList();
        if (nearMapList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        adapter = new MapListAdapter(getActivity());
        adapter.setItems(argNearMaps);
        adapter.setOnInterceptListener(new InterceptListener<Object>() {
            @Override
            public void intercept(int key, Object result) {
                if (key == 1) {
                    if (open_map) {
                        setMapScreen(false, (MapItem) result);
                    } else {
                        selectNearMap = (MapItem) result;
                        mapFragment.selectMarker((MapItem) result, false);
                    }
                } else {
                    ArgOutlet argOutlet = new ArgOutlet(getArgNearOutlet(), ((MapItem) result).outlet.id);
                    OutletIndexFragment.open(argOutlet);
                }
            }
        });

        listView.setAdapter(adapter);
        selectNearMap = nearMapList.get(0);

        if (nearMapList.size() != 0) {
            layout.setCollapseHeight(getListViewItemHeight());
        } else {
            layout.setCollapseHeight(0);
        }

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapFragment.setSearch(nearMapList);
            }
        }, 2000);
    }

    public void setMapScreen(boolean open, MapItem nearMap) {
        selectNearMap = nearMap;
        if (open) {
            if (layout.isExpanded()) {
                layout.collapse();
            }
            panel.removeAllViewsInLayout();

            if (selectNearMap != null) {
                int index = nearMapList.indexOf(selectNearMap);
                if (index == -1) return;
                panel.addView(adapter.getView(index, null, null));
            }
        } else {
            if (!layout.isExpanded()) {
                layout.expand();
                panel.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
            panel.removeAllViewsInLayout();
            lastSwipe = true;
        }
        listView.setDisableScroll(open);
        open_map = open;

        mapFragment.resetCamera(open ? 1 : 2);
    }

    public void onSwipeListBottom() {
        try {
            if (!open_map) {
                if (listView.getChildAt(0).getTop() == listView.getTop()) {
                    if (lastSwipe) {
                        setMapScreen(true, selectNearMap);
                        lastSwipe = false;
                    } else {
                        lastSwipe = true;
                    }
                } else {
                    lastSwipe = false;
                }
            }
        } catch (Exception ignore) {
        }
    }

    public int getListViewItemHeight() {
        int index = 0;
        for (MapItem nearMap : nearMapList) {
            index = nearMapList.indexOf(nearMap);
        }
        int height = 0;
        View childView = adapter.getView(index, null, listView);
        if (childView != null) {
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                    .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            height = childView.getMeasuredHeight();
        }
        return height;
    }
}
