package dev.yanshouwang.wonder.recyclerview;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public class MultiGridSnapHelper extends SnapHelper {
    //region 字段
    private static final boolean DEBUG = true;
    private static final String TAG = MultiGridSnapHelper.class.getSimpleName();
    private static final float MILLISECONDS_PER_INCH = 40f;

    private RecyclerView _recyclerView;
    private OrientationHelper _horizontalHelper;
    private OrientationHelper _verticalHelper;
    //endregion

    //region 方法
    private OrientationHelper getOrientationHelper(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollHorizontally()) {
            return getHorizontalHelper(layoutManager);
        } else if (layoutManager.canScrollVertically()) {
            return getVerticalHelper(layoutManager);
        } else {
            return null;
        }
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        if (_horizontalHelper == null || _horizontalHelper.getLayoutManager() != layoutManager) {
            _horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return _horizontalHelper;
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        if (_verticalHelper == null || _verticalHelper.getLayoutManager() != layoutManager) {
            _verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return _verticalHelper;
    }

    private int distanceToPageCenter(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView, OrientationHelper helper) {
        final MultiGridLayoutManager multiGridLayoutManager = (MultiGridLayoutManager) layoutManager;
        final int columnCount = multiGridLayoutManager.getColumnCount();
        final int rowCount = multiGridLayoutManager.getRowCount();
        final int targetPosition = layoutManager.getPosition(targetView);
        final int columnNumber = targetPosition % columnCount;
        final int rowNumber = targetPosition / columnCount % rowCount;

        final int targetCount = layoutManager.canScrollHorizontally() ? columnCount : rowCount;
        final int targetNumber = layoutManager.canScrollHorizontally() ? columnNumber : rowNumber;

        final int childStart = helper.getDecoratedStart(targetView);
        final int childMeasurement = helper.getDecoratedMeasurement(targetView);
        final int childCenter = childStart + childMeasurement / 2;
        final int pageStart = helper.getStartAfterPadding();
        final int pageMeasurement = childMeasurement * targetCount;
        final int pageCenter = pageStart + pageMeasurement / 2;

        if (targetNumber == 0) {
            // 目标为页面第一个子视图，将子视图起始位置与页面起始位置对齐
            return childStart - pageStart;
        }
        if (targetCount % 2 == 0) {
            // 偶数列（行），将子视图起始位置与页面中线对齐
            return childStart - pageCenter;
        } else {
            // 奇数列（行），将子视图中线与页面中线对齐
            return childCenter - pageCenter;
        }
    }

    private View findCenterView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
        final int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        final MultiGridLayoutManager multiGridLayoutManager = (MultiGridLayoutManager) layoutManager;
        final int columnCount = multiGridLayoutManager.getColumnCount();
        final int rowCount = multiGridLayoutManager.getRowCount();
        final int targetCount = layoutManager.canScrollHorizontally() ? columnCount : rowCount;
        final int targetNumber = targetCount / 2;
        final int pageStart = helper.getStartAfterPadding();
        final View view = layoutManager.getChildAt(0);
        assert view != null;
        final int childMeasurement = helper.getDecoratedMeasurement(view);
        final int pageMeasurement50 = childMeasurement * targetCount / 2;
        final int pageCenter = pageStart + pageMeasurement50;
        View targetView = null;
        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            assert child != null;
            final int position = layoutManager.getPosition(child);
            final int columnNumber = position % columnCount;
            final int rowNumber = position / columnCount % rowCount;
            if (layoutManager.canScrollHorizontally()) {
                if (rowNumber != 0 || columnNumber != targetNumber) {
                    continue;
                }
            } else if (columnNumber != 0 || rowNumber != targetNumber) {
                continue;
            }
            if (targetCount % 2 == 0) {
                // 偶数列，取左侧距离页面中线最近的子视图
                final int childStart = helper.getDecoratedStart(child);
                final int distance = Math.abs(childStart - pageCenter);
                if (distance <= pageMeasurement50) {
                    targetView = child;
                    break;
                }

            } else {
                // 奇数列，取中线距离页面中线最近的子视图
                final int childCenter = helper.getDecoratedStart(child) + helper.getDecoratedMeasurement(child) / 2;
                final int distance = Math.abs(childCenter - pageCenter);
                if (distance <= pageMeasurement50) {
                    targetView = child;
                    break;
                }
            }
        }
        if (targetView == null) {
            // 最后一页有可能不存在中线上的子视图，取页面的第一个子视图
            for (int i = 0; i < childCount; i++) {
                final View child = layoutManager.getChildAt(i);
                assert child != null;
                final int position = layoutManager.getPosition(child);
                final int columnNumber = position % columnCount;
                final int rowNumber = position / columnCount % rowCount;
                if (rowNumber == 0 && columnNumber == 0) {
                    targetView = child;
                    break;
                }
            }
        }
        return targetView;
    }

    private boolean isForwardFling(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (layoutManager.canScrollHorizontally()) {
            return velocityX > 0;
        } else {
            return velocityY > 0;
        }
    }
    //endregion

    //region 实现
    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        final int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            final OrientationHelper helper = getHorizontalHelper(layoutManager);
            out[0] = distanceToPageCenter(layoutManager, targetView, helper);
        }
        if (layoutManager.canScrollVertically()) {
            final OrientationHelper helper = getVerticalHelper(layoutManager);
            out[1] = distanceToPageCenter(layoutManager, targetView, helper);
        }
        return out;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        View targetView = null;
        if (layoutManager.canScrollHorizontally()) {
            final OrientationHelper helper = getHorizontalHelper(layoutManager);
            targetView = findCenterView(layoutManager, helper);
        } else if (layoutManager.canScrollVertically()) {
            final OrientationHelper helper = getVerticalHelper(layoutManager);
            targetView = findCenterView(layoutManager, helper);
        }
        return targetView;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        final int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        final MultiGridLayoutManager multiGridLayoutManager = (MultiGridLayoutManager) layoutManager;
        final OrientationHelper helper = getOrientationHelper(layoutManager);
        assert helper != null;
        final int columnCount = multiGridLayoutManager.getColumnCount();
        final int rowCount = multiGridLayoutManager.getRowCount();
        final int pageSize = columnCount * rowCount;
        final int targetCount = layoutManager.canScrollHorizontally() ? columnCount : rowCount;
        final View view = layoutManager.getChildAt(0);
        assert view != null;
        final int childMeasurement = helper.getDecoratedMeasurement(view);
        final int pageMeasurement = childMeasurement * targetCount;
        final int offset = layoutManager.canScrollHorizontally() ? -multiGridLayoutManager.getX() : -multiGridLayoutManager.getY();
        int targetPosition;
        final boolean isForwardFling = isForwardFling(layoutManager, velocityX, velocityY);
        if (isForwardFling) {
            final int pageNumber = offset / pageMeasurement;
            targetPosition = (pageNumber + 1) * pageSize;
        } else {
            final int pageNumber = offset / pageMeasurement + (offset % pageMeasurement > 0 ? 1 : 0);
            targetPosition = (pageNumber - 1) * pageSize;
        }
        if (targetPosition < 0 || targetPosition >= itemCount) {
            return RecyclerView.NO_POSITION;
        }
        if (DEBUG) {
            Log.d(TAG, "target position: " + targetPosition);
        }
        return targetPosition;
    }
    //endregion

    //region 重写
    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);

        _recyclerView = recyclerView;
    }

    @Nullable
    @Override
    protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(_recyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                if (_recyclerView == null) {
                    // The associated RecyclerView has been removed so there is no action to take.
                    return;
                }
                final RecyclerView.LayoutManager layoutManager1 = _recyclerView.getLayoutManager();
                if (layoutManager1 == null) {
                    return;
                }
                int[] distance = calculateDistanceToFinalSnap(layoutManager1, targetView);
                if (distance == null) {
                    return;
                }
                final int dx = distance[0];
                final int dy = distance[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    //endregion
}
