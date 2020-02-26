package dev.yanshouwang.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class BaselineLayout extends ViewGroup {

    private int mBaseline = -1;

    public BaselineLayout(Context context) {
        super(context);
    }

    public BaselineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaselineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaselineLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int count = this.getChildCount();
        int maxWidth = 0;
        int maxHeight = 0;
        int maxChildBaseline = -1;
        int maxChildDescent = -1;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            final int visibility = child.getVisibility();
            if (visibility == View.GONE) {
                continue;
            }

            this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
            final int measuredWidth = child.getMeasuredWidth();
            final int measuredHeight = child.getMeasuredHeight();
            final int measuredState = child.getMeasuredState();
            final int baseline = child.getBaseline();

            maxWidth = Math.max(maxWidth, measuredWidth);
            maxHeight = Math.max(maxHeight, measuredHeight);
            if (baseline != -1) {
                maxChildBaseline = Math.max(maxChildBaseline, baseline);
                maxChildDescent = Math.max(maxChildDescent, measuredHeight - baseline);
            }
            childState = View.combineMeasuredStates(childState, measuredState);
        }
        if (maxChildBaseline != -1) {
            maxChildDescent = Math.max(maxChildDescent, this.getPaddingBottom());
            maxHeight = Math.max(maxHeight, maxChildBaseline + maxChildDescent);
            this.mBaseline = maxChildBaseline;
        }
        maxWidth = Math.max(maxWidth, this.getSuggestedMinimumWidth());
        maxHeight = Math.max(maxHeight, this.getSuggestedMinimumHeight());

        this.setMeasuredDimension(
                View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState << View.MEASURED_HEIGHT_STATE_SHIFT)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = this.getChildCount();
        final int parentLeft = this.getPaddingLeft();
        final int parentTop = this.getPaddingTop();
        final int parentRight = r - l - this.getPaddingRight();
        final int parentContentWidth = parentRight - parentLeft;

        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            final int visibility = child.getVisibility();
            if (visibility == View.GONE) {
                continue;
            }
            final int childMeasuredWidth = child.getMeasuredWidth();
            final int childMeasuredHeight = child.getMeasuredHeight();

            final int childLeft = parentLeft + (parentContentWidth - childMeasuredWidth) / 2;
            final int childTop;
            final int childBaseline = child.getBaseline();
            if (this.mBaseline != -1 && childBaseline != -1) {
                childTop = parentTop + this.mBaseline - childBaseline;
            } else {
                childTop = parentTop;
            }

            child.layout(childLeft, childTop, childLeft + childMeasuredWidth, childTop + childMeasuredHeight);
        }
    }

    @Override
    public int getBaseline() {
        return this.mBaseline;
    }
}
