package uz.greenwhite.smartup5_trade.m_near.util;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListView extends ListView {

    private Runnable scrollerTask;
    private int initialPosition;

    private int newCheck = 200;
    private OnScrollStoppedListener onScrollStoppedListener;

    private boolean disableScroll = false;

    public CustomListView(Context context) {
        super(context);
        init();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        scrollerTask = new Runnable() {
            public void run() {
                int newPosition = getScrollY();
                if (initialPosition - newPosition == 0) {//has stopped
                    if (onScrollStoppedListener != null) {
                        onScrollStoppedListener.onScrollStopped();
                    }
                } else {
                    initialPosition = getScrollY();
                    postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public void setDisableScroll(boolean disableScroll) {
        this.disableScroll = disableScroll;
    }

    @Override
    public final boolean dispatchTouchEvent(final MotionEvent ev) {
        if (disableScroll) {
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                ev.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    public void setOnScrollStoppedListener(OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask() {
        initialPosition = getScrollY();
        postDelayed(scrollerTask, newCheck);
    }

    public interface OnScrollStoppedListener {
        void onScrollStopped();
    }
}