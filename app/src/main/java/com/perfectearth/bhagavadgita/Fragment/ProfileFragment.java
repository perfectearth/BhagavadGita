package com.perfectearth.bhagavadgita.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.perfectearth.bhagavadgita.Adapter.QuizAllAdapter;
import com.perfectearth.bhagavadgita.Adapter.ScoreAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.ItemScore;
import com.perfectearth.bhagavadgita.AdapterItem.QuizItemAll;
import com.perfectearth.bhagavadgita.QuizAll;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.UserRegister;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class  ProfileFragment extends Fragment {

    public ProfileFragment() {
    }
    private SessionManager sessionManager;
    private ScoreAdapter scoreAdapter;
    private ArrayList<ItemScore> itemScoreList;
    private RecyclerView scoreRecycler;
    private String url , phone,name;
    private TextView profileDateText,profileDetails,textLevelProfile,textLevelProgress
            ,totalMyText,monthMyText,correctMyText,wrongMyText,textNameProfile
            ,textNumberProfile,textLetterProfile;
    private ProgressBar levelProgress;
    private int totalQuestions = 0, totalCorrect = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView =  inflater.inflate(R.layout.fragment_profile, container, false);
        sessionManager = new SessionManager(getContext());
        url = getString(R.string.quiz_link);
        phone = sessionManager.getPhone();
        name = sessionManager.getName();

        levelProgress = profileView.findViewById(R.id.progress_level);
        textNameProfile = profileView.findViewById(R.id.profile_name);
        textNumberProfile = profileView.findViewById(R.id.profile_number);
        textLetterProfile = profileView.findViewById(R.id.profile_letter);
        profileDetails = profileView.findViewById(R.id.profile_details);


        textLevelProfile = profileView.findViewById(R.id.profile_level);
        profileDateText = profileView.findViewById(R.id.profile_date);
        totalMyText = profileView.findViewById(R.id.my_total_score);
        monthMyText = profileView.findViewById(R.id.my_month_score);
        correctMyText = profileView.findViewById(R.id.my_correct_ans);
        wrongMyText = profileView.findViewById(R.id.my_wrong_ans);
        textLevelProgress = profileView.findViewById(R.id.level_text_progress);

        scoreRecycler = profileView.findViewById(R.id.recycler_score);
        scoreRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        scoreRecycler.setNestedScrollingEnabled(false);
        itemScoreList = new ArrayList<>();

        textNameProfile.setText(name);
        textNumberProfile.setText("Your number 0"+phone);
        char firstChar = name.charAt(0);
        String firstLetter = String.valueOf(firstChar);
        textLetterProfile.setText(firstLetter);
        return profileView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getData(phone);
    }

    private void getData(String phone) {
        if (itemScoreList!=null){
            itemScoreList.clear();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArrayResult = new JSONArray(response);
                    String totalScore= jsonArrayResult.getJSONObject(0).getString("total_score");
                    String monthlyScore = jsonArrayResult.getJSONObject(0).getString("monthly_score");
                    String playDate = jsonArrayResult.getJSONObject(0).getString("date_play");
                    profileData(totalScore,monthlyScore,playDate);
                    for (int i = 1; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        String scoreChapter= jsonObject.getString("chapter");
                        String scoreTotal = jsonObject.getString("total");
                        String scoreCorrect = jsonObject.getString("correct");
                        totalQuestions += jsonObject.getInt("total");
                        totalCorrect += jsonObject.getInt("correct");
                        correctMyText.setText(totalCorrect+"\nCorrect ans");
                        wrongMyText.setText(totalQuestions-totalCorrect+"\nWrong ans");
                        ItemScore item = new ItemScore();
                        item.setScoreChapter(scoreChapter);
                        item.setQuesTotal(scoreTotal);
                        item.setScoreCorrect(scoreCorrect);
                        itemScoreList.add(item);
                        scoreAdapter = new ScoreAdapter(getContext(),itemScoreList);
                        scoreRecycler.setAdapter(scoreAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "s "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("action","list_score");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void profileData(String totalScore, String monthlyScore, String playDate) {
        int correctAns = Integer.parseInt(totalScore);
        int level = (correctAns / 100) + 1;
        levelProgress.setProgress(correctAns % 100 == 0 ? 0 : correctAns % 100);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(playDate);
            String formattedDate = new SimpleDateFormat("dd MMM").format(date);
            profileDateText.setText("Last play " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            profileDateText.setText("Last play N/A");
        }

        int sum = 500 - correctAns * 5;
        String text = "Need " + sum + " Score for next level";
        profileDetails.setText(text);
        textLevelProgress.setText(correctAns % 100 + "%");

        float progressWidth = levelProgress.getWidth() * (correctAns % 100) / 108.0f;
        textLevelProgress.setX(levelProgress.getX() + progressWidth - 10.0f);
        textLevelProgress.setY(levelProgress.getY() + levelProgress.getHeight() - textLevelProgress.getHeight() - 2);

        monthMyText.setText(Integer.parseInt(monthlyScore) * 5 + "\nMonthly score");
        totalMyText.setText(correctAns * 5 + "\nTotal score");

        String levelText = level + "\nLevel";
        SpannableString spannableString = new SpannableString(levelText);
        spannableString.setSpan(new RelativeSizeSpan(0.4f), levelText.indexOf("Level"), levelText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textLevelProfile.setLineSpacing(-20f, 1f);
        textLevelProfile.setText(spannableString);


    }

}