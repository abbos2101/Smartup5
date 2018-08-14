package uz.greenwhite.smartup5_trade.m_deal.variable.quiz;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.smartup5_trade.common.module.VForm;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealModule;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealQuiz;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealQuizModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.VDealModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class VDealQuizModule extends VDealModule {

    public final ValueArray<VDealQuizForm> forms;

    public VDealQuizModule(VisitModule module, ValueArray<VDealQuizForm> forms) {
        super(module);
        this.forms = forms;
    }

    private void convertQuizs(List<DealQuiz> r, VDealQuizForm vForm, VDealQuiz vQuiz) {
        if (vQuiz.hasValue()) {
            r.add(new DealQuiz(vForm.quizSet.quizSetId, vQuiz.quiz.quizId, vQuiz.answer.getText(), vQuiz.rootQuizId));
            for (VDealQuiz vChildQuiz : DealUtil.filterQuizChild(vQuiz)) {
                convertQuizs(r, vForm, vChildQuiz);
            }
        }
    }

    @Override
    public DealModule convertToDealModule() {
        List<DealQuiz> r = new ArrayList<>();
        for (VDealQuizForm form : forms.getItems()) {
            for (VDealQuiz quiz : form.quizzes.getItems()) {
                convertQuizs(r, form, quiz);
            }
        }
        return new DealQuizModule(MyArray.from(r));
    }

    @Override
    public MyArray<VForm> getModuleForms() {
        return this.forms.getItems().toSuper();
    }

    @Override
    public boolean hasValue() {
        for (VDealQuizForm form : forms.getItems()) {
            if (form.hasValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(forms).toSuper();
    }
}
