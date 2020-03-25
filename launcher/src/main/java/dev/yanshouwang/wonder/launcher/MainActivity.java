package dev.yanshouwang.wonder.launcher;

import android.content.ComponentName;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;

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
import dev.yanshouwang.wonder.launcher.views.AppsAdapter;
import dev.yanshouwang.wonder.recyclerview.PagerSnapHelper;
import dev.yanshouwang.wonder.recyclerview.GridLayoutManager;
import dev.yanshouwang.wonder.recyclerview.PathLayoutManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        final RecyclerView appsView = findViewById(R.id.appsView);
        final Indicator indicator = findViewById(R.id.indicator);

        //final RecyclerView.LayoutManager layout = new GridLayoutManager(3, 2);
        final Path path = new Path();
        path.moveTo(250, 250);
        path.rLineTo(600, 250);
        path.rLineTo(-600, 250);
        path.rLineTo(600, 250);
        path.rLineTo(-600, 250);
        final RecyclerView.LayoutManager layout = new PathLayoutManager(path, 10, 2);
        appsView.setLayoutManager(layout);
        final List<AppModel> models = getAppModels();
        final RecyclerView.Adapter adapter = new AppsAdapter(models);
        appsView.setAdapter(adapter);
        final SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(appsView);
        //int count = (int) Math.ceil((float) models.size() / 6);
        //indicator.setCount(count);
        appsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final int offset = recyclerView.computeHorizontalScrollOffset();
                final int range = recyclerView.computeHorizontalScrollRange();
                final int extent = recyclerView.computeHorizontalScrollExtent();
                final float progress = (float) offset / (range - extent);
                //indicator.setProgress(progress);
            }
        });
        indicator.addOnProgressChangedListener(progress -> {
            final int offset = appsView.computeHorizontalScrollOffset();
            final int range = appsView.computeHorizontalScrollRange();
            final int extent = appsView.computeHorizontalScrollExtent();
            final int x = (int) (progress * (range - extent) - offset);
            appsView.scrollBy(x, 0);
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
        Log.d(this.getClass().getSimpleName(), "onDestroy()!!!!!!!!!!!!!!");
    }
}
