package uz.greenwhite.smartup5_trade.m_deal.variable.quiz;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealForm;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizSet;

public class VDealQuizForm extends VDealForm {

    public final QuizSet quizSet;
    public ValueArray<VDealQuiz> quizzes;

    public VDealQuizForm(VisitModule module, QuizSet quizSet, ValueArray<VDealQuiz> quizzes) {
        super(module, "" + module.id + ":" + quizSet.quizSetId);
        this.quizSet = quizSet;
        this.quizzes = quizzes;
    }

    @Override
    public CharSequence getTitle() {
        return quizSet.name;
    }

    @Override
    public boolean hasValue() {
        for (VDealQuiz quiz : quizzes.getItems()) {
            if (quiz.hasValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(quizzes).toSuper();
    }
}
