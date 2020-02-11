package dev.yanshouwang.wonder.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.SeekBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CornerTreatment;
import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.TriangleEdgeTreatment;

import dev.yanshouwang.wonder.widget.CircleEdgeTreatment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.bnv);
        SeekBar slider = findViewById(R.id.slider);
        FloatingActionButton fab = findViewById(R.id.fab);

        Resources res = this.getResources();
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, res.getDisplayMetrics());
        float cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, res.getDisplayMetrics());
        float horizontalOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, res.getDisplayMetrics());
        float verticalOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, res.getDisplayMetrics());
        EdgeTreatment edge = new CircleEdgeTreatment(radius, cornerRadius, horizontalOffset, verticalOffset, true);
        ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                .setTopEdge(edge)
                .build();
        MaterialShapeDrawable drawable = new MaterialShapeDrawable();
        drawable.setShapeAppearanceModel(model);
        drawable.setPaintStyle(Paint.Style.FILL);
        //drawable.setElevation(elevation);
        float interpolation = (float) slider.getProgress() / (float) slider.getMax();
        drawable.setInterpolation(interpolation);
        int tint = ContextCompat.getColor(this, R.color.colorAccent);
        DrawableCompat.setTint(drawable, tint);
        ViewCompat.setBackground(view, drawable);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawable.setInterpolation((float) progress / (float) slider.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fab.setOnClickListener(v -> {
            ObjectAnimator animator = ObjectAnimator.ofFloat(drawable, "interpolation", 0f, 1f);
            animator.setInterpolator(new OvershootInterpolator());
            //animator.setDuration(3000);
            animator.addUpdateListener(animation -> {
                int progress = (int) ((float) animation.getAnimatedValue() * slider.getMax());
                slider.setProgress(progress);
            });
            animator.start();
        });
    }
}
