package uz.greenwhite.smartup5_trade.m_near.util;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;


public class SlideLayout extends RelativeLayout {

    private int expandHeight = 500;
    private int collapseHeight = 20;
    private int duration = 300;
    private Activity activity;
    private boolean expanded = true;
    private InterceptListener<Void> intercept;

    public SlideLayout(Context context) {
        super(context);
        activity = (Activity) context;
        init();
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity) context;
        init();
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        activity = (Activity) context;
        init();
    }

    private void init() {
        int display_height = activity.getWindowManager().getDefaultDisplay().getHeight();
        expandHeight = (display_height - ((display_height * 25) / 100)) - NearMapUtil.dpToPixel(50, activity);
    }

    public void setCollapseHeight(int collapseHeight) {
        this.collapseHeight = collapseHeight;
    }

    public void setExpanded() {
        this.getLayoutParams().height = expandHeight;
        this.requestLayout();
        expanded = true;
    }

    public void setCollapsed() {
        this.getLayoutParams().height = collapseHeight;
        this.requestLayout();
        expanded = false;
    }

    public void expand() {
        final float mToHeight = expandHeight;
        final float mFromHeight = SlideLayout.this.getLayoutParams().height;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
                SlideLayout.this.getLayoutParams().height = (int) height;
                SlideLayout.this.requestLayout();
                if ((int) height == expandHeight) {
                    expanded = true;
                    intercept.intercept(1, null);
                }
            }
        };
        a.setDuration(duration);
        this.startAnimation(a);
    }

    public void collapse() {
        final float mToHeight = collapseHeight;
        final float mFromHeight = SlideLayout.this.getLayoutParams().height;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
                SlideLayout.this.getLayoutParams().height = (int) height;
                SlideLayout.this.requestLayout();
                if ((int) height == collapseHeight) {
                    expanded = false;
                    intercept.intercept(-1, null);
                }
            }
        };
        a.setDuration(duration);
        this.startAnimation(a);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setOnInterceptListener(InterceptListener<Void> intercept) {
        this.intercept = intercept;
    }

}
