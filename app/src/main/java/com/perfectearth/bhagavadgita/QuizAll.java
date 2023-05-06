package com.perfectearth.bhagavadgita;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.perfectearth.bhagavadgita.Adapter.QuizAllAdapter;
import com.perfectearth.bhagavadgita.Adapter.ScoreAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.ItemScore;
import com.perfectearth.bhagavadgita.AdapterItem.QuizItemAll;
import com.perfectearth.bhagavadgita.Fragment.ProfileFragment;
import com.perfectearth.bhagavadgita.Fragment.ScoreFragment;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;
import com.perfectearth.bhagavadgita.Utilis.ZoomOutPageTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizAll extends AppCompatActivity {


    private Toolbar quizToolbar;
    private ImageButton quizShow, examShow;
    private SessionManager sessionManager;
    private View optionQuiz;
    private BottomNavigationView bottomNavQuiz;
    private Fragment activeFragment;
    private AppBarLayout appBarLayoutQuiz;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean isCollapsingToolbarAdded = true;
    private String url;
    private TextView firstNameQ,firstScoreQ,secondNameQ
            ,secondScoreQ,thirdNameQ,thirdScoreQ;
    private TextView firstNameE,firstScoreE,secondNameE
            ,secondScoreE,thirdNameE,thirdScoreE;

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
        url = getString(R.string.save_link);
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

        firstNameQ =findViewById(R.id.first_quiz_name);
        secondNameQ =findViewById(R.id.second_quiz_name);
        thirdNameQ =findViewById(R.id.third_quiz_name);
        firstScoreQ =findViewById(R.id.first_quiz_score);
        secondScoreQ =findViewById(R.id.second_quiz_score);
        thirdScoreQ =findViewById(R.id.third_quiz_score);

        firstNameE =findViewById(R.id.first_exam_name);
        secondNameE =findViewById(R.id.second_exam_name);
        thirdNameE =findViewById(R.id.third_exam_name);
        firstScoreE =findViewById(R.id.first_exam_score);
        secondScoreE =findViewById(R.id.second_exam_score);
        thirdScoreE =findViewById(R.id.third_exam_score);

        collapsingToolbarLayout = findViewById(R.id.collapsing_quiz);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.purple_500));
        collapsingToolbarLayout.setExpandedTitleMarginStart(getResources().getDimensionPixelSize(R.dimen.dp_10));
        collapsingToolbarLayout.setExpandedTitleMarginEnd(getResources().getDimensionPixelSize(R.dimen.dp_10));

        appBarLayoutQuiz = findViewById(R.id.app_bar_quiz);
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
        appBarLayoutQuiz.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();

                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    isCollapsingToolbarAdded = true;
                } else if (isShow) {
                    isShow = false;
                }
            }
        });
        setupBottomNavigation();
        showWinner();

    }
    private void showWinner() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray quizScores = jsonResponse.getJSONArray("winner_quiz");
                    JSONArray examScores = jsonResponse.getJSONArray("winner_exam");

                    for (int i = 0; i < Math.min(quizScores.length(), 3); i++) {
                        JSONObject score = quizScores.getJSONObject(i);
                        switch (i) {
                            case 0:
                                topScoreView(score, firstNameQ, firstScoreQ);
                                break;
                            case 1:
                                topScoreView(score, secondNameQ, secondScoreQ);
                                break;
                            case 2:
                                topScoreView(score, thirdNameQ, thirdScoreQ);
                                break;
                        }
                    }

                    for (int i = 0; i < Math.min(examScores.length(), 3); i++) {
                        JSONObject score = examScores.getJSONObject(i);
                        switch (i) {
                            case 0:
                                topScoreExam(score, firstNameE, firstScoreE);
                                break;
                            case 1:
                                topScoreExam(score, secondNameE, secondScoreE);
                                break;
                            case 2:
                                topScoreExam(score, thirdNameE, thirdScoreE);
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ContentValues", "error" + error.getMessage());
                Toast.makeText(QuizAll.this,"Wrong",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "get_scores");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add header to force 3G network
                Map<String, String> headers = new HashMap<>();
                headers.put("x-network-type", "3g");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void topScoreExam(JSONObject scoreObject, TextView nameView, TextView scoreView) throws JSONException {
        int score = Integer.parseInt(scoreObject.getString("user_score"));
        String name = scoreObject.getString("user_name");
        nameView.setText(name);
        String value = String.valueOf(score*5);
        String textScore = "Score\n" +value;
        SpannableString spannableString = new SpannableString(textScore);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), textScore.indexOf(value), textScore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = ContextCompat.getColor(this, R.color.teal_700);
        spannableString.setSpan(new ForegroundColorSpan(color), textScore.indexOf(value), textScore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        scoreView.setText(spannableString);
    }
    private void topScoreView(JSONObject scoreObject, TextView nameView, TextView scoreView) throws JSONException {
        int score = Integer.parseInt(scoreObject.getString("user_score"));
        String name = scoreObject.getString("user_name");
        nameView.setText(name);
        String value = String.valueOf(score*5);
        String textScore = "Score\n" +value;
        SpannableString spannableString = new SpannableString(textScore);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), textScore.indexOf(value), textScore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = ContextCompat.getColor(this, R.color.teal_700);
        spannableString.setSpan(new ForegroundColorSpan(color), textScore.indexOf(value), textScore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        scoreView.setText(spannableString);
    }
    private void switchToFragment1() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
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
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
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
                        if (isCollapsingToolbarAdded) {
                            collapsingToolbarLayout.setTitle("Exam & Quiz");
                            appBarLayoutQuiz.setExpanded(true);
                            isCollapsingToolbarAdded = true;
                        }
                        return true;
                    case R.id.menu_board:
                        if (optionQuiz.getVisibility() == View.VISIBLE) {
                            optionQuiz.setVisibility(View.GONE);
                        }
                        switchToFragment1();
                        collapsingToolbarLayout.setTitle("Leader Board");
                        if (isCollapsingToolbarAdded) {
                            appBarLayoutQuiz.setExpanded(false);
                            isCollapsingToolbarAdded = false;
                        }
                        return true;
                    case R.id.menu_profile:
                        if (optionQuiz.getVisibility() == View.VISIBLE) {
                            optionQuiz.setVisibility(View.GONE);
                        }
                        switchToFragment2();
                        collapsingToolbarLayout.setTitle("Profile");
                        if (isCollapsingToolbarAdded) {
                            appBarLayoutQuiz.setExpanded(false);
                            isCollapsingToolbarAdded = false;
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