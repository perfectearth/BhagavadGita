package com.perfectearth.bhagavadgita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class QuizAll extends AppCompatActivity {

    private Toolbar quizToolbar;
    private ImageButton quizShow,examShow;
    private SessionManager sessionManager;


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

        if (!sessionManager.isLoggedIn()) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, UserRegister.class);
            startActivity(intent);
            finish();
        }
        
        quizShow = findViewById(R.id.quiz_start);
        examShow = findViewById(R.id.exam_start);


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

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}