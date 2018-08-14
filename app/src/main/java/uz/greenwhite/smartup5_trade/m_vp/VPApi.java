package uz.greenwhite.smartup5_trade.m_vp;// 23.09.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_session.bean.OutletPlan;

public class VPApi {

    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    public static void save(Scope scope, MyArray<OutletPlan> vOutlets) {
        vOutlets.checkUniqueness(OutletPlan.KEY_ADAPTER);

        for (OutletPlan ov : vOutlets) {
            int state = scope.ds.db.entryLoadState(ov.localId);
            if (state == EntryState.NOT_SAVED ||
                    state == EntryState.READY ||
                    state == EntryState.SAVED) {
                if (state == EntryState.READY) {
                    scope.ds.db.tryMakeStateSaved(ov.localId);
                }
            } else {
                throw new AppError("can't change state entryId is locked");
            }
        }
        for (OutletPlan ov : vOutlets) {
            scope.entry.saveOutletVisit(scope, ov);
        }
    }
}
