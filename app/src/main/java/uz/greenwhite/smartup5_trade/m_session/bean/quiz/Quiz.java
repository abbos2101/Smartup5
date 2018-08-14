package uz.greenwhite.smartup5_trade.m_session.bean.quiz;


import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Quiz {

    public final String quizId;
    public final String name;
    public final String quizType;
    public final BigDecimal minValue;
    public final BigDecimal maxValue;
    public final BigDecimal scale;
    public final MyArray<QuizOption> options;
    public final boolean extraOption;
    public final boolean isParent;
    public final String rootQuizId;

    public static final String TEXT = "1";
    public static final String NUMBER = "2";
    public static final String BOOLEAN = "3";
    public static final String AUTOCOMPLETE = "4";
    public static final String SELECT = "5";
    public static final String DATE = "6";
    public static final String SELECT_WITH_CODE = "7";

    public Quiz(String quizId,
                String name,
                String quizType,
                BigDecimal minValue,
                BigDecimal maxValue,
                BigDecimal scale,
                MyArray<QuizOption> options,
                Boolean extraOption,
                Boolean isParent,
                String rootQuizId) {
        this.quizId = quizId;
        this.name = name;
        this.quizType = quizType;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.scale = scale;
        this.options = MyArray.nvl(options);
        this.extraOption = Util.nvl(extraOption, false);
        this.isParent = Util.nvl(isParent, false);
        this.rootQuizId = Util.nvl(rootQuizId);
    }

    public static final MyMapper<Quiz, String> KEY_ADAPTER = new MyMapper<Quiz, String>() {
        @Override
        public String apply(Quiz val) {
            return val.quizId;
        }
    };

    public static final UzumAdapter<Quiz> UZUM_ADAPTER = new UzumAdapter<Quiz>() {

        @Override
        public Quiz read(UzumReader in) {
            return new Quiz(in.readString(),
                    in.readString(), in.readString(),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readBigDecimal(), in.readArray(QuizOption.UZUM_ADAPTER),
                    in.readBoolean(), in.readBoolean(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, Quiz val) {
            out.write(val.quizId);
            out.write(val.name);
            out.write(val.quizType);
            out.write(val.minValue != null ? val.minValue.toString() : "");
            out.write((val.maxValue != null ? val.maxValue.toString() : ""));
            out.write(val.scale != null ? val.scale.toString() : "");
            out.write(val.options, QuizOption.UZUM_ADAPTER);
            out.write(val.extraOption);
            out.write(val.isParent);
            out.write(val.rootQuizId);
        }
    };
}
