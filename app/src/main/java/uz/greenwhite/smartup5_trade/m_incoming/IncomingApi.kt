package uz.greenwhite.smartup5_trade.m_incoming

import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_incoming.bean.Incoming

object IncomingApi {

    fun loadWarehouseIncoming(scope: Scope, warehouseId: String) = scope.entry.allIncoming.filter { warehouseId == it.incoming.warehouseId }

    fun dealDelete(scope: Scope, incoming: Incoming) {
        scope.ds.db.entryDelete(incoming.localId)
    }

    fun dealDelete(scope: Scope, entryId: String) {
        scope.ds.db.entryDelete(entryId)
    }

    fun dealMakeEdit(scope: Scope, incoming: Incoming) {
        scope.ds.db.tryMakeStateSaved(incoming.localId)
    }

    fun dealMakeEdit(scope: Scope, entryId: String) {
        scope.ds.db.tryMakeStateSaved(entryId)
    }

    fun saveDeal(scope: Scope, incoming: Incoming, ready: Boolean) {
        scope.entry.saveIncoming(incoming, ready)
    }

}