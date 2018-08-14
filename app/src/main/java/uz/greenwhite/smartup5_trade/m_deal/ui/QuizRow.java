package uz.greenwhite.smartup5_trade.m_deal.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.view_setup.ModelChange;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.lib.view_setup.ViewSetup;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuiz;
import uz.greenwhite.smartup5_trade.m_deal.variable.quiz.VDealQuizExtra;

public class QuizRow {

    @SuppressWarnings("ConstantConditions")
    public static View getTextView(Activity activity, final VDealQuiz vDealQuiz) {
        ViewSetup vs = new ViewSetup(activity, R.layout.deal_quiz_text);
        final TextView cError = vs.id(R.id.error);
        vs.textView(R.id.title).setText(vDealQuiz.quiz.name);
        vs.bind(R.id.et_quiz_text, vDealQuiz.answer);
        vs.model(R.id.et_quiz_text).add(new ModelChange() {
            @Override
            public void onChange() {
                ErrorResult error = vDealQuiz.getError();
                if (error.isError()) {
                    cError.setText(error.getErrorMessage());
                    cError.setVisibility(View.VISIBLE);
                } else {
                    cError.setText("");
                    cError.setVisibility(View.GONE);
                }
            }
        }).notifyListeners();
        return vs.view;
    }

    @SuppressWarnings("ConstantConditions")
    public static View getNumberView(Activity activity, final VDealQuiz vDealQuiz, final QuizSelect quizSelect) {
        final ViewSetup vs = new ViewSetup(activity, R.layout.deal_quiz_number);

        if (TextUtils.isEmpty(vDealQuiz.quiz.rootQuizId) ||
                vDealQuiz.quiz.rootQuizId.equals(vDealQuiz.quiz.quizId)) {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.text_color));
        } else {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.normal_silver));
        }
        final TextView cError = vs.id(R.id.error);
        vs.textView(R.id.title).setText(vDealQuiz.quiz.name);
        vs.bind(R.id.et_quiz_number, vDealQuiz.answer);
        vs.model(R.id.et_quiz_number).add(new ModelChange() {
            @Override
            public void onChange() {
                ErrorResult error = vDealQuiz.getError();
                if (error.isError()) {
                    cError.setText(error.getErrorMessage());
                    cError.setVisibility(View.VISIBLE);
                } else {
                    cError.setText("");
                    cError.setVisibility(View.GONE);
                }
                if (quizSelect != null) quizSelect.onChange(vs);
            }
        }).notifyListeners();
        return vs.view;
    }

    @SuppressWarnings("ConstantConditions")
    public static View getBoleanView(Activity activity, VDealQuiz vDealQuiz, final QuizSelect quizSelect) {
        final ViewSetup vs = new ViewSetup(activity, R.layout.deal_quiz_chekbox);
        if (TextUtils.isEmpty(vDealQuiz.quiz.rootQuizId) ||
                vDealQuiz.quiz.rootQuizId.equals(vDealQuiz.quiz.quizId)) {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.text_color));
        } else {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.normal_silver));
        }
        vs.textView(R.id.title).setText(vDealQuiz.quiz.name);
        vs.bind((R.id.cb_quiz_checkbox), (ValueBoolean) vDealQuiz.answer);
        vs.model(R.id.cb_quiz_checkbox).add(new ModelChange() {
            @Override
            public void onChange() {
                if (quizSelect != null) quizSelect.onChange(vs);
            }
        }).notifyListeners();
        return vs.view;
    }

    @SuppressWarnings("ConstantConditions")
    public static View getSelectView(Activity activity, VDealQuiz vDealQuiz, final QuizSelect quizSelect) {
        final ViewSetup vs = new ViewSetup(activity, R.layout.deal_quiz_spinner);
        if (TextUtils.isEmpty(vDealQuiz.quiz.rootQuizId) ||
                vDealQuiz.quiz.rootQuizId.equals(vDealQuiz.quiz.quizId)) {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.text_color));
        } else {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.normal_silver));
        }
        vs.textView(R.id.title).setText(vDealQuiz.quiz.name);
        UI.bind(vs.spinner(R.id.s_quiz_spinner), (ValueSpinner) vDealQuiz.answer, true);
        vs.model(R.id.s_quiz_spinner).add(new ModelChange() {
            @Override
            public void onChange() {
                if (quizSelect != null) quizSelect.onChange(vs);
            }
        }).notifyListeners();
        return vs.view;
    }

    public static View getSelectExtraView(Activity activity, VDealQuiz vDealQuiz, final QuizSelect quizSelect) {
        final VDealQuizExtra v = (VDealQuizExtra) vDealQuiz.answer;

        final ViewSetup vs = new ViewSetup(activity, R.layout.deal_quiz_spinner);
        if (TextUtils.isEmpty(vDealQuiz.quiz.rootQuizId) ||
                vDealQuiz.quiz.rootQuizId.equals(vDealQuiz.quiz.quizId)) {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.text_color));
        } else {
            vs.textView(R.id.title).setTextColor(DS.getColor(R.color.normal_silver));
        }
        vs.textView(R.id.title).setText(vDealQuiz.quiz.name);

        Spinner cSpinner = vs.id(R.id.s_quiz_spinner);
        final EditText cExtra = vs.id(R.id.your_answer);
        cExtra.setVisibility(View.VISIBLE);

        UI.bind(cSpinner, v.spinner, true);
        UI.bind(cExtra, v.extra);

        vs.model(R.id.s_quiz_spinner).add(new ModelChange() {
            @Override
            public void onChange() {
                String text = v.getText();
                cExtra.setEnabled(text.length() == 0);
                if (quizSelect != null) quizSelect.onChange(vs);
            }
        }).notifyListeners();
        return vs.view;
    }

    @SuppressWarnings("ConstantConditions")
    public static View getDateView(Activity activity, VDealQuiz vDealQuiz) {
        ViewSetup vs = new ViewSetup(activity, R.layout.deal_quiz_date);
        vs.textView(R.id.title).setText(vDealQuiz.quiz.name);
        vs.bind(R.id.et_quiz_date, vDealQuiz.answer);
        UI.makeDatePicker(vs.editText(R.id.et_quiz_date), true);
        return vs.view;
    }

    static interface QuizSelect {
        void onChange(ViewSetup viewSetup);
    }
}