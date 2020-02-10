package dev.yanshouwang.wonder.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.material.shape.CornerTreatment;
import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

import dev.yanshouwang.wonder.widget.BottomNavigationViewTopEdgeTreatment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.bnv);
        Resources res = this.getResources();
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, res.getDisplayMetrics());
        float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, res.getDisplayMetrics());
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, res.getDisplayMetrics());
        float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, res.getDisplayMetrics());
        CornerTreatment corner = new RoundedCornerTreatment();
        EdgeTreatment edge = new BottomNavigationViewTopEdgeTreatment();
        ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                .setTopEdge(edge)
                .build();
        MaterialShapeDrawable drawable = new MaterialShapeDrawable();
        drawable.setShapeAppearanceModel(model);
        drawable.setPaintStyle(Paint.Style.FILL);
        //drawable.setElevation(elevation);
        drawable.setInterpolation(1f);
        int tint = ContextCompat.getColor(this, R.color.colorAccent);
        DrawableCompat.setTint(drawable, tint);
        ViewCompat.setBackground(view, drawable);

        SeekBar slider = findViewById(R.id.slider);
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
    }
}
