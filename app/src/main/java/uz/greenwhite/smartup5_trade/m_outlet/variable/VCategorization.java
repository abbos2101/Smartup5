package uz.greenwhite.smartup5_trade.m_outlet.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResult;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResultDetail;

public class VCategorization extends VariableLike {

    public final String entryId;
    public final String filialId;
    public final String outletId;
    public final String clientTypeId;
    public final EntryState entryState;
    public final ValueArray<VCategorizationRow> catRows;

    public VCategorization(String entryId,
                           String filialId,
                           String outletId,
                           String clientTypeId,
                           EntryState entryState,
                           ValueArray<VCategorizationRow> catRows) {
        this.entryId = entryId;
        this.filialId = filialId;
        this.outletId = outletId;
        this.clientTypeId = clientTypeId;
        this.entryState = entryState;
        this.catRows = catRows;
    }

    public CatResult toValue() {
        return new CatResult(filialId, outletId, clientTypeId,
                catRows.getItems().map(new MyMapper<VCategorizationRow, CatResultDetail>() {
                    @Override
                    public CatResultDetail apply(VCategorizationRow vCategorizationRow) {
                        return vCategorizationRow.toValue();
                    }
                }));
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return catRows.getItems().toSuper();
    }
}