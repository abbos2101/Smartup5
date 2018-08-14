package uz.greenwhite.smartup5_trade.common.module;// 30.06.2016


import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.VariableLike;

public abstract class VForm extends VariableLike {

    public final String code;
    public final Object tag;
    public boolean enable;

    public VForm(String code, Object tag) {
        this.code = code;
        this.tag = tag;
        this.enable = true;
    }

    public VForm(String code) {
        this(code, VModule.DEFAULT_TAG);
    }

    public CharSequence getTitle() {
        return "";
    }

    public CharSequence getDetail() {
        return "";
    }

    public abstract boolean hasValue();

    public static final MyMapper<VForm, String> KEY_ADAPTER = new MyMapper<VForm, String>() {
        @Override
        public String apply(VForm val) {
            return val.code;
        }
    };
}
