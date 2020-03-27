package dev.yanshouwang.wonder.recyclerview;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PathLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = PathLayoutManager.class.getSimpleName();
    private static final boolean DEBUG = true;

    private final List<KeyPoint> mKeyPoints;
    private final int mStep;
    @RecyclerView.Orientation
    private final int mOrientation;

    private int mY;

    public PathLayoutManager(@NonNull Path path, int count, int step, @RecyclerView.Orientation int orientation) {
        if (count <= 0 || step <= 0) {
            throw new IllegalArgumentException();
        }

        mKeyPoints = extractKeyPoints(path, count);
        mStep = step;
        mOrientation = orientation;
    }

    public PathLayoutManager(Path path, int count, int step) {
        this(path, count, step, RecyclerView.VERTICAL);
    }

    private List<KeyPoint> extractKeyPoints(Path path, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be bigger than 0");
        }
        final List<KeyPoint> keyPoints = new ArrayList<>();
        final PathMeasure measure = new PathMeasure(path, false);
        final float length = measure.getLength();
        if (length == 0) {
            throw new IllegalArgumentException("Path length is 0");
        }
        final float accuracy = length / count;
        float distance = 0f;
        final float[] pos = new float[2];
        final float[] tan = new float[2];
        while (distance <= length) {
            measure.getPosTan(distance, pos, tan);
            final double radians = Math.atan2(tan[1], tan[0]);
            final double degrees = Math.toDegrees(radians);
            final KeyPoint keyPoint = new KeyPoint(pos[0], pos[1], degrees);
            keyPoints.add(keyPoint);
            distance += accuracy;
        }
        return keyPoints;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == RecyclerView.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == RecyclerView.VERTICAL;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            Log.d(TAG, "preLayout");
        }
        int travel = -dy;
        final int head0 = findHeadVisibleChildPosition();
        final int tail0 = findTailVisibleChildPosition();
        final int j0 = calculateStartKeyPointPosition();
        mY += travel;
        final int head1 = findHeadVisibleChildPosition();
        final int tail1 = findTailVisibleChildPosition();
        final int j1 = calculateStartKeyPointPosition();
        if (j1 != j0) {
            // 回收子视图
            if (head0 != -1) {
                if (head1 == -1) {
                    removeAndRecycleAllViews(recycler);
                } else {
                    for (int i = tail0; i >= head0; i--) {
                        if (i < head1 || i > tail1) {
                            final int index = i - head0;
                            if (DEBUG) {
                                Log.d(TAG, "回收子视图：" + index);
                            }
                            removeAndRecycleViewAt(index, recycler);
                        }
                    }
                }
            }
            // 更新布局
            onLayoutChildren(recycler, state);
        }
        return travel;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int itemCount = state.getItemCount();
        final int i0 = findHeadVisibleChildPosition();
        // 未实现预加载
        // 如果子视图数量为 0 或者未找到可见子视图，回收所有子视图
        if (state.isPreLayout() || itemCount == 0 || i0 == -1) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        // 重新布局
        final int i1 = findTailVisibleChildPosition();
        // 缓存所有子视图
        detachAndScrapAttachedViews(recycler);
        int j = calculateStartKeyPointPosition() + i0 * mStep;
        for (int i = i0; i <= i1; i++) {
            final View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            final int width = getDecoratedMeasuredWidth(child);
            final int height = getDecoratedMeasuredHeight(child);
            final KeyPoint keyPoint = mKeyPoints.get(j);
            final int left = (int) keyPoint.x - width / 2;
            final int top = (int) keyPoint.y - height / 2;
            final int right = left + width;
            final int bottom = top + height;
            final float rotation = (float) keyPoint.degrees;
            layoutDecoratedWithMargins(child, left, top, right, bottom);
            child.setRotation(rotation);

            j += mStep;
        }
    }

    private void removeAndRecycleViews(RecyclerView.Recycler recycler, int i0, int i1) {
        if (i0 == i1) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "回收视图：" + i0 + " ~ " + i1);
        }
        if (i0 < i1) {
            for (int i = i1 - 1; i >= i0; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        } else {
            for (int i = i0; i > i1; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        }
    }

    private int findHeadVisibleChildPosition() {
        int i0 = -1;
        final int jn = mKeyPoints.size() - 1;
        int j = calculateStartKeyPointPosition();
        final int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (j < 0) {
                j += mStep;
            } else if (j > jn) {
                break;
            } else {
                i0 = i;
                break;
            }
        }
        return i0;
    }

    private int findTailVisibleChildPosition() {
        int i1 = -1;
        final int jn = mKeyPoints.size() - 1;
        int j = calculateStartKeyPointPosition();
        final int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (j < 0) {
                j += mStep;
            } else if (j > jn) {
                break;
            } else {
                i1 = i;
                j += mStep;
            }
        }
        return i1;
    }

    private int calculateStartKeyPointPosition() {
        final int distance = getWidth() / 10;
        return mY / distance;
    }

    private static class KeyPoint {
        final float x;
        final float y;
        final double degrees;

        KeyPoint(float x, float y, double degrees) {
            this.x = x;
            this.y = y;
            this.degrees = degrees;
        }
    }
}
