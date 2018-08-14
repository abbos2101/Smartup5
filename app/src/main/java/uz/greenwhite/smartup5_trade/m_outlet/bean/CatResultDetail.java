package uz.greenwhite.smartup5_trade.m_outlet.bean;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class CatResultDetail {

    public final String quizId;
    public final String optionId;

    public CatResultDetail(String quizId, String optionId) {
        this.quizId = quizId;
        this.optionId = optionId;
    }

    public static final MyMapper<CatResultDetail, String> KEY_ADAPTER = new MyMapper<CatResultDetail, String>() {
        @Override
        public String apply(CatResultDetail oc) {
            return oc.quizId;
        }
    };

    public static final UzumAdapter<CatResultDetail> UZUM_ADAPTER = new UzumAdapter<CatResultDetail>() {

        @Override
        public CatResultDetail read(UzumReader in) {
            return new CatResultDetail(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, CatResultDetail val) {
            out.write(val.quizId);
            out.write(val.optionId);
        }
    };
}
