package uz.greenwhite.smartup5_trade.m_deal;// 30.06.2016

import android.graphics.Bitmap;

import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.m_deal.bean.Deal;

public class DealApi {

    public static Bitmap getPhotoInTable(Scope scope, String sha) {
        return scope.ds.loadThumbSha(sha);
    }

    public static Bitmap getFullPhotoInTable(Scope scope, String sha) {
        return scope.ds.loadPhotoSha(sha);
    }

    //----------------------------------------------------------------------------------------------

    public static boolean isCalculatorKeyboard() {
        return AdminApi.isCalculatorKeyboard();
    }

    //----------------------------------------------------------------------------------------------

    public static void dealDelete(Scope scope, Deal deal) {
        scope.ds.db.entryDelete(deal.dealLocalId);
    }

    public static void dealMakeEdit(Scope scope, Deal deal) {
        scope.ds.db.tryMakeStateSaved(deal.dealLocalId);
        scope.ds.db.photoUpdateStateByEntry(deal.dealLocalId, EntryState.SAVED);
    }

    public static void saveDeal(Scope scope, Deal deal, boolean ready) {
        assert scope.entry != null;
        scope.entry.saveDeal(deal, ready);
    }
}
