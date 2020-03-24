package dev.yanshouwang.core.view;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {
    private final int mRows;
    private final int mColumns;

    private int mOffset;

    public GridLayoutManager(int rows, int columns) {
        mRows = rows;
        mColumns = columns;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }

        detachAndScrapAttachedViews(recycler);
        int itemCount = state.getItemCount();
        int totalWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int totalHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int itemWidth = totalWidth / mColumns;
        int itemHeight = totalHeight / mRows;
        int widthUsed = (mColumns - 1) * itemWidth;
        int heightUsed = (mRows - 1) * itemHeight;
        int size = computePageSize(state);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < mRows; j++) {
                int top = itemHeight * j;
                for (int k = 0; k < mColumns; k++) {
                    int left = totalWidth * i + itemWidth * k;
                    int position = mRows * mColumns * i + mColumns * j + k;
                    if (position > itemCount - 1) {
                        break;
                    }
                    View view = recycler.getViewForPosition(position);
                    addView(view);
                    measureChildWithMargins(view, widthUsed, heightUsed);
                    layoutDecorated(view, left, top, left + itemWidth, top + itemHeight);
                }
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int travel = dx;
        int size = computePageSize(state);
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int totalWidth = (size - 1) * width;
        if (mOffset + dx > totalWidth) {
            travel = totalWidth - mOffset;
        } else if (mOffset + dx < 0) {
            travel = -mOffset;
        }
        mOffset += travel;
        offsetChildrenHorizontal(-travel);
        return travel;
    }

    @Override
    public int computeHorizontalScrollRange(@NonNull RecyclerView.State state) {
        int size = computePageSize(state);
        int width = getWidth();
        return size * width;
    }

    @Override
    public int computeHorizontalScrollExtent(@NonNull RecyclerView.State state) {
        return getWidth();
    }

    @Override
    public int computeHorizontalScrollOffset(@NonNull RecyclerView.State state) {
        return mOffset;
    }

    private int computePageSize(RecyclerView.State state) {
        int itemCount = state.getItemCount();
        int size = mRows * mColumns;
        return itemCount / size + (itemCount % size > 0 ? 1 : 0);
    }

    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        PointF vector = new PointF();
        int[] offset = getSnapOffset(targetPosition);
        vector.x = offset[0];
        vector.y = offset[1];
        return vector;
    }

    private int[] getSnapOffset(int targetPosition) {
        int[] offset = new int[2];
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int number = targetPosition / mRows / mColumns;
        if (canScrollHorizontally()) {
            offset[0] = width * number;
        }
        return offset;
    }

    public int getCurrentPosition() {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int number = width <= 0 ? 0 : mOffset / width;
        return number * mRows * mColumns;
    }

    public int getTargetPosition() {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int number = width <= 0 ? 0 : Math.round((float) mOffset / width);
        return number * mRows * mColumns;
    }
}
