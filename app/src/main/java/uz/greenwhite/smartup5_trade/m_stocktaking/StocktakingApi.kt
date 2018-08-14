package uz.greenwhite.smartup5_trade.m_stocktaking

import uz.greenwhite.smartup5_trade.datasource.Scope
import uz.greenwhite.smartup5_trade.m_stocktaking.bean.Stocktaking

object StocktakingApi {

    fun loadWarehouseStocktaking(scope: Scope, warehouseId: String) = scope.entry.allStocktaking.filter { warehouseId == it.stocktaking.warehouseId }

    fun dealDelete(scope: Scope, stocktaking: Stocktaking) {
        scope.ds.db.entryDelete(stocktaking.localId)
    }

    fun dealDelete(scope: Scope, entryId: String) {
        scope.ds.db.entryDelete(entryId)
    }

    fun dealMakeEdit(scope: Scope, stocktaking: Stocktaking) {
        scope.ds.db.tryMakeStateSaved(stocktaking.localId)
    }

    fun dealMakeEdit(scope: Scope, entryId: String) {
        scope.ds.db.tryMakeStateSaved(entryId)
    }

    fun saveDeal(scope: Scope, stocktaking: Stocktaking, ready: Boolean) {
        scope.entry.saveStocktaking(stocktaking, ready)
    }

}
