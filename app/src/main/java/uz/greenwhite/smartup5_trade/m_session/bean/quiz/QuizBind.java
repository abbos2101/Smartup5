package uz.greenwhite.smartup5_trade.m_session.bean.quiz;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class QuizBind {

    public final String quizId;
    public final MyArray<QuizChild> childs;


    public QuizBind(String quizId, MyArray<QuizChild> childs) {
        this.quizId = quizId;
        this.childs = childs;
    }

    public static Tuple2 getKey(String quizId, String optionId) {
        return new Tuple2(quizId, optionId);
    }

    public static final MyMapper<QuizBind, String> KEY_ADAPTER = new MyMapper<QuizBind, String>() {
        @Override
        public String apply(QuizBind val) {
            return val.quizId;
        }
    };

    public static final UzumAdapter<QuizBind> UZUM_ADAPTER = new UzumAdapter<QuizBind>() {
        @Override
        public QuizBind read(UzumReader in) {
            return new QuizBind(in.readString(), in.readArray(QuizChild.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, QuizBind val) {
            out.write(val.quizId);
            out.write(val.childs, QuizChild.UZUM_ADAPTER);
        }
    };
}
