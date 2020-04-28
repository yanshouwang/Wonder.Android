package dev.yanshouwang.wonder.recyclerview;

import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiGridLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {
    //region 常量
    private static final String TAG = MultiGridLayoutManager.class.getSimpleName();
    private static final boolean DEBUG = false;
    //endregion

    //region 字段
    private final int _rowCount;
    private final int _columnCount;
    @RecyclerView.Orientation
    private final int _orientation;

    private int _x;
    private int _y;
    //endregion

    //region 构造
    public MultiGridLayoutManager(int rowCount, int columnCount) {
        this(rowCount, columnCount, RecyclerView.HORIZONTAL);
    }

    public MultiGridLayoutManager(int rowCount, int columnCount, @RecyclerView.Orientation int orientation) {
        _rowCount = rowCount;
        _columnCount = columnCount;
        _orientation = orientation;
    }
    //endregion

    //region 方法
    int getRowCount() {
        return _rowCount;
    }

    int getColumnCount() {
        return _columnCount;
    }

    int getX() {
        return _x;
    }

    int getY() {
        return _y;
    }

    @NonNull
    private Map<Integer, Integer> resolveHorizontally(RecyclerView.State state) {
        final Map<Integer, Integer> headers = new HashMap<>();
        final int itemCount = state.getItemCount();
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int itemWidth = availableWidth / _columnCount;
        final int remainderWidth = availableWidth % itemWidth;
        final int left = getPaddingLeft() - itemWidth;
        final int right = getWidth() - getPaddingRight();
        int x = getPaddingLeft() + remainderWidth / 2 + _x;
        for (int i = 0; i < itemCount; i++) {
            final int rowNumber = i / _columnCount % _rowCount;
            if (rowNumber != 0) {
                continue;
            }
            if (x > left && x < right) {
                headers.put(i, x);
            }
            if (x >= right) {
                break;
            }
            x += itemWidth;
        }
        return headers;
    }

    @NonNull
    private Map<Integer, Integer> resolveVertically(RecyclerView.State state) {
        final Map<Integer, Integer> headers = new HashMap<>();
        final int itemCount = state.getItemCount();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int itemHeight = availableHeight / _rowCount;
        final int remainderHeight = availableHeight % itemHeight;
        final int top = getPaddingTop() - itemHeight;
        final int bottom = getHeight() - getPaddingBottom();
        int y = getPaddingTop() + remainderHeight / 2 + _y;
        for (int i = 0; i < itemCount; i++) {
            final int columnNumber = i % _columnCount;
            if (columnNumber != 0) {
                continue;
            }
            if (y > top && y < bottom) {
                headers.put(i, y);
            }
            if (y >= bottom) {
                break;
            }
            y += itemHeight;
        }
        return headers;
    }

    private int restrictScrollHorizontally(RecyclerView.State state, int dx) {
        int distance = dx;
        final int leftMaximum = 0;
        final int leftCurrent = _x;
        final int leftWanted = leftCurrent - distance;
        if (leftWanted > leftMaximum) {
            distance = leftCurrent - leftMaximum;
        } else {
            final int itemCount = state.getItemCount();
            final int pageSize = _columnCount * _rowCount;
            final int pageCount = itemCount / pageSize + (itemCount % pageSize > 0 ? 1 : 0);
            final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            final int itemWidth = availableWidth / _columnCount;
            final int pageWidth = itemWidth * _columnCount;
            final int totalWidth = pageCount * pageWidth;
            final int rightMinimum = leftMaximum + pageWidth;
            final int rightCurrent = leftCurrent + totalWidth;
            final int rightWanted = rightCurrent - distance;
            if (rightWanted < rightMinimum) {
                distance = rightCurrent - rightMinimum;
            }
        }
        return distance;
    }

    private int restrictScrollVertically(RecyclerView.State state, int dy) {
        int distance = dy;
        final int topMaximum = 0;
        final int topCurrent = _y;
        final int topWanted = topCurrent - distance;
        if (topWanted > topMaximum) {
            distance = topCurrent - topMaximum;
        } else {
            final int itemCount = state.getItemCount();
            final int pageSize = _columnCount * _rowCount;
            final int pageCount = itemCount / pageSize + (itemCount % pageSize > 0 ? 1 : 0);
            final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            final int itemHeight = availableHeight / _rowCount;
            final int pageHeight = itemHeight * _rowCount;
            final int totalHeight = pageCount * pageHeight;
            final int bottomMinimum = topMaximum + pageHeight;
            final int bottomCurrent = topCurrent + totalHeight;
            final int bottomWanted = bottomCurrent - distance;
            if (bottomWanted < bottomMinimum) {
                distance = bottomCurrent - bottomMinimum;
            }
        }
        return distance;
    }

    @NonNull
    private ScrolledParams resolveScrollHorizontally(RecyclerView.State state, int distance) {
        final List<Integer> recycleHeaders = new ArrayList<>();
        final Map<Integer, Integer> fillHeaders = new HashMap<>();

        final int itemCount = state.getItemCount();
        if (itemCount > 0) {
            final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            final int itemWidth = availableWidth / _columnCount;
            final int remainderWidth = availableWidth % itemWidth;
            final int left = getPaddingLeft() - itemWidth;
            final int right = getWidth() - getPaddingRight();
            int x0 = getPaddingLeft() + remainderWidth / 2 + _x;
            for (int i = 0; i < itemCount; i++) {
                final int rowNumber = i / _columnCount % _rowCount;
                if (rowNumber != 0) {
                    continue;
                }
                if (x0 > left && x0 < right) {
                    recycleHeaders.add(i);
                }
                final int x1 = x0 - distance;
                if (x1 > left && x1 < right) {
                    fillHeaders.put(i, x0);
                }
                if (x0 >= right && x1 >= right) {
                    break;
                }
                x0 += itemWidth;
            }
        }
        return distinctHeaders(recycleHeaders, fillHeaders);
    }

    private ScrolledParams resolveScrollVertically(RecyclerView.State state, int distance) {
        final List<Integer> recycleHeaders = new ArrayList<>();
        final Map<Integer, Integer> fillHeaders = new HashMap<>();

        final int itemCount = state.getItemCount();
        if (itemCount > 0) {
            final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            final int itemHeight = availableHeight / _rowCount;
            final int remainderHeight = availableHeight % itemHeight;
            final int top = getPaddingTop() - itemHeight;
            final int bottom = getHeight() - getPaddingBottom();
            int y0 = getPaddingTop() + remainderHeight / 2 + _y;
            for (int i = 0; i < itemCount; i++) {
                if (i % _columnCount != 0) {
                    continue;
                }
                if (y0 > top && y0 < bottom) {
                    recycleHeaders.add(i);
                }
                final int y1 = y0 - distance;
                if (y1 > top && y1 < bottom) {
                    fillHeaders.put(i, y0);
                }
                if (y0 >= bottom && y1 >= bottom) {
                    break;
                }
                y0 += itemHeight;
            }
        }
        return distinctHeaders(recycleHeaders, fillHeaders);
    }

    private ScrolledParams distinctHeaders(List<Integer> recycleHeaders, Map<Integer, Integer> fillHeaders) {
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
        if (DEBUG) {
            final int recycleSize = recycleHeaders.size();
            final Integer[] fillArray = fillHeaders.keySet().toArray(new Integer[0]);
            final int fillSize = fillArray.length;
            if (recycleSize > 0 || fillSize > 0) {
                StringBuilder recycleStrBuilder = new StringBuilder();
                for (int i = 0; i < recycleSize; i++) {
                    final int header = recycleHeaders.get(i);
                    recycleStrBuilder.append(header);
                    if (i != recycleSize - 1) {
                        recycleStrBuilder.append(", ");
                    }
                }
                final String recycleStr = recycleStrBuilder.toString();
                StringBuilder fillStrBuilder = new StringBuilder();
                for (int i = 0; i < fillSize; i++) {
                    final int header = fillArray[i];
                    fillStrBuilder.append(header);
                    if (i != fillSize - 1) {
                        fillStrBuilder.append(", ");
                    }
                }
                final String fillStr = fillStrBuilder.toString();
                Log.d(TAG, "回收：" + recycleStr + "；填充：" + fillStr);
            }
        }
        return new ScrolledParams(recycleHeaders, fillHeaders);
    }

    private void removeAndRecycleViewsHorizontally(RecyclerView.Recycler recycler, RecyclerView.State state, List<Integer> headers) {
        final int maxPosition = state.getItemCount() - 1;
        for (Integer header : headers) {
            for (int i = 0; i < _rowCount; i++) {
                final int targetPosition = header + i * _columnCount;
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
        }
    }

    private void removeAndRecycleViewsVertically(RecyclerView.Recycler recycler, RecyclerView.State state, List<Integer> headers) {
        final int maxPosition = state.getItemCount() - 1;
        for (Integer header : headers) {
            for (int i = 0; i < _columnCount; i++) {
                final int targetPosition = header + i;
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
        }
    }

    private void fillHorizontally(RecyclerView.Recycler recycler, RecyclerView.State state, Map<Integer, Integer> headers) {
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int itemWidth = availableWidth / _columnCount;
        final int itemHeight = availableHeight / _rowCount;
        final int remainderHeight = availableHeight % itemHeight;
        final int widthUsed = availableWidth - itemWidth;
        final int heightUsed = availableHeight - itemHeight;
        final int maxPosition = state.getItemCount() - 1;

        for (Map.Entry<Integer, Integer> entry : headers.entrySet()) {
            final int header = entry.getKey();
            final int left = entry.getValue();
            int top = getPaddingTop() + remainderHeight / 2;
            final int right = left + itemWidth;
            for (int i = 0; i < _rowCount; i++) {
                final int position = header + i * _columnCount;
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
        }

        if (DEBUG) {
            StringBuilder messageBuilder = new StringBuilder();
            final int childCount = getChildCount();
            messageBuilder.append("COUNT: ").append(childCount).append(" ");
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                assert child != null;
                final int position = getPosition(child);
                messageBuilder.append(position).append(" ");
            }
            final String message = messageBuilder.toString();
            Log.d(TAG, message);
        }
    }

    private void fillVertically(RecyclerView.Recycler recycler, RecyclerView.State state, Map<Integer, Integer> headers) {
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int itemWidth = availableWidth / _columnCount;
        final int itemHeight = availableHeight / _rowCount;
        final int remainderWidth = availableWidth % itemWidth;
        final int widthUsed = availableWidth - itemWidth;
        final int heightUsed = availableHeight - itemHeight;
        final int maxPosition = state.getItemCount() - 1;

        for (Map.Entry<Integer, Integer> entry : headers.entrySet()) {
            final int header = entry.getKey();
            int left = getPaddingLeft() + remainderWidth / 2;
            final int top = entry.getValue();
            final int bottom = top + itemHeight;
            for (int i = 0; i < _columnCount; i++) {
                final int position = header + i;
                if (position > maxPosition) {
                    break;
                }
                View child = recycler.getViewForPosition(position);
                addView(child);
                measureChildWithMargins(child, widthUsed, heightUsed);
                final int right = left + itemWidth;
                layoutDecoratedWithMargins(child, left, top, right, bottom);
                left = right;
            }
        }

        if (DEBUG) {
            StringBuilder messageBuilder = new StringBuilder();
            final int childCount = getChildCount();
            messageBuilder.append("COUNT: ").append(childCount).append(" ");
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                assert child != null;
                final int position = getPosition(child);
                messageBuilder.append(position).append(" ");
            }
            final String message = messageBuilder.toString();
            Log.d(TAG, message);
        }
    }
    //endregion

    //region 实现
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        final int targetRowNumber = (targetPosition / _columnCount) % _rowCount;
        final int targetHeader = targetPosition - targetRowNumber * _columnCount;
        int minimum = Integer.MAX_VALUE;
        int maximum = Integer.MIN_VALUE;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            assert child != null;
            final int position = getPosition(child);
            final int rowNumber = position / _columnCount % _rowCount;
            final int columnNumber = position % _columnCount;
            if (rowNumber != 0) {
                continue;
            }
            if (columnNumber == 0 && position < minimum) {
                minimum = position;
            } else if (columnNumber == _columnCount - 1 && position > maximum) {
                maximum = position;
            }
        }
        if (targetHeader < minimum) {
            return canScrollHorizontally() ? new PointF(-1, 0) : new PointF(0, -1);
        } else if (targetHeader > maximum) {
            return canScrollHorizontally() ? new PointF(1, 0) : new PointF(0, 1);
        } else {
            return null;
        }
    }
    //endregion

    //region 重写
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (DEBUG) {
            Log.d(TAG, "onLayoutChildren");
        }

        final int itemCount = state.getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        detachAndScrapAttachedViews(recycler);

        if (canScrollHorizontally()) {
            final Map<Integer, Integer> headers = resolveHorizontally(state);
            fillHorizontally(recycler, state, headers);
        } else {
            final Map<Integer, Integer> headers = resolveVertically(state);
            fillVertically(recycler, state, headers);
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return _orientation == RecyclerView.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return _orientation == RecyclerView.VERTICAL;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int distance = restrictScrollHorizontally(state, dx);
        final ScrolledParams params = resolveScrollHorizontally(state, distance);
        // 回收
        removeAndRecycleViewsHorizontally(recycler, state, params.recycleHeaders);
        // 填充
        fillHorizontally(recycler, state, params.fillHeaders);
        // 移动
        offsetChildrenHorizontal(-distance);
        _x -= distance;
        return distance;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int distance = restrictScrollVertically(state, dy);
        final ScrolledParams params = resolveScrollVertically(state, distance);
        // 回收
        removeAndRecycleViewsVertically(recycler, state, params.recycleHeaders);
        // 填充
        fillVertically(recycler, state, params.fillHeaders);
        // 移动
        offsetChildrenVertical(-distance);
        _y -= distance;
        return distance;
    }

    @Override
    public int computeHorizontalScrollExtent(@NonNull RecyclerView.State state) {
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int itemWidth = availableWidth / _columnCount;
        return itemWidth * _columnCount;
    }

    @Override
    public int computeVerticalScrollExtent(@NonNull RecyclerView.State state) {
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int itemHeight = availableHeight / _rowCount;
        return itemHeight * _rowCount;
    }

    @Override
    public int computeHorizontalScrollOffset(@NonNull RecyclerView.State state) {
        return -_x;
    }

    @Override
    public int computeVerticalScrollOffset(@NonNull RecyclerView.State state) {
        return -_y;
    }

    @Override
    public int computeHorizontalScrollRange(@NonNull RecyclerView.State state) {
        final int itemCount = getItemCount();
        final int pageSize = _columnCount * _rowCount;
        final int pageCount = itemCount / pageSize + (itemCount % pageSize > 0 ? 1 : 0);
        final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int itemWidth = availableWidth / _columnCount;
        final int pageWidth = itemWidth * _columnCount;
        return pageCount * pageWidth;
    }

    @Override
    public int computeVerticalScrollRange(@NonNull RecyclerView.State state) {
        final int itemCount = getItemCount();
        final int pageSize = _columnCount * _rowCount;
        final int pageCount = itemCount / pageSize + (itemCount % pageSize > 0 ? 1 : 0);
        final int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int itemHeight = availableHeight / _rowCount;
        final int pageHeight = itemHeight * _rowCount;
        return pageCount * pageHeight;
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
