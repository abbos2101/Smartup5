package uz.greenwhite.smartup5_trade.common.module;// 30.06.2016

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.VariableLike;

public abstract class VModule extends VariableLike {

    public static final Object DEFAULT_TAG = new Object();

    public final Object tag;

    protected VModule(Object tag) {
        this.tag = tag;
    }

    protected VModule() {
        this(DEFAULT_TAG);
    }

    public abstract int getModuleId();

    public abstract int getIconResId();

    public abstract CharSequence getTitle();

    public abstract MyArray<VForm> getModuleForms();

    public abstract boolean hasValue();

    public boolean isMandatory() {
        return false;
    }

    public String getNotifyCount() {
        return "";
    }

    public static final MyMapper<VModule, Integer> KEY_ADAPTER = new MyMapper<VModule, Integer>() {
        @Override
        public Integer apply(VModule val) {
            return val.getModuleId();
        }
    };

}
