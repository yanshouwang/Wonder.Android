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

    private final List<KeyPoint> mKeyPoints;
    private final int mStep;
    @RecyclerView.Orientation
    private final int mOrientation;

    private int mOffset;

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
        int travel = dy;
        final int offset = mOffset + dy;
        if (offset < 0) {
            travel = 0;
        }
        final int oldStart = calculateStart();
        mOffset += travel;
        final int newStart = calculateStart();
        if (newStart != oldStart) {
            onLayoutChildren(recycler, state);
        }
        return travel;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int itemCount = state.getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);

        final int i0 = findFirstVisibleIndex();
        Log.i(TAG, "第一个可见视图索引： " + i0);
        final int start = calculateStart();
        Log.i(TAG, "关键点起始索引： " + start);
        final int loopCount = (mKeyPoints.size() - start) / mStep + ((mKeyPoints.size() - start) % mStep > 0 ? 1 : 0);
        for (int i = 0; i < loopCount; i++) {
            final int j = i0 + i;
            if (j >= itemCount) {
                break;
            }
            final KeyPoint keyPoint = mKeyPoints.get(i * mStep + start);
            final View child = recycler.getViewForPosition(j);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            final int width = getDecoratedMeasuredWidth(child);
            final int height = getDecoratedMeasuredHeight(child);
            final int left = (int) keyPoint.x - width / 2;
            final int top = (int) keyPoint.y - height / 2;
            final int right = left + width;
            final int bottom = top + height;
            layoutDecorated(child, left, top, right, bottom);
            final float rotation = (float) keyPoint.degrees;
            child.setRotation(rotation);
        }
    }

    private int findFirstVisibleIndex() {
        final int interval = getWidth() / 5 / mStep;
        final int index = mOffset / interval;
        return index % mStep == 0 ? index / mStep : index / mStep + 1;
    }

    private int calculateStart() {
        final int interval = getWidth() / 5 / mStep;
        final int start = mStep - (mOffset / interval) % mStep;
        return start == mStep ? 0 : start;
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
