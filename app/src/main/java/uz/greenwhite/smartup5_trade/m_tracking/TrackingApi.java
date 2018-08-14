package uz.greenwhite.smartup5_trade.m_tracking;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.collection.MyReducer;
import uz.greenwhite.lib.view_setup.DialogBuilder;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_order_info.arg.ArgOrderInfo;
import uz.greenwhite.smartup5_trade.m_order_info.ui.OrderInfoIndexFragment;
import uz.greenwhite.smartup5_trade.m_tracking.arg.ArgTracking;
import uz.greenwhite.smartup5_trade.m_tracking.bean.DealInfo;
import uz.greenwhite.smartup5_trade.m_tracking.bean.TROutlet;

public class TrackingApi {

    public static String getOutletDetail(TROutlet item) {
        StringBuilder sbDetail = new StringBuilder();
        if (item.deals.size() == 1) {
            DealInfo dealInfo = item.deals.get(0);
            sbDetail.append(DS.getString(R.string.tracking_last_visit)).append(" <i>")
                    .append(dealInfo.visitDate).append("</i><br/>");


            if (!TextUtils.isEmpty(dealInfo.location) && !TextUtils.isEmpty(item.latLon)) {
                sbDetail.append(DS.getString(R.string.tracking_distance)).append(" ")
                        .append(LocationUtil.getDistance(item.latLon, dealInfo.location)).append("<br/>");
            }
        }
        sbDetail.append(DS.getString(R.string.tracking_status)).append(" ")
                .append(item.getState());
        return sbDetail.toString();
    }

    public static void showDealInfo(final Activity activity,
                                    final MyArray<DealInfo> deals,
                                    final String latLng,
                                    final ArgTracking argTracking) {
        if (deals.size() > 1) {
            UI.dialog()
                    .option(deals, new DialogBuilder.CommandFacade<DealInfo>() {
                        @Override
                        public CharSequence getName(DealInfo val) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(DS.getString(R.string.tracking_last_visit))
                                    .append(val.visitDate).append("<br/>");
                            if (!TextUtils.isEmpty(val.location) && !TextUtils.isEmpty(latLng)) {
                                sb.append(DS.getString(R.string.tracking_distance))
                                        .append(LocationUtil.getDistance(latLng, val.location));
                            }
                            return Html.fromHtml(sb.toString());
                        }

                        @Override
                        public void apply(DealInfo val) {
                            OrderInfoIndexFragment.open(new ArgOrderInfo(argTracking, val.dealId, val.state));
                        }
                    }).show(activity);
        } else {
            DealInfo dealInfo = deals.get(0);
            OrderInfoIndexFragment.open(new ArgOrderInfo(argTracking, dealInfo.dealId, dealInfo.state));
        }

    }

    public static MyArray<DealInfo> makeDealItem(MyArray<TROutlet> listItems, final String key) {
        MyArray<TROutlet> toRows = listItems.filter(new MyPredicate<TROutlet>() {
            @Override
            public boolean apply(TROutlet to) {
                return key.equals(to.outletId) &&
                        (TROutlet.VISITED.equals(to.state) || TROutlet.EXTRAORDINARY.equals(to.state)) &&
                        to.deals.nonEmpty();
            }
        });

        return toRows.reduce(MyArray.<DealInfo>emptyArray(), new MyReducer<MyArray<DealInfo>, TROutlet>() {
            @Override
            public MyArray<DealInfo> apply(MyArray<DealInfo> result, TROutlet item) {
                return result.append(item.deals);
            }
        });
    }
}
