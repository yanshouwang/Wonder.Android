package dev.yanshouwang.wonder.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.view.ViewCompat;

import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import dev.yanshouwang.wonder.R;

public class WonderNavigationView extends FrameLayout implements MenuView {

    private final MaterialShapeDrawable mDrawable;
    private final MenuPresenter mPresenter;
    private final MenuInflater mInflater;
    private final MenuBuilder mBuilder;
    private final int mItemHeight;

    public WonderNavigationView(@NonNull Context context) {
        this(context, null);
    }

    public WonderNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WonderNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WonderNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.mDrawable = new MaterialShapeDrawable();
        this.mPresenter = new WonderNavigationPresenter(this);
        this.mInflater = new SupportMenuInflater(context);
        this.mBuilder = new MenuBuilder(context);

        //this.mBuilder.addMenuPresenter(this.mPresenter);
        final Resources res = this.getResources();
        this.mItemHeight = res.getDimensionPixelSize(R.dimen.bnv_height);
        final float elevation = res.getDimension(R.dimen.bnv_elevation);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WonderNavigationView, defStyleAttr, defStyleRes);
        try {
            float defRadius = res.getDimension(R.dimen.bnv_radius);
            float defCornerRadius = res.getDimension(R.dimen.bnv_corner_radius);
            float defHorizontalOffset = res.getDimension(R.dimen.bnv_horizontal_offset);
            float defVerticalOffset = res.getDimension(R.dimen.bnv_vertical_offset);

            float radius = a.getDimension(R.styleable.WonderNavigationView_radius, defRadius);
            float cornerRadius = a.getDimension(R.styleable.WonderNavigationView_radius, defCornerRadius);
            float horizontalOffset = a.getDimension(R.styleable.WonderNavigationView_radius, defHorizontalOffset);
            float verticalOffset = a.getDimension(R.styleable.WonderNavigationView_radius, defVerticalOffset);

            EdgeTreatment topEdge = new CircleEdgeTreatment(radius, cornerRadius, horizontalOffset, verticalOffset, false);
            ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                    .setTopEdge(topEdge)
                    .build();
            this.mDrawable.setShapeAppearanceModel(model);
            this.mDrawable.setPaintStyle(Paint.Style.FILL);
            this.mDrawable.setElevation(elevation);
            ViewCompat.setBackground(this, mDrawable);

            if (a.hasValue(R.styleable.WonderNavigationView_menu)) {
                int resId = a.getResourceId(R.styleable.WonderNavigationView_menu, 0);
                this.inflateMenu(resId);
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
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
        super.onLayout(changed, left, top, right, bottom);

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

    public void inflateMenu(int resId) {
        this.mInflater.inflate(resId, this.mBuilder);
        for (int i = 0; i < this.mBuilder.size(); i++) {
            MenuItem item = this.mBuilder.getItem(i);
            final Context context = this.getContext();
            WonderNavigationItemView itemView = new WonderNavigationItemView(context);
            itemView.initialize((MenuItemImpl) item, 0);
            this.addView(itemView);
        }
    }

    @Override
    public void initialize(MenuBuilder menu) {

    }

    @Override
    public int getWindowAnimations() {
        return 0;
    }

    public void build() {
        for (int i = 0; i < this.mBuilder.size(); i++) {
            MenuItem item = mBuilder.getItem(i);
        }
    }

    public void update() {

    }
}
