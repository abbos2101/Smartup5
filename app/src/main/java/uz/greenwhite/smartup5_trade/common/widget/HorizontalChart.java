package uz.greenwhite.smartup5_trade.common.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import uz.greenwhite.lib.collection.MyArray;

public class HorizontalChart extends View {

    public HorizontalChart(Context context) {
        super(context);
        init(context);
    }

    public HorizontalChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizontalChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HorizontalChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Paint paint;
    private MyArray<ChartLine> lines;

    private void init(Context context) {
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startX = 0F;
        float startY = 0F;
        float endX = 0F;
        float endY = getHeight();

        float size = getWidth();
        float onePercent = size / 100F;

        for (ChartLine line : lines) {
            paint.setColor(line.color);

            float result = line.percent * onePercent;
            endX = endX + result;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float rX = 50f;
                float rY = 50f;
                canvas.drawRoundRect(startX, startY, endX, endY, rX, rY, paint);
            } else {
                canvas.drawRect(startX, startY, endX, endY, paint);
            }

            startX = endX;
        }
    }

    public void setLines(MyArray<ChartLine> lines) {
        this.lines = MyArray.nvl(lines);
    }
}
