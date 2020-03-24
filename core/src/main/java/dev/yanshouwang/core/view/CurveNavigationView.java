package dev.yanshouwang.core.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.core.view.ViewCompat;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import dev.yanshouwang.core.R;
import dev.yanshouwang.core.transition.Deformation;
import dev.yanshouwang.core.transition.Move;

public class CurveNavigationView extends ViewGroup {
    private static final int DEF_STYLE_RES = R.style.Widget_Wonder_CurveNavigationView;
    private static final long ACTIVE_ANIMATION_DURATION_MS = 300L;

    private final MenuInflater mInflater;
    private final MenuBuilder mBuilder;
    private final int mItemHeight;

    public CurveNavigationView(@NonNull Context context) {
        this(context, null);
    }

    public CurveNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.curveNavigationStyle);
    }

    public CurveNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, DEF_STYLE_RES);
    }

    public CurveNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.mInflater = new MenuInflater(context);
        this.mBuilder = new MenuBuilder(context);

        this.setClipChildren(false);

        final Resources res = this.getResources();
        this.mItemHeight = res.getDimensionPixelSize(R.dimen.bnv_item_height);
        //float labelSize = res.getDimension(R.dimen.bnv_label_size);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CurveNavigationView, defStyleAttr, defStyleRes);
        try {
            float iconSize = res.getDimension(R.dimen.bnv_icon_size);
            float radius = a.getDimension(R.styleable.CurveNavigationView_iconSize, iconSize);
            float elevation = res.getDimension(R.dimen.bnv_elevation);
            float interpolation = 0f;
            Drawable drawable = this.createShapeDrawable(radius, elevation, interpolation);
            ViewCompat.setBackground(this, drawable);

            if (a.hasValue(R.styleable.CurveNavigationView_menu)) {
                int resId = a.getResourceId(R.styleable.CurveNavigationView_menu, 0);
                this.inflate(resId);
            }
        } finally {
            a.recycle();
        }
    }

    private Drawable createShapeDrawable(float radius, float elevation, float interpolation) {
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        EdgeTreatment topEdge = new CircleEdgeTreatment(radius, radius, 0f, radius * 0.6f, false);
        ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                .setTopEdge(topEdge)
                .build();
        shapeDrawable.setShapeAppearanceModel(model);
        shapeDrawable.setPaintStyle(Paint.Style.FILL);
        final Drawable drawable = this.getBackground();
        if (drawable instanceof ColorDrawable) {
            final ColorDrawable colorDrawable = (ColorDrawable) drawable;
            int color = colorDrawable.getColor();
            ColorStateList fillColor = ColorStateList.valueOf(color);
            shapeDrawable.setFillColor(fillColor);
        }
        shapeDrawable.setElevation(elevation);
        shapeDrawable.setInterpolation(interpolation);
        return shapeDrawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int visibleCount = this.mBuilder.getVisibleItems().size();
        final int totalCount = this.getChildCount();
        final int averageWidth = width / (visibleCount == 0 ? 1 : visibleCount);
        int extra = width - averageWidth * visibleCount;
        for (int i = 0; i < totalCount; i++) {
            final View itemView = this.getChildAt(i);
            final int visibility = itemView.getVisibility();
            if (visibility == View.GONE) {
                continue;
            }
            int itemWidth = averageWidth;
            if (extra > 0) {
                itemWidth++;
                extra--;
            }
            final int itemWidthMeasureSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
            final int itemHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY);
            itemView.measure(itemWidthMeasureSpec, itemHeightMeasureSpec);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = itemView.getMeasuredWidth();
        }

        this.setMeasuredDimension(
                View.resolveSizeAndState(width, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 0),
                View.resolveSizeAndState(mItemHeight, heightMeasureSpec, 0)
        );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);

        final int count = this.getChildCount();
        final int width = right - left;
        final int height = bottom - top;
        int used = 0;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            final int using = child.getMeasuredWidth();
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                child.layout(width - used - using, 0, width - used, height);
            } else {
                child.layout(used, 0, used + using, height);
            }
            used += using;
        }
    }

    public void inflate(int resId) {
        this.mInflater.inflate(resId, this.mBuilder);
        for (int i = 0; i < this.mBuilder.size(); i++) {
            MenuItem item = this.mBuilder.getItem(i);
            final Context context = this.getContext();
            CurveNavigationItemView itemView = new CurveNavigationItemView(context);
            itemView.initialize((MenuItemImpl) item, 0);
            itemView.setOnClickListener(this::animateItemView);
            this.addView(itemView);
        }
    }

    private void animateItemView(View v) {
        CurveNavigationItemView itemView = (CurveNavigationItemView) v;
        float horizontalOffset = itemView.getX() + itemView.getWidth() / 2f - itemView.getIconSize();

        final MaterialShapeDrawable drawable = this.getShapeDrawable();
        final CircleEdgeTreatment topEdge = this.getTopEdge();
        drawable.setInterpolation(0f);
        if (topEdge.getHorizontalOffset() != horizontalOffset) {
            topEdge.setHorizontalOffset(horizontalOffset);
        }
        for (int i = 0; i < this.getChildCount(); i++) {
            CurveNavigationItemView wiv = (CurveNavigationItemView) this.getChildAt(i);
            wiv.translateIcon(0f);
        }

        TransitionSet transitionSet = new AutoTransition();
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        Interpolator interpolator = new OvershootInterpolator();
        transitionSet.setInterpolator(interpolator);
        transitionSet.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        Transition move = new Move();
        transitionSet.addTransition(move);
        Transition deformation = new Deformation();
        transitionSet.addTransition(deformation);
        TransitionManager.beginDelayedTransition(this, transitionSet);

        drawable.setInterpolation(1f);
        float verticalOffset = topEdge.getVerticalOffset();
        itemView.translateIcon(verticalOffset);
    }

    private MaterialShapeDrawable getShapeDrawable() {
        return (MaterialShapeDrawable) this.getBackground();
    }

    private CircleEdgeTreatment getTopEdge() {
        return (CircleEdgeTreatment) this.getShapeDrawable().getShapeAppearanceModel().getTopEdge();
    }
}
