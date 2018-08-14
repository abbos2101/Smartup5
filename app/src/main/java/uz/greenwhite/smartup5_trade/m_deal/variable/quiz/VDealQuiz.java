package uz.greenwhite.smartup5_trade.m_deal.variable.quiz;

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.TextValue;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueBigDecimal;
import uz.greenwhite.lib.variable.ValueBoolean;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.Quiz;
import uz.greenwhite.smartup5_trade.m_session.bean.quiz.QuizOption;

public class VDealQuiz extends VariableLike {

    public final Quiz quiz;
    public final TextValue answer;

    public final String rootQuizId;
    public final String optionId;
    public final ValueArray<VDealQuiz> childQuizes;

    public VDealQuiz(Quiz quiz, String answer, String rootQuizId, String optionId, ValueArray<VDealQuiz> childQuizes) {
        this.quiz = quiz;

        this.rootQuizId = rootQuizId;
        this.optionId = optionId;
        this.childQuizes = childQuizes;

        this.answer = makeValue(quiz);
        if (this.answer != null) {
            this.answer.setText(answer);
        }
    }

    private static TextValue makeValue(Quiz quiz) {
        switch (quiz.quizType) {
            case Quiz.TEXT:
            case Quiz.DATE:
                return new ValueString(200);

            case Quiz.NUMBER:
                BigDecimal scale = quiz.scale != null ? quiz.scale : BigDecimal.ZERO;
                return new ValueBigDecimal(20, scale.intValue());

            case Quiz.BOOLEAN:
                return new ValueBoolean();

            case Quiz.AUTOCOMPLETE:
            case Quiz.SELECT:
            case Quiz.SELECT_WITH_CODE:

                if (quiz.extraOption) {
                    return new VDealQuizExtra(makeOptions(quiz.options), new ValueString(80));
                } else {
                    MyArray<SpinnerOption> os = makeOptions(quiz.options).append(SpinnerOption.EMPTY);
                    return new ValueSpinner(os, SpinnerOption.EMPTY);
                }
        }
        return null;
    }

    private static MyArray<SpinnerOption> makeOptions(MyArray<QuizOption> options) {
        return options.map(new MyMapper<QuizOption, SpinnerOption>() {
            @Override
            public SpinnerOption apply(QuizOption option) {
                return new SpinnerOption(option.code, option.name, option);
            }
        });
    }

    @Override
    public ErrorResult getError() {
        ErrorResult error = answer.getError();
        if (error.isError()) {
            return error;
        }
        if (answer.getText().length() == 0) {
            return ErrorResult.NONE;
        }
        switch (quiz.quizType) {
            case Quiz.TEXT:
                int len = answer.getText().length();
                if (quiz.minValue != null && len < quiz.minValue.intValue()) {
                    return ErrorResult.make(DS.getString(R.string.deal_min_length, quiz.minValue.intValue()));
                }
                if (quiz.maxValue != null && len > quiz.maxValue.intValue()) {
                    return ErrorResult.make(DS.getString(R.string.deal_max_length, quiz.maxValue.intValue()));
                }
                break;
            case Quiz.NUMBER:
                ValueBigDecimal d = (ValueBigDecimal) answer;
                BigDecimal value = d.getValue();
                if (value != null) {
                    if (quiz.minValue != null && value.compareTo(quiz.minValue) < 0) {
                        return ErrorResult.make(DS.getString(R.string.deal_greater_than, quiz.minValue.intValue()));
                    }
                    if (quiz.maxValue != null && value.compareTo(quiz.maxValue) > 0) {
                        return ErrorResult.make(DS.getString(R.string.deal_less_than, quiz.maxValue.intValue()));
                    }
                }
                break;
        }
        return ErrorResult.NONE;
    }

    public boolean hasValue() {
        if (Quiz.BOOLEAN.equals(quiz.quizType)) {
            ValueBoolean r = (ValueBoolean) answer;
            return r.getValue();
        }
        return answer.getText().length() > 0;
    }

    public static final MyMapper<VDealQuiz, String> KEY_ADAPTER = new MyMapper<VDealQuiz, String>() {
        @Override
        public String apply(VDealQuiz val) {
            return val.quiz.quizId;
        }
    };

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(answer).toSuper();
    }
}

