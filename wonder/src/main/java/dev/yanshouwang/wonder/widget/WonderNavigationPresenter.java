package dev.yanshouwang.wonder.widget;

import android.content.Context;
import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.view.menu.SubMenuBuilder;

public class WonderNavigationPresenter implements MenuPresenter {

    private static final int NAVIGATION_PRESENTER_ID = 1;

    private WonderNavigationView mNavigationView;

    public WonderNavigationPresenter(WonderNavigationView navigationView) {
        this.mNavigationView = navigationView;
    }

    @Override
    public void initForMenu(Context context, MenuBuilder menu) {
        this.mNavigationView.initialize(menu);
    }

    @Override
    public MenuView getMenuView(ViewGroup root) {
        return this.mNavigationView;
    }

    @Override
    public void updateMenuView(boolean cleared) {
        if (cleared) {
            this.mNavigationView.build();
        } else {
            this.mNavigationView.update();
        }
    }

    @Override
    public void setCallback(Callback cb) {

    }

    @Override
    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        return false;
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {

    }

    @Override
    public boolean flagActionItems() {
        return false;
    }

    @Override
    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    @Override
    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    @Override
    public int getId() {
        return WonderNavigationPresenter.NAVIGATION_PRESENTER_ID;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return null;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

    }
}
