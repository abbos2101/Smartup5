package uz.greenwhite.smartup5_trade.m_duty;

import android.graphics.drawable.Drawable;

import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class Duty {

    public final int id;
    public final Drawable icon;
    public final CharSequence title;

    public Duty(int id, Drawable icon, CharSequence title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
    }

    public Duty(int id, int resId, CharSequence title) {
        this.id = id;
        this.icon = DS.getDrawable(resId);
        this.title = title;
    }

    public static final MyMapper<Duty, Integer> KEY_ADAPTER = new MyMapper<Duty, Integer>() {
        @Override
        public Integer apply(Duty duty) {
            return duty.id;
        }
    };
}
