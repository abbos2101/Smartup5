package uz.greenwhite.smartup5_trade.m_report.ui;

import android.graphics.drawable.Drawable;

import uz.greenwhite.smartup5_trade.datasource.DS;

public class Report {

    public final int id;
    public final Drawable icon;
    public final CharSequence title;
    public final Object tag;

    public Report(int id, int resId, CharSequence title, Object tag) {
        this.id = id;
        this.icon = DS.getDrawable(resId);
        this.title = title;
        this.tag = tag;
    }

    public Report(int id, Drawable icon, CharSequence title, Object tag) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.tag = tag;
    }
}
