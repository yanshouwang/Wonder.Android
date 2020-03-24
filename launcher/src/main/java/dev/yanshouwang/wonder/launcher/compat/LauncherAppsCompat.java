package dev.yanshouwang.wonder.launcher.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.ShortcutInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;

import java.util.List;

import dev.yanshouwang.wonder.launcher.util.LooperExecutor;

public abstract class LauncherAppsCompat {
    private static final Object sInstanceLock = new Object();

    private static LauncherAppsCompat sInstance;

    public static LauncherAppsCompat getInstance(Context context) {
        // 未使用双 if + lock 模式
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    sInstance = new LauncherAppsCompatVQ(context);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sInstance = new LauncherAppsCompatVO(context);
                } else {
                    sInstance = new LauncherAppsCompatVL(context);
                }
            }
            return sInstance;
        }
    }

    public abstract List<LauncherActivityInfo> getActivityList(String packageName, UserHandle user);

    public abstract LauncherActivityInfo resolveActivity(Intent intent, UserHandle user);

    public abstract ApplicationInfo getApplicationInfo(String packageName, int flags, UserHandle user);

    public abstract void startActivityForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts);

    public abstract void showAppDetailsForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts);

    public abstract void addOnAppsChangedCallback(OnAppsChangedCallbackCompat listener);

    public abstract void removeOnAppsChangedCallback(OnAppsChangedCallbackCompat listener);

    public abstract boolean isPackageEnabledForProfile(String packageName, UserHandle user);

    public abstract boolean isActivityEnabledForProfile(ComponentName component, UserHandle user);

    //public abstract List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(@Nullable PackageUserKey packageUser);

    public abstract List<PackageInstaller.SessionInfo> getAllPackageInstallerSessions();

    public abstract void registerSessionCallback(LooperExecutor executor, PackageInstaller.SessionCallback sessionCallback);

    public abstract void unregisterSessionCallback(PackageInstaller.SessionCallback sessionCallback);

    public interface OnAppsChangedCallbackCompat {
        void onPackageRemoved(String packageName, UserHandle user);

        void onPackageAdded(String packageName, UserHandle user);

        void onPackageChanged(String packageName, UserHandle user);

        void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing);

        void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing);

        void onPackagesSuspended(String[] packageNames, UserHandle user);

        void onPackagesUnsuspended(String[] packageNames, UserHandle user);

        void onShortcutsChanged(String packageName, List<ShortcutInfo> shortcuts, UserHandle user);
    }
}
