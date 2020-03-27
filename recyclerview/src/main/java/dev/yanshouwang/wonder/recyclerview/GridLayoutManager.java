package dev.yanshouwang.wonder.recyclerview;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GridLayoutManager extends RecyclerView.LayoutManager {
    //region 常量
    private static final String TAG = GridLayoutManager.class.getSimpleName();
    private static final boolean DEBUG = true;
    //endregion

    //region 字段
    private final int mRows;
    private final int mColumns;
    @RecyclerView.Orientation
    private final int mOrientation;

    private int mX;
    private int mY;
    //endregion

    //region 构造
    public GridLayoutManager(int rows, int columns) {
        this(rows, columns, RecyclerView.HORIZONTAL);
    }

    public GridLayoutManager(int rows, int columns, @RecyclerView.Orientation int orientation) {
        mRows = rows;
        mColumns = columns;
        mOrientation = orientation;
    }
    //endregion

    //region 方法
    private int restrictDistance(RecyclerView.State state, int dx) {
        int distance = dx;
        final int leftMaximum = getPaddingLeft();
        final int leftWanted = mX - distance;
        if (leftWanted > leftMaximum) {
            distance = mX - leftMaximum;
        } else {
            final int itemCount = state.getItemCount();
            final int pageSize = mColumns * mRows;
            final int pageCount = itemCount / pageSize + (itemCount % pageSize > 0 ? 1 : 0);
            final int pageWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            final int totalWidth = pageCount * pageWidth;
            final int rightMinimum = getWidth() - getPaddingRight();
            final int rightWanted = mX - distance + totalWidth;
            if (rightWanted < rightMinimum) {
                distance = mX + totalWidth - rightMinimum;
            }
        }
        return distance;
    }

    @NonNull
    private ScrolledParams resolveColumns(RecyclerView.State state, int distance) {
        final List<Integer> recycleHeaders = new ArrayList<>();
        final Map<Integer, Integer> fillHeaders = new HashMap<>();

        final int pageWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int itemWidth = pageWidth / mColumns;
        final int itemCount = state.getItemCount();
        final int size = mColumns * mRows;
        final int left = getPaddingLeft() - itemWidth;
        final int right = getWidth() - getPaddingRight();
        int x0 = mX;
        for (int i = 0; i < itemCount; i++) {
            if (i % size >= mColumns) {
                continue;
            }
            if (x0 > left && x0 <= right) {
                recycleHeaders.add(i);
            }
            final int x1 = x0 - distance;
            if (x1 > left && x1 <= right) {
                fillHeaders.put(i, x0);
            }
            if (x0 > right && x1 > right) {
                break;
            }
            x0 += itemWidth;
        }

        if (recycleHeaders.size() > 0 && fillHeaders.size() > 0) {
            final int minimum0 = Collections.min(recycleHeaders);
            final int maximum0 = Collections.max(recycleHeaders);
            final Set<Integer> fillKeys = fillHeaders.keySet();
            final int minimum1 = Collections.min(fillKeys);
            final int maximum1 = Collections.max(fillKeys);
            for (int i = recycleHeaders.size() - 1; i >= 0; i--) {
                final int header = recycleHeaders.get(i);
                if (header >= minimum1 && header <= maximum1) {
                    recycleHeaders.remove(i);
                }
            }
            final Integer[] keysCopy = fillKeys.toArray(new Integer[0]);
            for (Integer header : keysCopy) {
                if (header >= minimum0 && header <= maximum0) {
                    fillHeaders.remove(header);
                }
            }
        }

        return new ScrolledParams(recycleHeaders, fillHeaders);
    }

    private void removeAndRecycleColumns(RecyclerView.Recycler recycler, RecyclerView.State state, List<Integer> headers) {
        final int maxPosition = state.getItemCount() - 1;
        for (Integer header : headers) {
            for (int i = 0; i < mRows; i++) {
                final int targetPosition = header + i * mColumns;
                if (targetPosition > maxPosition) {
                    break;
                }
                final int childCount = getChildCount();
                for (int j = childCount - 1; j >= 0; j--) {
                    final View child = getChildAt(j);
                    assert child != null;
                    final int position = getPosition(child);
                    if (position == targetPosition) {
                        removeAndRecycleView(child, recycler);
                        break;
                    }
                    if (j == 0 && DEBUG) {
                        Log.d(TAG, "没有找到需要回收的视图：" + targetPosition);
                    }
                }
            }
            if (DEBUG) {
                Log.d(TAG, "回收第 " + header + " 列完成");
            }
        }
    }

    private void fillColumns(RecyclerView.Recycler recycler, RecyclerView.State state, Map<Integer, Integer> headers) {
        final int pageWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int itemWidth = pageWidth / mColumns;
        final int pageHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int itemHeight = pageHeight / mRows;
        final int widthUsed = pageWidth - itemWidth;
        final int heightUsed = pageHeight - itemHeight;
        final int maxPosition = state.getItemCount() - 1;

        for (Map.Entry<Integer, Integer> entry : headers.entrySet()) {
            final int header = entry.getKey();
            final int left = entry.getValue();
            int top = getPaddingTop();
            final int right = left + itemWidth;
            for (int i = 0; i < mRows; i++) {
                final int position = header + i * mColumns;
                if (position > maxPosition) {
                    break;
                }
                View child = recycler.getViewForPosition(position);
                addView(child);
                measureChildWithMargins(child, widthUsed, heightUsed);
                final int bottom = top + itemHeight;
                layoutDecoratedWithMargins(child, left, top, right, bottom);
                top = bottom;
            }
            if (DEBUG) {
                Log.d(TAG, "添加第 " + header + " 列完成");
            }
        }
    }
    //endregion

    //region 重写
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int itemCount = state.getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        detachAndScrapAttachedViews(recycler);

        final Map<Integer, Integer> headers = new HashMap<>();
        final int pageWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int itemWidth = pageWidth / mColumns;
        final int pageSize = mColumns * mRows;
        final int left = getPaddingLeft() - itemWidth;
        final int right = getWidth() - getPaddingRight();
        int x = mX;
        for (int i = 0; i < itemCount; i++) {
            if (i % pageSize >= mColumns) {
                continue;
            }
            if (x > left && x <= right) {
                headers.put(i, x);
            }
            if (x > right) {
                break;
            }
            x += itemWidth;
        }

        fillColumns(recycler, state, headers);
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == RecyclerView.HORIZONTAL;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int distance = restrictDistance(state, dx);
        final ScrolledParams params = resolveColumns(state, distance);
        // 回收
        removeAndRecycleColumns(recycler, state, params.recycleHeaders);
        // 填充
        fillColumns(recycler, state, params.fillHeaders);
        // 移动
        offsetChildrenHorizontal(-distance);
        mX -= distance;
        return distance;
    }

    @Override
    public int computeHorizontalScrollExtent(@NonNull RecyclerView.State state) {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public int computeHorizontalScrollOffset(@NonNull RecyclerView.State state) {
        return getPaddingLeft() - mX;
    }

    @Override
    public int computeHorizontalScrollRange(@NonNull RecyclerView.State state) {
        final int itemCount = getItemCount();
        final int pageSize = mColumns * mRows;
        final int pageCount = itemCount / pageSize + (itemCount % pageSize > 0 ? 1 : 0);
        final int pageWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        return pageCount * pageWidth;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == RecyclerView.VERTICAL;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return 0;
    }

    @Override
    public int computeVerticalScrollExtent(@NonNull RecyclerView.State state) {
        return super.computeVerticalScrollExtent(state);
    }

    @Override
    public int computeVerticalScrollOffset(@NonNull RecyclerView.State state) {
        return super.computeVerticalScrollOffset(state);
    }

    @Override
    public int computeVerticalScrollRange(@NonNull RecyclerView.State state) {
        return super.computeVerticalScrollRange(state);
    }
    //endregion

    //region 类
    private static class ScrolledParams {
        @NonNull
        final List<Integer> recycleHeaders;
        @NonNull
        final Map<Integer, Integer> fillHeaders;

        ScrolledParams(@NonNull List<Integer> recycleHeaders, @NonNull Map<Integer, Integer> fillHeaders) {
            this.recycleHeaders = recycleHeaders;
            this.fillHeaders = fillHeaders;
        }
    }
    //endregion
}
