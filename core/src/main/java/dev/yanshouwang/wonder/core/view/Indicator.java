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

    private final Paint _paint;
    private final float _dotSize;
    private final float _dotExtension;
    private final float _dotSpacing;
    private final int _inactiveColor;
    private final int _activeColor;

    private int _count;
    private float _progress;
    private float _eventProgress;
    private Runnable _eventAction;

    private List<OnProgressChangedListener> _onProgressChangedListeners;

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

        _progress = 0f;
        _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources res = getResources();
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Indicator, defStyleAttr, defStyleRes);
        try {
            final int count = res.getInteger(R.integer.indicator_count);
            _count = a.getInteger(R.styleable.Indicator_count, count);
            final float dotSize = res.getDimension(R.dimen.indicator_dotSize);
            _dotSize = a.getDimension(R.styleable.Indicator_dotSize, dotSize);
            final float dotSpacing = res.getDimension(R.dimen.indicator_dotSpacing);
            _dotSpacing = a.getDimension(R.styleable.Indicator_dotSpacing, dotSpacing);
            final float dotExtension = res.getDimension(R.dimen.indicator_dotExtension);
            _dotExtension = a.getDimension(R.styleable.Indicator_dotExtension, dotExtension);
            final int inactiveColor = res.getColor(R.color.indicator_inactiveColor);
            _inactiveColor = a.getColor(R.styleable.Indicator_inactiveColor, inactiveColor);
            final int activeColor = res.getColor(R.color.indicator_activeColor);
            _activeColor = a.getColor(R.styleable.Indicator_activeColor, activeColor);
        } finally {
            a.recycle();
        }
    }

    public void setCount(int count) {
        _count = count;
        requestLayout();
        invalidate();
    }

    public void setProgress(float progress) {
        if (_progress == progress) {
            return;
        }

        _progress = progress;
        invalidate();

        if (_onProgressChangedListeners == null) {
            return;
        }
        for (OnProgressChangedListener listener : _onProgressChangedListeners) {
            listener.onProgressChanged(progress);
        }
    }

    public void addOnProgressChangedListener(@NonNull OnProgressChangedListener listener) {
        if (_onProgressChangedListeners == null) {
            _onProgressChangedListeners = new ArrayList<>();
        } else if (_onProgressChangedListeners.contains(listener)) {
            return;
        }
        _onProgressChangedListeners.add(listener);
    }

    public void removeOnProgressChangedListener(@NonNull OnProgressChangedListener listener) {
        if (_onProgressChangedListeners == null || !_onProgressChangedListeners.contains(listener)) {
            return;
        }
        _onProgressChangedListeners.remove(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final float length = _dotSize * _count + _dotExtension + _dotSpacing * (_count - 1);
        final int widthSize = (int) length + getPaddingLeft() + getPaddingRight();
        final int heightSize = (int) _dotSize + getPaddingTop() + getPaddingBottom();
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
        final float r = _dotSize / 2;
        final float length = _dotSize * _count + _dotExtension + _dotSpacing * _count;
        float x0 = centerX - length / 2;

        if (_count <= 0) {
            throw new InvalidParameterException();
        } else if (_count == 1) {
            final float left = x0 + _dotSpacing / 2;
            final float top = centerY - r;
            final float right = left + _dotSize + _dotExtension;
            final float bottom = centerY + r;
            _paint.setColor(_activeColor);
            canvas.drawRoundRect(left, top, right, bottom, r, r, _paint);
        } else {
            final float unit = 1f / (_count - 1);

            final int r0 = _inactiveColor >> 16 & 0xFF;
            final int g0 = _inactiveColor >> 8 & 0xFF;
            final int b0 = _inactiveColor & 0xFF;

            final int r2 = _activeColor >> 16 & 0xFF;
            final int g2 = _activeColor >> 8 & 0xFF;
            final int b2 = _activeColor & 0xFF;

            final int r1 = r2 - r0;
            final int g1 = g2 - g0;
            final int b1 = b2 - b0;

            for (int i = 0; i < _count; i++) {
                final float vi = i * unit;
                final float vh = vi - unit;
                final float vj = vi + unit;
                if (_progress >= vi && _progress < vj) {
                    // 圆角矩形剩余部分
                    final float ratio = (vj - _progress) / unit;
                    final float left = x0 + _dotSpacing / 2;
                    final float top = centerY - r;
                    final float right = left + _dotSize + _dotExtension * ratio;
                    final float bottom = centerY + r;
                    final int red = (int) (r1 * ratio) + r0;
                    final int green = (int) (g1 * ratio) + g0;
                    final int blue = (int) (b1 * ratio) + b0;
                    final int color = Color.rgb(red, green, blue);
                    _paint.setColor(color);
                    canvas.drawRoundRect(left, top, right, bottom, r, r, _paint);
                    x0 = right + _dotSpacing / 2;
                } else if (_progress > vh && _progress < vi) {
                    // 圆角矩形滑出部分
                    final float ratio = (_progress - vh) / unit;
                    final float left = x0 + _dotSpacing / 2;
                    final float top = centerY - r;
                    final float right = left + _dotSize + _dotExtension * ratio;
                    final float bottom = centerY + r;
                    final int red = (int) (r1 * ratio) + r0;
                    final int green = (int) (g1 * ratio) + g0;
                    final int blue = (int) (b1 * ratio) + b0;
                    final int color = Color.rgb(red, green, blue);
                    _paint.setColor(color);
                    canvas.drawRoundRect(left, top, right, bottom, r, r, _paint);
                    x0 = right + _dotSpacing / 2;
                } else {
                    // 圆点
                    final float cx = x0 + _dotSpacing / 2 + r;
                    _paint.setColor(_inactiveColor);
                    canvas.drawCircle(cx, centerY, r, _paint);
                    x0 += _dotSize + _dotSpacing;
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
                if (progress != _progress) {
                    runAnimation(progress);
                }

                _eventProgress = progress;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = event.getX();
                float progress = xToProgress(x);
                if (_eventProgress != progress) {
                    removeCallbacks(_eventAction);
                    _eventAction = () -> runAnimation(progress);
                    postDelayed(_eventAction, ANIMATION_DURATION);

                    _eventProgress = progress;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                removeCallbacks(_eventAction);
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
                .ofFloat(this, "progress", _progress, progress)
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
        final float length = _dotSize * _count + _dotExtension + _dotSize * _count;
        final float x0 = (width - length) / 2 + getPaddingLeft();
        final float x1 = x0 + length;
        if (_count <= 0) {
            throw new InvalidParameterException();
        } else if (_count == 1) {
            progress = _progress;
        } else if (x < x0) {
            progress = 0f;
        } else if (x > x1) {
            progress = 1f;
        } else {
            progress = _progress;
            final float unit = 1f / (_count - 1);
            float xi = x0;
            float xj;
            for (int i = 0; i < _count; i++) {
                final float vi = i * unit;
                final float vh = vi - unit;
                final float vj = vi + unit;
                if (_progress >= vi && _progress < vj) {
                    float ratio = (vj - _progress) / unit;
                    xj = xi + _dotSize + _dotExtension * ratio + _dotSpacing;
                } else if (_progress > vh && _progress < vi) {
                    float ratio = (_progress - vh) / unit;
                    xj = xi + _dotSize + _dotExtension * ratio + _dotSpacing;
                } else {
                    xj = xi + _dotSize + _dotSpacing;
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
