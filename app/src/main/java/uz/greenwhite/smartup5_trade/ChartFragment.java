package uz.greenwhite.smartup5_trade;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;

import java.util.ArrayList;
import java.util.Random;

import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.mold.MoldContentFragment;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class ChartFragment extends MoldContentFragment {

    public static void open(Activity activity) {
        Mold.openContent(activity, ChartFragment.class);
    }

    private ViewSetup vsRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.z_chart);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<ChartSet> result = new ArrayList<>();

        int[] color = new int[]{DS.getColor(R.color.red), DS.getColor(R.color.green)};
        int[] fill = new int[]{DS.getColor(R.color.red_alpha), DS.getColor(R.color.green_alpha)};
        for (int y = 0; y < 2; y++) {
            String[] titles = new String[10];
            float[] cordinates = new float[10];
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                titles[i] = String.valueOf(i);
                cordinates[i] = random.nextFloat();
            }

            LineSet lineSet = new LineSet(titles, cordinates);
//            lineSet.setDotsColor(DS.getColor(R.color.colorAccent));
            lineSet.setColor(color[y]);
            lineSet.setFill(fill[y]);
            lineSet.setSmooth(true);
            result.add(lineSet);
        }
        LineChartView chart = vsRoot.id(R.id.linechart);
        chart.addData(result);
        chart.setXAxis(false);
        chart.setYAxis(false);

        chart.setClickablePointRadius(10);
//        chart.setAxisColor(DS.getColor(R.color.app_color_7));
        chart.setLabelsColor(DS.getColor(R.color.app_color_7));
        chart.show();
    }
}
