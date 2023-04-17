package com.perfectearth.bhagavadgita;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.perfectearth.bhagavadgita.Adapter.ChapterAdapter;
import com.perfectearth.bhagavadgita.Adapter.ExamAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.ChapterItem;
import com.perfectearth.bhagavadgita.AdapterItem.CommonQuiz;
import com.perfectearth.bhagavadgita.AdapterItem.Question;
import com.perfectearth.bhagavadgita.Utilis.ChapterAll;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Quizprocess extends AppCompatActivity implements View.OnClickListener,ChapterAdapter.OnItemClickListener  {

    private Button ansA , ansB , ansC , ansD ,nextQuestion;
    private ChapterAdapter quizAdapter;
    private RecyclerView quizRecycler;
    private ArrayList<ChapterItem> catalogList;
    private JSONArray arryQuestion;
    private View  currentView, showQuiz,showCatalog,rootView;
    private int index=0,thisQuestion=0,totalQuestion, correctAns=0,playChapter=0,wrongAns=0;
    private final static long INTERVAL = 1000;
    private final static long TIMEOUT = 30000;
    private int counValue = 0;
    private CountDownTimer mCountDown;
    private ProgressBar countProgress,timeProgress;
    private Toolbar processToolbar;
    private Button[] optionButtons = new Button[4];
    private TextView txtQuestionNum,countText,question_Text,categoryName;
    private LottieAnimationView animationView;
    private InterstitialAd mInterstitialAd;
    private String url , phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizprocess);

        processToolbar = findViewById(R.id.ptoolbar);
        setSupportActionBar(processToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.arrow_back_24, null);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentView = findViewById(R.id.show_catalog);
        showCatalog = findViewById(R.id.show_catalog);
        showQuiz = findViewById(R.id.show_quiz_in);

        SessionManager sessionManager = new SessionManager(this);
        phone = sessionManager.getPhone();
        url = getString(R.string.quiz_link);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //AdView mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
       // mAdView.loadAd(adRequest);
        ansA = findViewById(R.id.ans_btna);
        ansB = findViewById(R.id.ans_btnb);
        ansC = findViewById(R.id.ans_btnc);
        ansD = findViewById(R.id.ans_btnd);

        animationView =findViewById(R.id.animation_view_quiz);

        nextQuestion = findViewById(R.id.quiz_next);
        rootView = findViewById(android.R.id.content);

        countProgress = findViewById(R.id.progress_question);
        timeProgress = findViewById(R.id.progress_time);
        countText = findViewById(R.id.time_count);
        question_Text = findViewById(R.id.question_show_text);
        categoryName = findViewById(R.id.txt_title_quiz);
        txtQuestionNum= findViewById(R.id.question_text_id);

        timeProgress.setMax(30);

        Collections.shuffle(CommonQuiz.questionList);


        quizRecycler = findViewById(R.id.catalog_quiz_recycler);
        int columns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && getResources().getConfiguration().screenWidthDp < 600 ? 2 : 4;
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        quizRecycler.setLayoutManager(layoutManager);
        quizRecycler.setHasFixedSize(true);
        quizRecycler.setNestedScrollingEnabled(false);
        catalogList = new ArrayList<>();

        optionButtons[0] = ansA;
        optionButtons[1] = ansB;
        optionButtons[2] = ansC;
        optionButtons[3] = ansD;

        // Set the onClickListener for all option buttons.
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setOnClickListener(this);
        }
        processToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameButton = nextQuestion.getText().toString();
                if (nameButton.equals("Next Question")) {
                    showQuestion(++index);

                }else {
                    switchLayouts(showCatalog);
                    categoryName.setText(R.string.quiz_catalog);
                    mCountDown.cancel();
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(currentDate);

                    submitData(phone,formattedDate,correctAns,playChapter,totalQuestion,correctAns,wrongAns);
                    CustomProgress.showProgressBar(Quizprocess.this,false,"Wait...");
                    index=0;
                    thisQuestion=0;
                }
            }
        });

        CustomProgress.showProgressBar(this,false,"Wait..");
        getQuizFile();
    }

    private void interAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-8889566517679501/9227619100", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        if (mInterstitialAd != null) {
                         //   mInterstitialAd.show(Quizprocess.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (currentView == showCatalog) {
            super.onBackPressed();
        } else if (currentView == showQuiz){
            switchLayouts(showCatalog);
            categoryName.setText(R.string.quiz_catalog);
            mCountDown.cancel();
            index=0;
            thisQuestion=0;
        }else {
            super.onBackPressed();
        }
    }

    private void submitData(String phonea,String date,int cor , int playChapter,int QuesTotal, int correct,int wrong ) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgress.hideProgressBar();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgress.hideProgressBar();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "save_quiz");
                params.put("phone", phonea);
                params.put("total_score", String.valueOf(cor));
                params.put("play_date", String.valueOf(date));
                params.put("chapter", String.valueOf(playChapter));
                params.put("total", String.valueOf(QuesTotal));
                params.put("correct", String.valueOf(correct));
                params.put("wrong", String.valueOf(wrong));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getQuizFile() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArrayResult = new JSONArray(response);
                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        JSONArray quizAllArra = jsonObject.getJSONArray("quiz_all");
                        String chapterDetails = jsonObject.getString("chapter_details");
                        int numQuizItems = quizAllArra.length();
                        ChapterItem item = new ChapterItem();
                        item.setChapterName(chapterDetails);
                        item.setChapterCount(String.valueOf(numQuizItems+" টি কুইজ।"));
                        catalogList.add(item);
                        CustomProgress.hideProgressBar();
                        quizAdapter = new ChapterAdapter(Quizprocess.this,catalogList, Quizprocess.this);
                        quizRecycler.setAdapter(quizAdapter);
                        arryQuestion = new JSONArray(response.toString());
                        if (animationView.getVisibility() == View.VISIBLE){
                            animationView.setVisibility(View.GONE);
                            if (currentView.getVisibility() == View.GONE){
                                currentView.setVisibility(View.VISIBLE);
                            }else {
                                showQuiz.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.hideProgressBar();
                    if (animationView.getVisibility() == View.GONE){
                        animationView.setVisibility(View.VISIBLE);
                        if (currentView.getVisibility() == View.VISIBLE){
                            currentView.setVisibility(View.GONE);
                        }else {
                            showQuiz.setVisibility(View.GONE);
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ContentValues", "error" + error.getMessage());
                CustomProgress.hideProgressBar();
                if (animationView.getVisibility() == View.GONE){
                    animationView.setVisibility(View.VISIBLE);
                    if (currentView.getVisibility() == View.VISIBLE){
                        currentView.setVisibility(View.GONE);
                    }else {
                        showQuiz.setVisibility(View.GONE);
                    }
                }
                Snackbar.make(rootView, "Having trouble loading the quiz!", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getQuizFile();
                    }
                }).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onItemClick(String position) {
        loadQuestion(position);
        startQuiz();
        if (currentView == showCatalog) {
            switchLayouts(showQuiz);
            playChapter = Integer.parseInt(position);
            correctAns=0;
            wrongAns=0;
        }
    }

    private void startQuiz() {
        totalQuestion = CommonQuiz.questionList.size();
        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                counValue = (int) (millisUntilFinished / 1000); // Convert millis to seconds
                countText.setText(String.valueOf(counValue));
                timeProgress.setProgress(counValue);
            }
            @Override
            public void onFinish() {
                counValue = 0;
                showQuestion(++index);
                if (index == totalQuestion) {
                    mCountDown.cancel();
                    index = 0;
                    thisQuestion = 0;
                    if (currentView == showCatalog) {
                        // Do something
                    } else if (currentView == showQuiz) {
                        categoryName.setText(R.string.quiz_catalog);
                        switchLayouts(showCatalog);
                    } else {
                        finish();
                    }
                }
            }
        };
        showQuestion(index);
    }

    private void loadQuestion(String position) {
        if (CommonQuiz.questionList.size() > 0) {
            CommonQuiz.questionList.clear();
        }
        for (int i = 0; i < arryQuestion.length(); i++) {
            try {
                JSONObject jsonElement = arryQuestion.getJSONObject(i);
                if (jsonElement.getString("chapter_serial").equals(position)) {
                    if (jsonElement != null) {
                        JSONArray quizArray = jsonElement.getJSONArray("quiz_all");
                        for (int j = 0; j < quizArray.length(); j++) {
                            JSONObject quizObject = quizArray.getJSONObject(j);
                            Question question = new Question();
                            question.setAnsA(quizObject.getString("quiz_a"));
                            question.setAnsB(quizObject.getString("quiz_b"));
                            question.setAnsC(quizObject.getString("quiz_c"));
                            question.setAnsD(quizObject.getString("quiz_d"));
                            question.setQuestion(quizObject.getString("question"));
                            question.setCorrectAnswer(quizObject.getString("ans"));
                            CommonQuiz.questionList.add(question);
                            ChapterAll chapters = new ChapterAll();

                            List<String> chapterNames = chapters.getChapterNames();
                            int chapterNumber = Integer.parseInt(position);
                            if (chapterNumber >= 1 && chapterNumber <= chapterNames.size()) {
                                String chapterName = chapterNames.get(chapterNumber - 1);
                                categoryName.setText(chapterName);
                            }
                        }
                    } else {
                        System.out.println("Element not found.");
                    }
            }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        Collections.shuffle(CommonQuiz.questionList);
        }
    }
    private void showQuestion(int index) {
        countProgress.setMax(totalQuestion);
        thisQuestion++;
        if (index < totalQuestion) {
            txtQuestionNum.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            countProgress.setProgress(thisQuestion);
            counValue = 0;
            question_Text.setText(CommonQuiz.questionList.get(index).getQuestion());
            List<Button> buttonList = Arrays.asList(ansA, ansB, ansC, ansD);
            Collections.shuffle(buttonList);

            for (int i = 0; i < buttonList.size(); i++) {
                Button button = buttonList.get(i);
                String answer = "";
                switch (i) {
                    case 0:
                        answer = CommonQuiz.questionList.get(index).getAnsA();
                        break;
                    case 1:
                        answer = CommonQuiz.questionList.get(index).getAnsB();
                        break;
                    case 2:
                        answer = CommonQuiz.questionList.get(index).getAnsC();
                        break;
                    case 3:
                        answer = CommonQuiz.questionList.get(index).getAnsD();
                        break;
                }
                button.setText(answer);
            }

            if (thisQuestion==totalQuestion){
                nextQuestion.setText("Finish");
            }else {
                nextQuestion.setText(R.string.quiz_next);
            }
            enableButtons();
            mCountDown.start();
        } else {

            mCountDown.cancel();

        }

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
    private void enableButtons() {
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setEnabled(true);
            Drawable customButtonDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.button_select, null);
            optionButtons[i].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            optionButtons[i].setBackground(customButtonDrawable);

        }
    }

    @Override
    public void onClick(View v) {
        mCountDown.cancel();
        if (index < totalQuestion) {
            Button clickedButton = (Button) v;
            disableButtonsExcept(clickedButton);
        }
    }
    private void disableButtonsExcept(Button enabledButton) {
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i] != enabledButton) {
                optionButtons[i].setEnabled(false);
                optionButtons[i].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white1)));


            }
        }

        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isEnabled()) {
                if (optionButtons[i].getText().equals(CommonQuiz.questionList.get(index).getCorrectAnswer())) {
                    checkAnswer("Correct");
                    correctAns++;
                } else {
                    optionButtons[i].setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    wrongAns++;
                    checkAnswer("Wrong");
                }
            }
        }
    }

    private void checkAnswer(String answer) {
        Snackbar.make(rootView, answer + " Answer", Snackbar.LENGTH_LONG).show();
    }
}