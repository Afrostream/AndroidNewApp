package tv.afrostream.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ScrollThroughRecyclerView extends RecyclerView {

    private boolean isOnClick;

    public ScrollThroughRecyclerView(Context context) {
        super(context);
    }

    public ScrollThroughRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollThroughRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        final int action = e.getAction();
        final int actionMasked = action & MotionEvent.ACTION_MASK;
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            return false; //intercepted by the viewgroup and passed down to child
        } else if (actionMasked == MotionEvent.ACTION_UP) {
            return false; //intercepted by the viewgroup and passed down to child
        }
        return super.onInterceptTouchEvent(e);

        //return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int actionMasked = action & MotionEvent.ACTION_MASK;
        Log.e("actionmasked", "" + actionMasked);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                return super.dispatchTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_CANCEL:
                return true;
            case MotionEvent.ACTION_UP:
                return super.dispatchTouchEvent(ev);
            default:
                return super.dispatchTouchEvent(ev);
        }


    }
}