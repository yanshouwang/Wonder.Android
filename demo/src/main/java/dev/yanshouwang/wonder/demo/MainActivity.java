package dev.yanshouwang.wonder.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BottomNavigationView bnv = findViewById(R.id.bnv);
        //bnv.inflateMenu(R.menu.bnv_menu);

//        fab.setOnClickListener(v -> {
//            ObjectAnimator animator = ObjectAnimator.ofFloat(drawable, "interpolation", 0f, 1f);
//            animator.setInterpolator(new OvershootInterpolator());
//            animator.start();
//        });
    }
}
