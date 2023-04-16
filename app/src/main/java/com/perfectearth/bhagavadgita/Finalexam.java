package com.perfectearth.bhagavadgita;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.perfectearth.bhagavadgita.Adapter.ExamAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.Question;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Finalexam extends AppCompatActivity implements ExamAdapter.ItemOnClickListener {
    private ExamAdapter examAdapter;
    private RecyclerView examRecycler;
    private ArrayList<Question> examList;
    private ProgressBar progressExam;
    private TextView countExam,textNoExam, textDescription, textTimerExam,textToolbar;

    private TextView textCorrect,textWrong,textQuestion,textSelect,textTimer,textPercent,textResult;
    private LinearLayout animationExam;
    private View showExam,currentView,startExam,showResult;
    private long timeLeftInMillis ;
    private Toolbar processToolbar;
    private CountDownTimer countDownTimer;
    private int numQuizItems,totalSelection,totalCorrect,totalWrong;
    private String url,phone,version,set_version;
    private SessionManager sessionManager;
    private long timeInLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalexam);

        processToolbar = findViewById(R.id.exam_toolbar);
        setSupportActionBar(processToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.arrow_back_24, null);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressExam = findViewById(R.id.progress_exam);
        countExam = findViewById(R.id.progress_count_qus);
        textNoExam = findViewById(R.id.no_exam_text);
        animationExam = findViewById(R.id.animation_view_exam);
        showExam = findViewById(R.id.show_exam);
        currentView = findViewById(R.id.start_exam);
        startExam = findViewById(R.id.start_exam);
        textToolbar = findViewById(R.id.txt_title_exam);
        textToolbar.setText(getString(R.string.final_exam));
        showResult = findViewById(R.id.show_result);
        Button btnStartExam = findViewById(R.id.exam_start_btn);
        Button btnSubmitExam = findViewById(R.id.exam_submit_btn);
        textDescription = findViewById(R.id.exam_description);
        textTimerExam = findViewById(R.id.timer_exam);
        textCorrect =findViewById(R.id.ans_correct_exam);
        textWrong =findViewById(R.id.ans_wrong_exam);
        textQuestion = findViewById(R.id.total_exam_count);
        textSelect = findViewById(R.id.total_exam_done);
        textTimer = findViewById(R.id.total_exam_time);
        textPercent = findViewById(R.id.total_exam_percent);
        textResult = findViewById(R.id.exam_total_number);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, UserRegister.class);
            startActivity(intent);
            finish();
        }else {
            String name = sessionManager.getName();
            phone = sessionManager.getPhone();
        }
        url= getString(R.string.exam_link);
        examRecycler = findViewById(R.id.exam_recycler);
        examRecycler.setLayoutManager(new LinearLayoutManager(this));
        examList = new ArrayList<>();

        ImageButton closeDialogExam = (ImageButton) findViewById(R.id.close_dialog_exam);
        closeDialogExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnStartExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView == startExam) {
                    switchLayouts(showExam);
                    totalCorrect= 0;
                    totalSelection= 0;
                    totalWrong= 0;
                    textToolbar.setText(getString(R.string.exam_start));
                    submitResult(phone,set_version,"0","0","0","0","0");
                    startTimer();
                }

            }
        });

        btnSubmitExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView == showExam) {
                    switchLayouts(showResult);
                    countDownTimer.onFinish();
                    String total = String.valueOf(numQuizItems);
                    String select = String.valueOf(totalSelection);
                    String correct1 = String.valueOf(totalCorrect);
                    String wrong1 = String.valueOf(totalWrong);
                    String time = String.valueOf(Math.toIntExact(timeInLeft));
                    submitResult(phone,set_version,total,select,correct1,wrong1,time);
                    allResultSet();
                }
            }
        });
        checkExam();
        processToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void allResultSet() {
        textQuestion.setText(numQuizItems+" Que");
        textSelect.setText(totalSelection+" Que");
        textCorrect.setText("Correct\nAns "+totalCorrect);
        textWrong.setText("Wrong\nAns "+totalWrong);

        double percentageQ = 0;
        double percentageW = 0;
        double percentageTotal= 0;
        double resultTotal= 0;
        double checkMark= 0;

        checkMark = ((double) totalWrong / 4);
        checkMark = ((double) 100 / numQuizItems)*checkMark;
        percentageQ = ((double) totalCorrect / totalSelection) * 100;
        percentageW = ((double) totalWrong / totalSelection) * 100;
        percentageTotal = ((double) totalCorrect / numQuizItems) * 100-checkMark;
        resultTotal = ((double) totalCorrect / numQuizItems) * 100;

        int percentCorrect = (int) Math.round(percentageQ);
        int percentWrong = (int) Math.round(percentageW);
        int percentTotal = (int) Math.round(percentageTotal);
        int reTotal = (int) Math.round(resultTotal);

        TextView correct = findViewById(R.id.correct_percent);
        TextView wrong = findViewById(R.id.wrong_percent);
        correct.setText(percentCorrect+"%");
        wrong.setText(percentWrong+"%");

        ProgressBar correctBar = findViewById(R.id.progress_correct);
        ProgressBar wrongBar = findViewById(R.id.progress_wrong);

        correctBar.setMax(100);
        wrongBar.setMax(100);
        correctBar.setProgress(percentCorrect);
        wrongBar.setProgress(percentWrong);
        textPercent.setText(percentTotal+"% Passed");
        textResult.setText("Total Result: "+reTotal+" Mark");
        int minutes = (int) (timeInLeft / 1000) / 60;
        int seconds = (int) (timeInLeft / 1000) % 60;
        String time = String.format(Locale.getDefault(), "%02d : %02d", minutes,seconds);
        textTimer.setText(time+" sec");
        textToolbar.setText(getString(R.string.exam_score));
    }
    private void checkExam(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        version = jsonObject.getString("exam_version");
                        String total = jsonObject.getString("total");
                        timeInLeft = Long.valueOf(jsonObject.getString("time_left"));
                        String select = jsonObject.getString("select");
                        String correct = jsonObject.getString("correct");
                        String wrong = jsonObject.getString("wrong");
                        numQuizItems = Integer.parseInt(total);
                        totalCorrect = Integer.parseInt(correct);
                        totalSelection = Integer.parseInt(select);
                        totalWrong = Integer.parseInt(wrong);
                        getExam();

                    } else {
                        String error = jsonObject.getString("error");
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }}
             , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "check");
                params.put("phone", phone);
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
            // Add request to request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

    private void getExam() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArrayResult = new JSONArray(response);
                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        JSONArray examAllArray = jsonObject.getJSONArray("exam_all");
                        set_version = jsonObject.getString("examVersion");
                        if (set_version.equals(version)){
                            allResultSet();
                            switchLayouts(showResult);
                        }else {
                            String examHave = jsonObject.getString("exam");
                            String examDescription = jsonObject.getString("exam_description");
                            int timeHow = jsonObject.getInt("examTime");
                            numQuizItems = examAllArray.length(); //check how many question have
                            if (examHave.equals("false")){
                                for (int j = 0; j < examAllArray.length(); j++) {
                                    JSONObject examObject = examAllArray.getJSONObject(j);
                                    String question = examObject.getString("question");
                                    String answer = examObject.getString("ans");
                                    String aBtn = examObject.getString("exam_a");
                                    String bBtn = examObject.getString("exam_b");
                                    String cBtn = examObject.getString("exam_c");
                                    String dBtn = examObject.getString("exam_d");
                                    Question item = new Question();
                                    item.setAnsA(aBtn);
                                    item.setAnsB(bBtn);
                                    item.setAnsC(cBtn);
                                    item.setAnsD(dBtn);
                                    item.setQuestion(question);
                                    item.setCorrectAnswer(answer);
                                    examList.add(item);
                                    progressExam.setMax(numQuizItems);
                                    timeLeftInMillis = timeHow*1000;
                                    countExam.setText("0/"+numQuizItems);

                                }
                                examAdapter = new ExamAdapter(examList,Finalexam.this,Finalexam.this);
                                examRecycler.setAdapter(examAdapter);
                                CustomProgress.hideProgressBar();
                                if (startExam.getVisibility()==View.GONE){
                                    startExam.setVisibility(View.VISIBLE);
                                    textDescription.setText(examDescription);
                                }
                            }else {
                                CustomProgress.hideProgressBar();
                                if (animationExam.getVisibility() == View.GONE){
                                    animationExam.setVisibility(View.VISIBLE);
                                    AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                                    alphaAnimation.setDuration(1500);
                                    textNoExam.startAnimation(alphaAnimation);
                                }
                                if (startExam.getVisibility()==View.VISIBLE){
                                    startExam.setVisibility(View.GONE);
                                }
                            }


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.hideProgressBar();
                    if (startExam.getVisibility()==View.VISIBLE){
                        startExam.setVisibility(View.GONE);
                    }
                    if (animationExam.getVisibility() == View.GONE){
                        animationExam.setVisibility(View.VISIBLE);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(1500);
                        textNoExam.startAnimation(alphaAnimation);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgress.hideProgressBar();
                if (startExam.getVisibility()==View.VISIBLE){
                    startExam.setVisibility(View.GONE);
                }
                if (animationExam.getVisibility() == View.GONE){
                    animationExam.setVisibility(View.VISIBLE);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                    alphaAnimation.setDuration(1500);
                    textNoExam.startAnimation(alphaAnimation);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "load");
                params.put("phone", phone);
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

        // Add request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void submitResult(String phone, String exam_version,
                              String total, String select,
                              String correct , String wrong, String time) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("exam_version", exam_version);
                params.put("total", total);
                params.put("select", select);
                params.put("correct", correct);
                params.put("wrong", wrong);
                params.put("time_left", time);
                return params;
            }
        };

        // Add request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void switchLayouts(View newView) {
        if (currentView != newView) {
            newView.setVisibility(View.VISIBLE);
            int currentViewId = currentView.getId();
            int newViewId = newView.getId();
            int slideOut = currentViewId < newViewId ? R.anim.slide_out_left : R.anim.slide_out_right;
            int slideIn = currentViewId < newViewId ? R.anim.slide_in_right : R.anim.slide_in_left;
            Animation slideOutAnimation = AnimationUtils.loadAnimation(this, slideOut);
            Animation slideInAnimation = AnimationUtils.loadAnimation(this, slideIn);
            currentView.startAnimation(slideOutAnimation);
            newView.startAnimation(slideInAnimation);
            currentView.setVisibility(View.GONE);
            currentView = newView;
        }
    }


    @Override
    public void onItemClick(int correct) {
        int count = ++totalSelection;
        if (correct > 0) {
            ++totalCorrect;
        } else {
            ++totalWrong;
        }
        positionProgressValue(progressExam,countExam,count);
    }

    private void positionProgressValue(ProgressBar progressBar, TextView progressValueTextView, int progress) {
        Point maxSizePoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(maxSizePoint);
        int maxX = maxSizePoint.x;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                int position = progress * (progressBar.getWidth() - 2) / progressBar.getMax();
                int textViewX = position - progressValueTextView.getWidth() / 2;
                int finalX = (progressValueTextView.getWidth() + textViewX > maxX) ? maxX - progressValueTextView.getWidth() : textViewX;
                progressValueTextView.setTranslationX(finalX);
                progressValueTextView.setText(progress+"/"+numQuizItems);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (currentView==showExam){
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to leave this page?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Close the activity and navigate back
                            countDownTimer.onFinish();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton("No", null)
                    .show();
        }else {
            super.onBackPressed();
        }

    }

    private void startTimer() {

        Long startTime = System.currentTimeMillis();

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                timeInLeft = System.currentTimeMillis() - startTime;
                textTimerExam.setText("Exam\nFinished");
            }
        }.start();
    }

    private void updateCountdownText() {
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "Start Exam\n"+"%02d : %02d sec", minutes, seconds);
        textTimerExam.setText(timeLeftFormatted);
    }
}