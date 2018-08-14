package uz.greenwhite.smartup5_trade.m_session.bean.quiz;


import android.text.TextUtils;

import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;


public class QuizOption {

    public final String name;
    public final String code;
    public final String optionId;

    public QuizOption(String name, String code, String optionId) {
        this.name = name;
        this.code = TextUtils.isEmpty(code) ? name : code;
        this.optionId = Util.nvl(optionId);
    }

    public static final UzumAdapter<QuizOption> UZUM_ADAPTER = new UzumAdapter<QuizOption>() {
        @Override
        public QuizOption read(UzumReader in) {
            return new QuizOption(in.readString(),
                    in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, QuizOption val) {
            out.write(val.name);
            out.write(val.code);
            out.write(val.optionId);
        }
    };
}
