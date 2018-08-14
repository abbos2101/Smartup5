package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.Tuple2;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class DealQuiz {

    public final String quizSetId;
    public final String quizId;
    public final String value;
    public final String rootQuizId;

    public DealQuiz(String quizSetId, String quizId, String value, String rootQuizId) {
        this.quizSetId = quizSetId;
        this.quizId = quizId;
        this.value = value;
        this.rootQuizId = Util.nvl(rootQuizId);
    }

    public static Tuple2 getKey(String quizSetId, String quizId) {
        return new Tuple2(quizSetId, quizId);
    }

    public static final MyMapper<DealQuiz, Tuple2> KEY_ADAPTER = new MyMapper<DealQuiz, Tuple2>() {
        @Override
        public Tuple2 apply(DealQuiz val) {
            return DealQuiz.getKey(val.quizSetId, val.quizId);
        }

    };

    public static final UzumAdapter<DealQuiz> UZUM_ADAPTER = new UzumAdapter<DealQuiz>() {
        @Override
        public DealQuiz read(UzumReader in) {
            return new DealQuiz(in.readString(),
                    in.readString(), in.readString(),
                    in.readString());
        }

        @Override
        public void write(UzumWriter out, DealQuiz val) {
            out.write(val.quizSetId);
            out.write(val.quizId);
            out.write(val.value);
            out.write(val.rootQuizId);
        }
    };

}
