package uz.greenwhite.smartup5_trade.m_outlet.categorization;

import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResult;
import uz.greenwhite.smartup5_trade.m_outlet.variable.VCategorization;

public class CatApi {

    public static void saveCategories(ArgOutlet arg, VCategorization vCategorization, boolean ready) throws Exception {
        CatResult catResult = vCategorization.toValue();
        Scope scope = arg.getScope();
        scope.entry.saveCategorization(new CatHolder(vCategorization.entryId, catResult, vCategorization.entryState), ready);

    }

    public static void catMakeEdit(ArgOutlet arg, CatData data) {
        Scope scope = arg.getScope();
        String entryId = data.vCategorization.entryId;
        scope.ds.db.tryMakeStateSaved(entryId);
    }
}