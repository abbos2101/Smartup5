package uz.greenwhite.smartup5_trade.m_session.bean.quiz;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class QuizSet {

    public final String quizSetId;
    public final String name;
    public final MyArray<String> quizIds;
    public final String orderNo;

    public QuizSet(String quizSetId, String name, MyArray<String> quizIds, String orderNo) {
        this.quizSetId = quizSetId;
        this.name = name;
        this.quizIds = MyArray.nvl(quizIds);
        this.orderNo = Util.nvl(orderNo, "9999");
    }

    public static final MyMapper<QuizSet, String> KEY_ADAPTER = new MyMapper<QuizSet, String>() {
        @Override
        public String apply(QuizSet val) {
            return val.quizSetId;
        }
    };

    public static final UzumAdapter<QuizSet> UZUM_ADAPTER = new UzumAdapter<QuizSet>() {

        @Override
        public QuizSet read(UzumReader in) {

            return new QuizSet(in.readString(), in.readString(),
                    in.readValue(STRING_ARRAY), in.readString());
        }

        @Override
        public void write(UzumWriter out, QuizSet val) {
            out.write(val.quizSetId);
            out.write(val.name);
            out.write(val.quizIds, STRING_ARRAY);
            out.write(val.orderNo);

        }
    };
}
