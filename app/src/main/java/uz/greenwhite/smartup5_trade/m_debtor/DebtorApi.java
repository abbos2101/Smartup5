package uz.greenwhite.smartup5_trade.m_debtor;

import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.RT;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_debtor.bean.Debtor;

public class DebtorApi {

    public static void debtorDelete(Scope scope, String entryId) {
        scope.ds.db.entryDelete(entryId);
    }

    public static void debtorMakeEdit(Scope scope, String entryId) {
        scope.ds.db.tryMakeStateSaved(entryId);
    }

    public static void saveDeal(Scope scope, Debtor debtor, boolean ready) {
        scope.ds.db.entrySave(debtor.localId, scope.filialId,
                RT.PERSON_DEBTOR, Uzum.toBytes(debtor, Debtor.UZUM_ADAPTER));

        if (ready) {
            scope.ds.db.tryMakeStateReady(debtor.localId);
            int state = scope.ds.db.entryLoadState(debtor.localId);
            if (state != EntryState.READY) {
                throw new AppError(DS.getString(R.string.deal_error_in_ready_deal));
            }
        }
    }
}
