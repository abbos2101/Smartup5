package uz.greenwhite.smartup5_trade.m_shipped;// 08.09.2016

import android.text.TextUtils;

import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.m_outlet.bean.SDeal;
import uz.greenwhite.smartup5_trade.m_shipped.bean.SDealHolder;

public class ShippedApi {

    public static void dealDelete(Scope scope, SDealHolder holder) {
        scope.ds.db.entryDelete(holder.entryId);
    }

    public static void dealMakeEdit(Scope scope, SDealHolder holder) {
        scope.ds.db.tryMakeStateSaved(holder.entryId);
        scope.ds.db.photoUpdateStateByEntry(holder.entryId, EntryState.SAVED);
    }

    @SuppressWarnings("ConstantConditions")
    public static void saveDeal(Scope scope, String entryId, SDeal deal, boolean ready) {
        if (TextUtils.isEmpty(entryId)) {
            entryId = String.valueOf(AdminApi.nextSequence());
        }

        scope.entry.saveSDeal(entryId, deal, ready);

    }
}
