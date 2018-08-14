package uz.greenwhite.smartup5_trade.m_session;// 24.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHolder;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletGroup;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletType;

public class SessionApi {

    @SuppressWarnings("ConstantConditions")
    public static MyArray<DealHolder> getOrderDeals(Scope scope) {
        return scope.entry.getOrderDeals();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<DealHolder> getReturnDeals(Scope scope) {
        return scope.entry.getReturnDeals();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<DealHolder> getExtraordinaryDeals(Scope scope) {
        return scope.entry.getExtraordinaryDeals();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<OutletGroup> getOutletGroups(Scope scope) {
        return scope.ref.getOutletGroups();
    }

    @SuppressWarnings("ConstantConditions")
    public static MyArray<OutletType> getOutletTypes(Scope scope) {
        return scope.ref.getOutletTypes();
    }
}
