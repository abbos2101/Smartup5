package uz.greenwhite.smartup5_trade.common.widget;

import uz.greenwhite.lib.util.Util;

public class ChartLine {

    public final float percent;
    public final String text;
    public final int color;

    public ChartLine(float percent, String text, int color) {
        this.percent = percent;
        this.text = Util.nvl(text);
        this.color = color;
    }
}
