package com.perfectearth.bhagavadgita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perfectearth.bhagavadgita.Adapter.SliderAdapter;

public class Splash extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    private Button letsGetStarted;
    Animation animation;
    int currentPos;
    private SharedPreferences saveSplashPrefs;
    private boolean isSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //Hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);

        saveSplashPrefs = getSharedPreferences("saveSplashPrefs", MODE_PRIVATE);
        isSplash = saveSplashPrefs.getBoolean("isSplash", true);

        if (!isSplash) {
            startActivity(new Intent(Splash.this, MainActivity.class));
            finish();
        }

        //Call adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);

        letsGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = saveSplashPrefs.edit();
                editor.putBoolean("isSplash", false);
                editor.apply();
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            }
        });
    }

    public void skip(View view) {
        SharedPreferences.Editor editor = saveSplashPrefs.edit();
        editor.putBoolean("isSplash", false);
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    private void addDots(int position) {

        dots = new TextView[3];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.purple_500));
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;
            if (position == 0) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                if (letsGetStarted.getVisibility() == View.VISIBLE) {
                    letsGetStarted.setVisibility(View.INVISIBLE);
                    animation = AnimationUtils.loadAnimation(Splash.this, R.anim.slide_out_right);
                    letsGetStarted.setAnimation(animation);
                }
            } else {
                animation = AnimationUtils.loadAnimation(Splash.this, R.anim.slide_in_right);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}