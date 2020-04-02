package dev.yanshouwang.wonder.launcher;

import android.content.ComponentName;
import android.content.pm.LauncherActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.List;

import dev.yanshouwang.wonder.core.view.Indicator;
import dev.yanshouwang.wonder.launcher.compat.LauncherAppsCompat;
import dev.yanshouwang.wonder.launcher.compat.UserManagerCompat;
import dev.yanshouwang.wonder.launcher.model.AppModel;
import dev.yanshouwang.wonder.launcher.recyclerview.AppsAdapter;
import dev.yanshouwang.wonder.recyclerview.MultiGridLayoutManager;
import dev.yanshouwang.wonder.recyclerview.MultiGridSnapHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        final RecyclerView appsView = findViewById(R.id.appsView);
        final Indicator indicator = findViewById(R.id.indicator);

        final RecyclerView.LayoutManager layout = new MultiGridLayoutManager(3, 4, RecyclerView.HORIZONTAL);
        //final Path path = new Path();
        //path.addArc(250, 300, 850, 1000, -90, 180);
        //final RecyclerView.LayoutManager layout = new PathLayoutManager(path, 15, 3);
        appsView.setLayoutManager(layout);
        final List<AppModel> models = getAppModels();
        final RecyclerView.Adapter adapter = new AppsAdapter(models);
        appsView.setAdapter(adapter);
        final SnapHelper helper = new MultiGridSnapHelper();
        helper.attachToRecyclerView(appsView);
        final int pageSize = 3 * 4;
        final int pageCount = models.size() / pageSize + (models.size() % pageSize > 0 ? 1 : 0);
        indicator.setCount(pageCount);
        appsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                assert layoutManager != null;
                if (layoutManager.canScrollHorizontally()) {
                    final int offset = recyclerView.computeHorizontalScrollOffset();
                    final int range = recyclerView.computeHorizontalScrollRange();
                    final int extent = recyclerView.computeHorizontalScrollExtent();
                    final float progress = (float) offset / (range - extent);
                    indicator.setProgress(progress);
                } else {
                    final int offset = recyclerView.computeVerticalScrollOffset();
                    final int range = recyclerView.computeVerticalScrollRange();
                    final int extent = recyclerView.computeVerticalScrollExtent();
                    final float progress = (float) offset / (range - extent);
                    indicator.setProgress(progress);
                }
            }
        });
        indicator.addOnProgressChangedListener(progress -> {
            if (layout.canScrollHorizontally()) {
                final int offset = appsView.computeHorizontalScrollOffset();
                final int range = appsView.computeHorizontalScrollRange();
                final int extent = appsView.computeHorizontalScrollExtent();
                final int x = (int) (progress * (range - extent) - offset);
                appsView.scrollBy(x, 0);
            } else {
                final int offset = appsView.computeVerticalScrollOffset();
                final int range = appsView.computeVerticalScrollRange();
                final int extent = appsView.computeVerticalScrollExtent();
                final int y = (int) (progress * (range - extent) - offset);
                appsView.scrollBy(0, y);
            }
        });

        //int visibility = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        //container.setSystemUiVisibility(visibility);

//        PackageManager pm = getPackageManager();
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
    }

    private List<AppModel> getAppModels() {
        List<AppModel> models = new ArrayList<>();
        LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(this);
        UserManagerCompat userManager = UserManagerCompat.getInstance(this);
        final List<UserHandle> users = userManager.getUserProfiles();
        for (UserHandle user : users) {
            List<LauncherActivityInfo> apps = launcherApps.getActivityList(null, user);
            if (apps == null || apps.isEmpty()) {
                return models;
            }
            for (LauncherActivityInfo app : apps) {
                Drawable icon = app.getIcon(0);
                CharSequence label = app.getLabel();
                ComponentName componentName = app.getComponentName();
                AppModel model = new AppModel(icon, label, componentName);
                models.add(model);
            }
        }
        return models;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
