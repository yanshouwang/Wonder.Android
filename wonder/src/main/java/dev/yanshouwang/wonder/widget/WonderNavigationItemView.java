package dev.yanshouwang.wonder.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.TooltipCompat;

import dev.yanshouwang.wonder.R;

public class WonderNavigationItemView extends FrameLayout implements MenuView.ItemView {

    private final ImageView mIcon;
    private final TextView mLabel;

    private MenuItemImpl mItemData;

    public WonderNavigationItemView(@NonNull Context context) {
        this(context, null);
    }

    public WonderNavigationItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WonderNavigationItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WonderNavigationItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final Resources res = this.getResources();
        LayoutInflater.from(context).inflate(R.layout.navigation_item_merge, this, true);

        this.mIcon = this.findViewById(R.id.icon);
        this.mLabel = this.findViewById(R.id.label);
    }

    @Override
    public void initialize(MenuItemImpl itemData, int menuType) {
        this.mItemData = itemData;

        final boolean checkable = itemData.isCheckable();
        final boolean checked = itemData.isChecked();
        final boolean enabled = itemData.isEnabled();
        final Drawable icon = itemData.getIcon();
        final CharSequence title = itemData.getTitle();
        final int id = itemData.getItemId();
        final CharSequence description = itemData.getContentDescription();
        final CharSequence tooltip = itemData.getTooltipText();

        this.setCheckable(checkable);
        this.setChecked(checked);
        this.setEnabled(enabled);
        this.setIcon(icon);
        this.setTitle(title);
        this.setId(id);
        if (TextUtils.isEmpty(description)) {
            this.setContentDescription(title);
        } else {
            this.setContentDescription(description);
        }
        if (TextUtils.isEmpty(tooltip)) {
            TooltipCompat.setTooltipText(this, title);
        } else {
            TooltipCompat.setTooltipText(this, tooltip);
        }
    }

    @Override
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    @Override
    public void setTitle(CharSequence title) {
        this.mLabel.setText(title);
    }

    @Override
    public void setCheckable(boolean checkable) {
        refreshDrawableState();
    }

    @Override
    public void setChecked(boolean checked) {
        setSelected(checked);
    }

    @Override
    public void setShortcut(boolean showShortcut, char shortcutKey) {

    }

    @Override
    public void setIcon(Drawable icon) {
        Drawable drawable = this.mIcon.getDrawable();
        if (icon == drawable) {
            return;
        }
        this.mIcon.setImageDrawable(icon);
    }

    @Override
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override
    public boolean showsIcon() {
        return true;
    }
}
