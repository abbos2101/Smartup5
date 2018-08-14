package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.Mold;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.m_deal.DealUtil;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuiz;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuizForm;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.Quiz;

public class QuizFragment extends DealFormContentFragment {

    private ViewSetup vsRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vsRoot = new ViewSetup(inflater, container, R.layout.z_quiz);
        return vsRoot.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Mold.setTitle(getActivity(), R.string.quiz);
        VDealQuizForm vDealQuizForm = DealUtil.getDealForm(this);

        MyArray<VDealQuiz> items = vDealQuizForm.quizzes.getItems();
        makeQuizs(vsRoot.viewGroup(R.id.liner_layout), items);
    }

    private void makeQuizs(ViewGroup viewGroup, MyArray<VDealQuiz> items) {
        for (int i = 0; i < items.size(); i++) {
            View v;
            final VDealQuiz vDealQuiz = items.get(i);
            if (vDealQuiz.answer == null) {
                continue;
            }

            QuizRow.QuizSelect quizSelect = new QuizRow.QuizSelect() {
                @Override
                public void onChange(ViewSetup viewSetup) {
                    ViewGroup vg = viewSetup.viewGroup(R.id.ll_child_quiz);
                    vg.removeAllViews();

                    if (Quiz.BOOLEAN.equals(vDealQuiz.quiz.quizType) || vDealQuiz.hasValue()) {
                        MyArray<VDealQuiz> vDealQuizs = DealUtil.filterQuizChild(vDealQuiz);
                        if (vDealQuizs.nonEmpty()) {
                            vg.setVisibility(View.VISIBLE);
                            makeQuizs(vg, vDealQuizs);
                        } else {
                            vg.setVisibility(View.GONE);
                        }
                    } else {
                        vg.setVisibility(View.GONE);
                    }
                }
            };
            switch (vDealQuiz.quiz.quizType) {
                case Quiz.TEXT:
                    v = QuizRow.getTextView(getActivity(), vDealQuiz);
                    break;
                case Quiz.NUMBER:
                    v = QuizRow.getNumberView(getActivity(), vDealQuiz, quizSelect);
                    break;
                case Quiz.BOOLEAN:
                    v = QuizRow.getBoleanView(getActivity(), vDealQuiz, quizSelect);
                    break;
                case Quiz.AUTOCOMPLETE:
                case Quiz.SELECT:
                case Quiz.SELECT_WITH_CODE:
                    if (vDealQuiz.quiz.extraOption) {
                        v = QuizRow.getSelectExtraView(getActivity(), vDealQuiz, quizSelect);
                    } else {
                        v = QuizRow.getSelectView(getActivity(), vDealQuiz, quizSelect);
                    }
                    break;
                case Quiz.DATE:
                    v = QuizRow.getDateView(getActivity(), vDealQuiz);
                    break;
                default:
                    continue;
            }
            viewGroup.addView(v);
        }
    }
}
