package uz.greenwhite.smartup5_trade.m_outlet.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatQuiz;
import uz.greenwhite.smartup5_trade.m_outlet.bean.CatResultDetail;

public class VCategorizationRow extends VariableLike {

    public final CatQuiz catQuiz;
    public final ValueSpinner answer;

    public VCategorizationRow(CatQuiz catQuiz, ValueSpinner answer) {
        this.catQuiz = catQuiz;
        this.answer = answer;
    }

    public CatResultDetail toValue() {
        return new CatResultDetail(catQuiz.quizId, answer.getValue().code);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(answer).toSuper();
    }
}
