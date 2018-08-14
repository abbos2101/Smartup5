package uz.greenwhite.smartup5_trade.m_session.bean.quiz;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class QuizRole {

    public final String roleId;
    public final MyArray<String> quizSetIds;

    public QuizRole(String roleId, MyArray<String> quizSetIds) {
        this.roleId = roleId;
        this.quizSetIds = quizSetIds;
    }

    public static final MyMapper<QuizRole, String> KEY_ADAPTER = new MyMapper<QuizRole, String>() {
        @Override
        public String apply(QuizRole quizRole) {
            return quizRole.roleId;
        }
    };

    public static final UzumAdapter<QuizRole> UZUM_ADAPTER = new UzumAdapter<QuizRole>() {
        @Override
        public QuizRole read(UzumReader in) {
            return new QuizRole(in.readString(), in.readValue(STRING_ARRAY));
        }

        @Override
        public void write(UzumWriter out, QuizRole val) {
            out.write(val.roleId);
            out.write(val.quizSetIds, STRING_ARRAY);
        }
    };
}
