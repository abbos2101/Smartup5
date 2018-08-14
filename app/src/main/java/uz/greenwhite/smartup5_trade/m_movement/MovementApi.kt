package uz.greenwhite.smartup5_trade.m_movement

import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingPost


object MovementApi {
    fun loadWarehouseIncoming(scope: Scope, warehouseId: String) = scope.entry.allIncoming.filter { warehouseId == it.incoming.warehouseId }

    fun dealDelete(scope: Scope, incoming: MovementIncomingPost) {
        scope.ds.db.entryDelete(incoming.entryId)
    }

    fun dealDelete(scope: Scope, entryId: String) {
        scope.ds.db.entryDelete(entryId)
    }

    fun dealMakeEdit(scope: Scope, incoming: MovementIncomingPost) {
        scope.ds.db.tryMakeStateSaved(incoming.entryId)
    }

    fun dealMakeEdit(scope: Scope, entryId: String) {
        scope.ds.db.tryMakeStateSaved(entryId)
    }

    fun saveDeal(scope: Scope, incoming: MovementIncomingPost, ready: Boolean) {
        scope.entry.saveMovementIncoming(incoming, ready)
    }
}
