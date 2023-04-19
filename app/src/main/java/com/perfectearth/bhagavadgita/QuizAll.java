package com.perfectearth.bhagavadgita;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.perfectearth.bhagavadgita.Adapter.ScoreAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.ItemScore;
import com.perfectearth.bhagavadgita.Fragment.ProfileFragment;
import com.perfectearth.bhagavadgita.Fragment.ScoreFragment;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizAll extends AppCompatActivity {


    private Toolbar quizToolbar;
    private ImageButton quizShow,examShow;
    private SessionManager sessionManager;
    private View optionQuiz;
    private BottomNavigationView bottomNavQuiz;
    private Fragment activeFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizall);

        quizToolbar = (Toolbar) findViewById(R.id.quiz_toolbar);
        setSupportActionBar(quizToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.arrow_back_24, null);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        optionQuiz = findViewById(R.id.quiz_option);
        if (!sessionManager.isLoggedIn()) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, UserRegister.class);
            startActivity(intent);
            finish();
        }

        quizShow = findViewById(R.id.quiz_start);
        examShow = findViewById(R.id.exam_start);


        bottomNavQuiz = findViewById(R.id.bottom_navi_quiz);


        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_quiz);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.purple_500));
        collapsingToolbarLayout.setExpandedTitleMarginStart(getResources().getDimensionPixelSize(R.dimen.dp_10));
        collapsingToolbarLayout.setExpandedTitleMarginEnd(getResources().getDimensionPixelSize(R.dimen.dp_10));

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_quiz);
        quizShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startActivity = new Intent(QuizAll.this, Quizprocess.class);
                startActivity(startActivity);
            }
        });
        examShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startActivity = new Intent(QuizAll.this, Finalexam.class);
                startActivity(startActivity);
            }
        });

        quizToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();

                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                } else if(isShow) {
                    isShow = false;
                }
            }
        });
        setupBottomNavigation();

    }

    private void switchToFragment1() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment1 = fragmentManager.findFragmentByTag("fragment1");
        Fragment fragment2 = fragmentManager.findFragmentByTag("fragment2");
        if (fragment1 == null) {
            fragment1 = new ScoreFragment();
            transaction.add(R.id.overlay_quiz, fragment1, "fragment1");
        }
        if (fragment2 != null) {
            transaction.hide(fragment2);
        }
        transaction.show(fragment1);
        transaction.commit();
        activeFragment = fragment1;
    }

    private void switchToFragment2() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment1 = fragmentManager.findFragmentByTag("fragment1");
        Fragment fragment2 = fragmentManager.findFragmentByTag("fragment2");
        if (fragment2 == null) {
            fragment2 = new ProfileFragment();
            transaction.add(R.id.overlay_quiz, fragment2, "fragment2");
        }
        if (fragment1 != null) {
            transaction.hide(fragment1);
        }
        transaction.show(fragment2);
        transaction.commit();
        activeFragment = fragment2;
    }

    private void setupBottomNavigation() {
        bottomNavQuiz.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.quiz_home:
                        if (optionQuiz.getVisibility() == View.GONE) {
                            optionQuiz.setVisibility(View.VISIBLE);
                        }
                        if (activeFragment != null) {
                            getSupportFragmentManager().beginTransaction().remove(activeFragment).commit();
                            activeFragment = null;
                        }
                        return true;
                    case R.id.menu_board:
                        if (optionQuiz.getVisibility() == View.VISIBLE) {
                            optionQuiz.setVisibility(View.GONE);
                        }
                        switchToFragment1();
                        return true;
                    case R.id.menu_profile:
                        switchToFragment2();
                        if (optionQuiz.getVisibility() == View.VISIBLE) {
                            optionQuiz.setVisibility(View.GONE);
                        }
                        return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (activeFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(activeFragment).commit();
            activeFragment = null;
            if (optionQuiz.getVisibility() == View.GONE) {
                optionQuiz.setVisibility(View.VISIBLE);
            }
            bottomNavQuiz.setSelectedItemId(R.id.quiz_home);
        } else {
            super.onBackPressed();
        }
    }


}