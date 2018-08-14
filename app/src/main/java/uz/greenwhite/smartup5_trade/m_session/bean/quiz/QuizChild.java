package uz.greenwhite.smartup5_trade.m_session.bean.quiz;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class QuizChild {

    public final String parentQuizId;
    public final String optionId;
    public final String childQuizId;


    public QuizChild(String parentQuizId, String optionId, String childQuizId) {
        this.parentQuizId = parentQuizId;
        this.optionId = optionId;
        this.childQuizId = childQuizId;
    }

    public static Tuple2 getKey(String quizId, String optionId) {
        return new Tuple2(quizId, optionId);
    }

    public static final MyMapper<QuizChild, Tuple2> KEY_ADAPTER = new MyMapper<QuizChild, Tuple2>() {
        @Override
        public Tuple2 apply(QuizChild val) {
            return getKey(val.parentQuizId, val.optionId);
        }
    };

    public static final UzumAdapter<QuizChild> UZUM_ADAPTER = new UzumAdapter<QuizChild>() {
        @Override
        public QuizChild read(UzumReader in) {
            return new QuizChild(in.readString(), in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, QuizChild val) {
            out.write(val.parentQuizId);
            out.write(val.optionId);
            out.write(val.childQuizId);
        }
    };
}
