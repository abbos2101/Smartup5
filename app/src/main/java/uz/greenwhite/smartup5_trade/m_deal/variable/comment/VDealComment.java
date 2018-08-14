package uz.greenwhite.smartup5_trade.m_deal.variable.comment;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_session.bean.Comment;

public class VDealComment extends VariableLike {

    public final Comment comment;
    public final ValueBoolean check;

    public VDealComment(Comment comment, boolean check) {
        this.comment = comment;
        this.check = new ValueBoolean(check);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(check).toSuper();
    }
}
