package uz.greenwhite.smartup5_trade.m_deal.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.m_session.bean.VisitModule;

public class DealQuizModule extends DealModule {

    public final MyArray<DealQuiz> quizzes;

    public DealQuizModule(MyArray<DealQuiz> quizzes) {
        super(VisitModule.M_QUIZ);
        this.quizzes = quizzes;
        this.quizzes.checkUniqueness(DealQuiz.KEY_ADAPTER);
    }

    public static final UzumAdapter<DealModule> UZUM_ADAPTER = new UzumAdapter<DealModule>() {
        @Override
        public DealModule read(UzumReader in) {
            return new DealQuizModule(in.readArray(DealQuiz.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealModule val) {
            out.write(((DealQuizModule)val).quizzes, DealQuiz.UZUM_ADAPTER);
        }
    };
}
