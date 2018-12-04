package com.savvi.rangepickersample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.savvi.rangedatepicker.CalendarPickerView;

public class ScrollDisabledListView extends CalendarPickerView {

    private int touchItemPosition;
    private boolean scrollable = true;



    public ScrollDisabledListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setScrollingEnabled(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isScrollable()) {
            final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

            if (actionMasked == MotionEvent.ACTION_DOWN) {
                // record the position the list the touch landed on
                this.touchItemPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                return super.dispatchTouchEvent(ev);
            }

            if (actionMasked == MotionEvent.ACTION_MOVE) {
                if (pointToPosition((int) ev.getX(), (int) ev.getY()) != this.touchItemPosition) {
                    setPressed(false); // clear pressed state if item position was changed
                    this.touchItemPosition = -2; // -1 not working because pointToPosition() returns it when item not found
                }
                return true;
            }

            if (actionMasked == MotionEvent.ACTION_UP) {
                // check if we are still within the same view
                if (pointToPosition((int) ev.getX(), (int) ev.getY()) == this.touchItemPosition) {
                    super.dispatchTouchEvent(ev);
                } else
                    return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}