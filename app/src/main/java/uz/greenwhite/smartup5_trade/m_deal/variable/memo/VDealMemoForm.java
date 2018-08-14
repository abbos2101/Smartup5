package uz.greenwhite.smartup5_trade.m_deal.variable.memo;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_outlet.bean.Memo;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealMemoForm extends VDealForm {

    public final MyArray<Memo> memos;
    public final ValueString memo;

    public VDealMemoForm(VisitModule module, MyArray<Memo> memos, String memo) {
        super(module);
        this.memos = memos;
        this.memo = new ValueString(200, memo);
    }

    @Override
    public boolean hasValue() {
        return memo.nonEmpty();
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(memo).toSuper();
    }
}
