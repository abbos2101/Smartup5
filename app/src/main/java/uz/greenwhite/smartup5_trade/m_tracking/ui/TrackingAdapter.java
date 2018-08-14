package uz.greenwhite.smartup5_trade.m_tracking.ui;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uz.greenwhite.lib.collection.MyAdapter;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TROutlet;

import static uz.greenwhite.smartup5_trade.datasource.DS.getString;


public class TrackingAdapter extends MyAdapter<TROutlet, TrackingAdapter.ViewHolder> {

    public TrackingAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.tracking_row;
    }

    @Override
    public ViewHolder makeHolder(View view) {
        ViewHolder h = new ViewHolder();
        h.icon = UI.id(view, R.id.state);
        h.name = UI.id(view, R.id.name);
        h.address = UI.id(view, R.id.address);
        h.data = UI.id(view, R.id.data);
        return h;
    }

    @Override
    public void populate(ViewHolder holder, TROutlet item) {

        StringBuilder sb = new StringBuilder();
        if (item.deals.size() > 1) {
            sb.append(item.deals.size()).append(getString(R.string.tracking_order)).append("<br/>");
        } else if (item.deals.size() == 1) {
            sb.append(item.deals.isEmpty() ? "" :
                    "<i>" + item.deals.get(0).visitDate + "</i><br/>");
        }

        if (item.isTrackingLocationEmpty()) {
            sb.append(getString(R.string.tracking_location_is_not)).append("<br/>");
        }

        sb.append(item.getState());

        holder.icon.setImageResource(item.getStateIcon());
        holder.name.setText(UI.html().b().v(item.name).b().html());
        holder.address.setText(item.address);
        holder.data.setText(Html.fromHtml(sb.toString()));
    }

    class ViewHolder {
        ImageView icon;
        TextView name;
        TextView address;
        TextView data;
    }
}
