package uz.greenwhite.smartup5_trade.m_deal.builder;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.util.CharSequenceUtil;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealQuiz;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealQuizModule;
import uz.greenwhite.smartup5_trade.m_deal.variable.DealRef;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuiz;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuizForm;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuizModule;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.Quiz;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizBind;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizChild;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizSet;

public class BuilderQuiz {

    public final DealRef dealRef;
    public final VisitModule module;
    public final MyArray<DealQuiz> initial;

    public BuilderQuiz(DealRef dealRef, VisitModule module) {
        this.dealRef = dealRef;
        this.module = module;
        this.initial = getInitial();
    }

    private MyArray<DealQuiz> getInitial() {
        DealQuizModule dealModule = dealRef.findDealModule(module.id);
        return dealModule != null ? dealModule.quizzes : MyArray.<DealQuiz>emptyArray();
    }

    private MyArray<QuizSet> getQuizSets() {
        Set<String> quizSets = dealRef.getFilialRoleQuizSetIds().asSet();

        for (DealQuiz quiz : initial) {
            if (quiz.rootQuizId.equals(quiz.quizId)) {
                quizSets.add(quiz.quizSetId);
            }
        }
        return MyArray.from(quizSets).map(new MyMapper<String, QuizSet>() {
            @Override
            public QuizSet apply(String quizSetId) {
                return dealRef.getQuizSet(quizSetId);
            }
        }).filterNotNull();
    }

    private MyArray<String> makeQuizIds(final QuizSet quizSet) {
        return initial.filter(new MyPredicate<DealQuiz>() {
            @Override
            public boolean apply(DealQuiz dealQuiz) {
                return dealQuiz.rootQuizId.equals(dealQuiz.quizId) && quizSet.quizSetId.equals(dealQuiz.quizSetId);
            }
        }).map(new MyMapper<DealQuiz, String>() {
            @Override
            public String apply(DealQuiz dealQuiz) {
                return dealQuiz.quizId;
            }
        });
    }

    @Nullable
    private VDealQuiz makeQuizItem(final QuizSet quizSet,
                                   @Nullable final QuizBind quizBind,
                                   final String quizId,
                                   final String rootQuizId,
                                   String optionId) {
        Quiz quiz = dealRef.getQuiz(quizId);
        if (quiz == null) {
            return null;
        }

        MyArray<VDealQuiz> vDealQuizs = MyArray.emptyArray();
        if (quizBind != null) {
            vDealQuizs = quizBind.childs.filter(new MyPredicate<QuizChild>() {
                @Override
                public boolean apply(QuizChild quizChild) {
                    return quizChild.parentQuizId.equals(quizId);
                }
            }).map(new MyMapper<QuizChild, VDealQuiz>() {
                @Override
                public VDealQuiz apply(QuizChild quizChild) {
                    return makeQuizItem(quizSet, quizBind, quizChild.childQuizId, rootQuizId, quizChild.optionId);
                }
            }).filterNotNull();
        }

        Tuple2 key = DealQuiz.getKey(quizSet.quizSetId, quiz.quizId);
        DealQuiz v = initial.find(key, DealQuiz.KEY_ADAPTER);
        String answer = "";
        if (v != null) {
            answer = v.value;
        }
        return new VDealQuiz(quiz, answer, rootQuizId, optionId, new ValueArray<>(vDealQuizs));
    }

    private MyArray<VDealQuiz> makeQuizzes(QuizSet quizSet, MyArray<String> quizIds) {
        ArrayList<VDealQuiz> result = new ArrayList<>();
        for (String quizId : quizSet.quizIds.union(quizIds)) {
            QuizBind quizBind = dealRef.getQuizBind(quizId);
            VDealQuiz vDealQuiz = makeQuizItem(quizSet, quizBind, quizId, quizId, "");
            if (vDealQuiz != null) {
                result.add(vDealQuiz);
            }
        }
        return MyArray.from(result);
    }

    private ValueArray<VDealQuizForm> makeForms() {
        MyArray<QuizSet> quizSets = getQuizSets();
        ArrayList<VDealQuizForm> result = new ArrayList<>();
        for (QuizSet quizSet : quizSets) {
            MyArray<VDealQuiz> vDealQuizs = makeQuizzes(quizSet, makeQuizIds(quizSet));
            if (vDealQuizs.nonEmpty()) {
                result.add(new VDealQuizForm(module, quizSet, new ValueArray<>(vDealQuizs)));
            }
        }
        Collections.sort(result, new Comparator<VDealQuizForm>() {
            @Override
            public int compare(VDealQuizForm l, VDealQuizForm r) {
                return CharSequenceUtil.compareToIgnoreCase(l.quizSet.orderNo, r.quizSet.orderNo);
            }
        });
        return new ValueArray<>(MyArray.from(result));
    }

    public VDealQuizModule build() {
        return new VDealQuizModule(module, makeForms());
    }

}
