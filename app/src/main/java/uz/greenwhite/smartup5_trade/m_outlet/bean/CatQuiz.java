package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CatQuiz {

    public final String quizId;
    public final String name;
    public final String orderNo;
    public final MyArray<String> outletTypeIds;
    public final MyArray<CatOption> catOptions;

    public CatQuiz(String quizId, String name, String orderNo, MyArray<String> outletTypeIds, MyArray<CatOption> catOptions) {
        this.quizId = quizId;
        this.name = name;
        this.orderNo = orderNo;
        this.outletTypeIds = outletTypeIds;
        this.catOptions = catOptions;
        if (name == null || outletTypeIds == null || catOptions == null) {
            throw AppError.NullPointer();
        }
    }

    public static final MyMapper<CatQuiz, String> KEY_ADAPTER = new MyMapper<CatQuiz, String>() {
        @Override
        public String apply(CatQuiz c) {
            return c.quizId;
        }
    };

    public static final UzumAdapter<CatQuiz> UZUM_ADAPTER = new UzumAdapter<CatQuiz>() {

        @Override
        public CatQuiz read(UzumReader in) {
            return new CatQuiz(in.readString(), in.readString(), in.readString(),
                    in.readValue(STRING_ARRAY), in.readArray(CatOption.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, CatQuiz val) {
            out.write(val.quizId);
            out.write(val.name);
            out.write(val.orderNo);
            out.write(val.outletTypeIds, STRING_ARRAY);
            out.write(val.catOptions, CatOption.UZUM_ADAPTER);
        }
    };
}
