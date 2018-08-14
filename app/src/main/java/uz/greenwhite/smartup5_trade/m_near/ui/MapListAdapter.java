package uz.greenwhite.smartup5_trade.m_near.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import uz.greenwhite.lib.collection.MyAdapter;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_near.bean.MapItem;
import uz.greenwhite.smartup5_trade.m_near.util.InterceptListener;

public class MapListAdapter extends MyAdapter<MapItem, MapListAdapter.ViewHolder> {

    private InterceptListener<Object> intercept;

    public MapListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.near_map_list_item;
    }

    @Override
    public ViewHolder makeHolder(View view) {
        ViewHolder h = new ViewHolder();
        h.title = UI.id(view, R.id.tv_title);
        h.detail = UI.id(view, R.id.tv_detail);
        h.btnNext = UI.id(view, R.id.btn_next);
        h.btnBody = UI.id(view, R.id.btn_body);
        return h;
    }

    @Override
    public void populate(ViewHolder holder, final MapItem item) {
        holder.title.setText(item.title);
        holder.detail.setText(item.detail);
        holder.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intercept.intercept(2, item);
            }
        });

        holder.btnBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intercept.intercept(1, item);
            }
        });
    }

    class ViewHolder {
        TextView title;
        TextView detail;
        FrameLayout btnNext;
        LinearLayout btnBody;
    }

    public void setOnInterceptListener(InterceptListener<Object> intercept) {
        this.intercept = intercept;
    }
}
