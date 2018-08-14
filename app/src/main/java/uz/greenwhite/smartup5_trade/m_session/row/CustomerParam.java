package uz.greenwhite.smartup5_trade.m_session.row;// 29.12.2016

import java.util.Comparator;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;
import uz.greenwhite.smartup5_trade.m_session.bean.dashboard.DOutlet;

public class CustomerParam {

    public static CustomerParam newInstance(Outlet outlet,
                                            MyArray<DealHolder> deals,
                                            boolean completedVisit) {
        String lastDealDate = getLastDealDate(deals);
        return new CustomerParam(outlet.id, outlet.name, outlet.barcode, outlet.photoSha,
                completedVisit, deals.nonEmpty(), lastDealDate);
    }

    public static CustomerParam newInstance(Outlet outlet, boolean visited, boolean completedVisit) {
        return new CustomerParam(outlet.id, outlet.name, outlet.barcode, outlet.photoSha,
                completedVisit, visited, "");
    }

    public static CustomerParam newInstance(DOutlet outlet, MyArray<DealHolder> deals) {
        String lastDealDate = getLastDealDate(deals);
        return new CustomerParam(outlet.outletId, outlet.name, "", "", true, deals.nonEmpty(), lastDealDate);
    }

    public static CustomerParam newInstance(DOutlet outlet, boolean visited) {
        return new CustomerParam(outlet.outletId, outlet.name, "", "", true, visited, "");
    }

    private static String getLastDealDate(MyArray<DealHolder> holders) {
        if (holders.isEmpty()) return "";
        return holders.sort(new Comparator<DealHolder>() {
            @Override
            public int compare(DealHolder l, DealHolder r) {
                return CharSequenceUtil.compareToIgnoreCase(r.deal.header.begunOn, l.deal.header.begunOn);
            }
        }).get(0).deal.header.begunOn;
    }

    public final String outletId;
    public final String outletName;
    public final String outletBarcode;
    public final String outletPhotoSha;
    public final boolean completedVisit;
    public final boolean visited;
    public final String lastVisit;

    public CustomerParam(String outletId,
                         String outletName,
                         String outletBarcode,
                         String outletPhotoSha,
                         boolean completedVisit,
                         boolean visited,
                         String lastVisit) {
        this.outletId = outletId;
        this.outletName = outletName;
        this.outletBarcode = outletBarcode;
        this.outletPhotoSha = outletPhotoSha;
        this.completedVisit = completedVisit;
        this.visited = visited;
        this.lastVisit = lastVisit;
    }
}
