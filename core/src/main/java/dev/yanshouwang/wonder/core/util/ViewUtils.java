package dev.yanshouwang.wonder.core.util;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

public class ViewUtils {
    public static void enlargeHitBounds(View view, int left, int top, int right, int bottom) {
        Rect bounds = new Rect();
        view.getHitRect(bounds);
        bounds.left -= left;
        bounds.top -= top;
        bounds.right += right;
        bounds.bottom += bottom;
        TouchDelegate delegate = new TouchDelegate(bounds, view);
        View delegateView = (View) view.getParent();
        delegateView.setTouchDelegate(delegate);
    }

    public static boolean isVisibleOnScreen(View view) {
        Rect bounds = new Rect();
        view.getHitRect(bounds);
        return view.getLocalVisibleRect(bounds);
    }
}
