package dev.yanshouwang.wonder.launcher.model;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class AppModel {
    public final Drawable icon;
    public final CharSequence label;
    public final ComponentName componentName;

    public AppModel(Drawable icon, CharSequence label, ComponentName componentName) {
        this.icon = icon;
        this.label = label;
        this.componentName = componentName;
    }
}
