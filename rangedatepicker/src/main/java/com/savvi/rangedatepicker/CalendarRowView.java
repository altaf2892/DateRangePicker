// Copyright 2012 Square, Inc.
package com.savvi.rangedatepicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * TableRow that draws a divider between each cell. To be used with {@link CalendarGridView}.
 */
public class CalendarRowView extends ViewGroup implements View.OnClickListener {
    private boolean isHeaderRow;
    private MonthView.Listener listener;

    public int outer_widthl = 9;
    public int outer_widthr = 9;
    public int inner_widthl = 9;
    public int inner_widthr = 9;

    public boolean isHeaderRow() {
        return isHeaderRow;
    }

    public void setHeaderRow(boolean headerRow) {
        isHeaderRow = headerRow;
    }

    public MonthView.Listener getListener() {
        return listener;
    }

    public int getOuter_widthl() {
        return outer_widthl;
    }

    public void setOuter_widthl(int outer_widthl) {
        this.outer_widthl = outer_widthl;
    }

    public int getOuter_widthr() {
        return outer_widthr;
    }

    public void setOuter_widthr(int outer_widthr) {
        this.outer_widthr = outer_widthr;
    }

    public int getInner_widthl() {
        return inner_widthl;
    }

    public void setInner_widthl(int inner_widthl) {
        this.inner_widthl = inner_widthl;
    }

    public int getInner_widthr() {
        return inner_widthr;
    }

    public void setInner_widthr(int inner_widthr) {
        this.inner_widthr = inner_widthr;
    }

    public CalendarRowView(Context context, AttributeSet attrs) {

        super(context, attrs);
      /*  TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarPickerView);
        Resources res = context.getResources();
        outer_widthl = a.getResourceId(R.styleable.CalendarPickerView_tsquare_outer_l,
                res.getDimension(R.dimen.));
        outer_widthr = a.getResourceId(R.styleable.CalendarPickerView_tsquare_outer_r,
                res.getColor(R.color.calendar_bg));
        inner_widthl = a.getResourceId(R.styleable.CalendarPickerView_tsquare_inner_l,
                res.getColor(R.color.calendar_bg));
        inner_widthr = a.getResourceId(R.styleable.CalendarPickerView_tsquare_inner_r,
                res.getColor(R.color.calendar_bg));*/
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        child.setOnClickListener(this);

        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        long start = System.currentTimeMillis();
        final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int rowHeight = 0;
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            final View child = getChildAt(c);
            // Calculate width cells, making sure to cover totalWidth.
            int l = ((c + 0) * totalWidth) / outer_widthl;
            int r = ((c + 1) * totalWidth) / outer_widthr;
            int cellSize = r - l;
            int cellWidthSpec = makeMeasureSpec(cellSize, EXACTLY);
            int cellHeightSpec = isHeaderRow ? makeMeasureSpec(cellSize, AT_MOST) : cellWidthSpec;
            child.measure(cellWidthSpec, cellHeightSpec);
            // The row height is the height of the tallest cell.
            if (child.getMeasuredHeight() > rowHeight) {
                rowHeight = child.getMeasuredHeight();
            }
        }
        final int widthWithPadding = totalWidth + getPaddingLeft() + getPaddingRight();
        final int heightWithPadding = rowHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(widthWithPadding, heightWithPadding);
        Logr.d("Row.onMeasure %d ms", System.currentTimeMillis() - start);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        long start = System.currentTimeMillis();
        int cellHeight = bottom - top;
        int width = (right - left);
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            final View child = getChildAt(c);
            int l = ((c + 0) * width) / inner_widthl;
            int r = ((c + 1) * width) / inner_widthr;
            child.layout(l, 0, r, cellHeight);
        }
        Logr.d("Row.onLayout %d ms", System.currentTimeMillis() - start);
    }

    public void setIsHeaderRow(boolean isHeaderRow) {
        this.isHeaderRow = isHeaderRow;
    }

    @Override
    public void onClick(View v) {
        // Header rows don't have a click listener
        if (listener != null) {
            listener.handleClick((MonthCellDescriptor) v.getTag());
        }
    }

    public void setListener(MonthView.Listener listener) {
        this.listener = listener;
    }

    public void setDayViewAdapter(DayViewAdapter adapter) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                CalendarCellView cell = ((CalendarCellView) getChildAt(i));
                cell.removeAllViews();
                adapter.makeCellView(cell);
            }
        }
    }

    public void setCellBackground(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setBackgroundResource(resId);
        }
    }

    public void setCellTextColor(int resId) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                ((CalendarCellView) getChildAt(i)).getDayOfMonthTextView().setTextColor(resId);
            } else {
                ((TextView) getChildAt(i)).setTextColor(resId);
            }
        }
    }

    public void setCellTextColor(ColorStateList colors) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                ((CalendarCellView) getChildAt(i)).getDayOfMonthTextView().setTextColor(colors);
            } else {
                ((TextView) getChildAt(i)).setTextColor(colors);
            }
        }
    }

    public void setTypeface(Typeface typeface) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CalendarCellView) {
                ((CalendarCellView) getChildAt(i)).getDayOfMonthTextView().setTypeface(typeface);
            } else {
                ((TextView) getChildAt(i)).setTypeface(typeface);
            }
        }
    }
}
