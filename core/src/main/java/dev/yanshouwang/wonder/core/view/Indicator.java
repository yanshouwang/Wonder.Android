package dev.yanshouwang.wonder.core.view;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import dev.yanshouwang.wonder.core.R;

public class Indicator extends View {
    private static final int ANIMATION_DURATION = 300;

    private final Paint mPaint;
    private final float mDotSize;
    private final float mDotExtension;
    private final float mDotSpacing;
    private final int mInactiveColor;
    private final int mActiveColor;

    private int mCount;
    private float mProgress;
    private float mEventProgress;
    private Runnable mEventAction;

    private List<OnProgressChangedListener> mOnProgressChangedListeners;

    public Indicator(Context context) {
        this(context, null);
    }

    public Indicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Indicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Indicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mProgress = 0f;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources res = getResources();
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Indicator, defStyleAttr, defStyleRes);
        try {
            final int count = res.getInteger(R.integer.indicator_count);
            mCount = a.getInteger(R.styleable.Indicator_count, count);
            final float dotSize = res.getDimension(R.dimen.indicator_dotSize);
            mDotSize = a.getDimension(R.styleable.Indicator_dotSize, dotSize);
            final float dotSpacing = res.getDimension(R.dimen.indicator_dotSpacing);
            mDotSpacing = a.getDimension(R.styleable.Indicator_dotSpacing, dotSpacing);
            final float dotExtension = res.getDimension(R.dimen.indicator_dotExtension);
            mDotExtension = a.getDimension(R.styleable.Indicator_dotExtension, dotExtension);
            final int inactiveColor = res.getColor(R.color.indicator_inactiveColor);
            mInactiveColor = a.getColor(R.styleable.Indicator_inactiveColor, inactiveColor);
            final int activeColor = res.getColor(R.color.indicator_activeColor);
            mActiveColor = a.getColor(R.styleable.Indicator_activeColor, activeColor);
        } finally {
            a.recycle();
        }
    }

    public void setCount(int count) {
        mCount = count;
        requestLayout();
        invalidate();
    }

    public void setProgress(float progress) {
        if (mProgress == progress) {
            return;
        }

        mProgress = progress;
        invalidate();

        if (mOnProgressChangedListeners == null) {
            return;
        }
        for (OnProgressChangedListener listener : mOnProgressChangedListeners) {
            listener.onProgressChanged(progress);
        }
    }

    public void addOnProgressChangedListener(@NonNull OnProgressChangedListener listener) {
        if (mOnProgressChangedListeners == null) {
            mOnProgressChangedListeners = new ArrayList<>();
        } else if (mOnProgressChangedListeners.contains(listener)) {
            return;
        }
        mOnProgressChangedListeners.add(listener);
    }

    public void removeOnProgressChangedListener(@NonNull OnProgressChangedListener listener) {
        if (mOnProgressChangedListeners == null || !mOnProgressChangedListeners.contains(listener)) {
            return;
        }
        mOnProgressChangedListeners.remove(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final float length = mDotSize * mCount + mDotExtension + mDotSpacing * (mCount - 1);
        final int widthSize = (int) length + getPaddingLeft() + getPaddingRight();
        final int heightSize = (int) mDotSize + getPaddingTop() + getPaddingBottom();
        final int measuredWidth = resolveSize(widthSize, widthMeasureSpec);
        final int measuredHeight = resolveSize(heightSize, heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final int width = getWidth() - getPaddingLeft() - getPaddingRight();
        final int height = getHeight() - getPaddingTop() - getPaddingBottom();
        final float centerX = width / 2f + getPaddingLeft();
        final float centerY = height / 2f + getPaddingTop();
        final float r = mDotSize / 2;
        final float length = mDotSize * mCount + mDotExtension + mDotSpacing * mCount;
        float x0 = centerX - length / 2;

        if (mCount <= 0) {
            throw new InvalidParameterException();
        } else if (mCount == 1) {
            final float left = x0 + mDotSpacing / 2;
            final float top = centerY - r;
            final float right = left + mDotSize + mDotExtension;
            final float bottom = centerY + r;
            mPaint.setColor(mActiveColor);
            canvas.drawRoundRect(left, top, right, bottom, r, r, mPaint);
        } else {
            final float unit = 1f / (mCount - 1);

            final int r0 = mInactiveColor >> 16 & 0xFF;
            final int g0 = mInactiveColor >> 8 & 0xFF;
            final int b0 = mInactiveColor & 0xFF;

            final int r2 = mActiveColor >> 16 & 0xFF;
            final int g2 = mActiveColor >> 8 & 0xFF;
            final int b2 = mActiveColor & 0xFF;

            final int r1 = r2 - r0;
            final int g1 = g2 - g0;
            final int b1 = b2 - b0;

            for (int i = 0; i < mCount; i++) {
                final float vi = i * unit;
                final float vh = vi - unit;
                final float vj = vi + unit;
                if (mProgress >= vi && mProgress < vj) {
                    // 圆角矩形剩余部分
                    final float ratio = (vj - mProgress) / unit;
                    final float left = x0 + mDotSpacing / 2;
                    final float top = centerY - r;
                    final float right = left + mDotSize + mDotExtension * ratio;
                    final float bottom = centerY + r;
                    final int red = (int) (r1 * ratio) + r0;
                    final int green = (int) (g1 * ratio) + g0;
                    final int blue = (int) (b1 * ratio) + b0;
                    final int color = Color.rgb(red, green, blue);
                    mPaint.setColor(color);
                    canvas.drawRoundRect(left, top, right, bottom, r, r, mPaint);
                    x0 = right + mDotSpacing / 2;
                } else if (mProgress > vh && mProgress < vi) {
                    // 圆角矩形滑出部分
                    final float ratio = (mProgress - vh) / unit;
                    final float left = x0 + mDotSpacing / 2;
                    final float top = centerY - r;
                    final float right = left + mDotSize + mDotExtension * ratio;
                    final float bottom = centerY + r;
                    final int red = (int) (r1 * ratio) + r0;
                    final int green = (int) (g1 * ratio) + g0;
                    final int blue = (int) (b1 * ratio) + b0;
                    final int color = Color.rgb(red, green, blue);
                    mPaint.setColor(color);
                    canvas.drawRoundRect(left, top, right, bottom, r, r, mPaint);
                    x0 = right + mDotSpacing / 2;
                } else {
                    // 圆点
                    final float cx = x0 + mDotSpacing / 2 + r;
                    mPaint.setColor(mInactiveColor);
                    canvas.drawCircle(cx, centerY, r, mPaint);
                    x0 += mDotSize + mDotSpacing;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                float progress = xToProgress(x);
                if (progress != mProgress) {
                    runAnimation(progress);
                }

                mEventProgress = progress;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = event.getX();
                float progress = xToProgress(x);
                if (mEventProgress != progress) {
                    removeCallbacks(mEventAction);
                    mEventAction = () -> runAnimation(progress);
                    postDelayed(mEventAction, ANIMATION_DURATION);

                    mEventProgress = progress;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                removeCallbacks(mEventAction);
                performClick();
                break;
            }
            default: {
                Log.i(Indicator.class.getSimpleName(), String.valueOf(action));
                break;
            }
        }
        return true;
    }

    private void runAnimation(float progress) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(this, "progress", mProgress, progress)
                .setDuration(ANIMATION_DURATION);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        animator.setInterpolator(interpolator);
        animator.start();

        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        //playSoundEffect(SoundEffectConstants.CLICK);
    }

    private float xToProgress(float x) {
        float progress;
        final int width = getWidth() - getPaddingLeft() - getPaddingRight();
        final float length = mDotSize * mCount + mDotExtension + mDotSize * mCount;
        final float x0 = (width - length) / 2 + getPaddingLeft();
        final float x1 = x0 + length;
        if (mCount <= 0) {
            throw new InvalidParameterException();
        } else if (mCount == 1) {
            progress = mProgress;
        } else if (x < x0) {
            progress = 0f;
        } else if (x > x1) {
            progress = 1f;
        } else {
            progress = mProgress;
            final float unit = 1f / (mCount - 1);
            float xi = x0;
            float xj;
            for (int i = 0; i < mCount; i++) {
                final float vi = i * unit;
                final float vh = vi - unit;
                final float vj = vi + unit;
                if (mProgress >= vi && mProgress < vj) {
                    float ratio = (vj - mProgress) / unit;
                    xj = xi + mDotSize + mDotExtension * ratio + mDotSpacing;
                } else if (mProgress > vh && mProgress < vi) {
                    float ratio = (mProgress - vh) / unit;
                    xj = xi + mDotSize + mDotExtension * ratio + mDotSpacing;
                } else {
                    xj = xi + mDotSize + mDotSpacing;
                }
                if (x >= xi && x < xj) {
                    progress = vi;
                    break;
                }
                xi = xj;
            }
        }
        return progress;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(float progress);
    }
}
