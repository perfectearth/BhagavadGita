package com.perfectearth.bhagavadgita.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.perfectearth.bhagavadgita.AdapterItem.QuizItemAll;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.OrdinalUtilis;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizScoreFragment extends Fragment {

    public QuizScoreFragment() {
        // Required empty public constructor
    }
    private QuizAllAdapter quizAllAdapter;
    private ArrayList<QuizItemAll> quizItemAllArrayList;
    private RecyclerView quizScoreRecycler;
    private String url , phone;
    private View cardFirst;

    private TextView firstWord,firstName,firstScore,secondWord,secondName,secondScore
            ,thirdWord,thirdName,thirdScore,myRankSerial,myRankDetails;
    private RelativeLayout animShow,quizScoreView;
    private boolean checkNotify = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View viewScoreQuiz = inflater.inflate(R.layout.fragment_quiz_score, container, false);
        url = getString(R.string.quiz_link);
        quizScoreRecycler = viewScoreQuiz.findViewById(R.id.recycler_quiz_score);
        SessionManager sessionManager = new SessionManager(getContext());
        phone = sessionManager.getPhone();

        firstName = viewScoreQuiz.findViewById(R.id.first_name);
        firstWord = viewScoreQuiz.findViewById(R.id.first_word);
        firstScore = viewScoreQuiz.findViewById(R.id.first_score);

        secondName = viewScoreQuiz.findViewById(R.id.second_name);
        secondWord = viewScoreQuiz.findViewById(R.id.second_word);
        secondScore = viewScoreQuiz.findViewById(R.id.second_score);

        thirdName = viewScoreQuiz.findViewById(R.id.third_name);
        thirdWord = viewScoreQuiz.findViewById(R.id.third_word);
        thirdScore = viewScoreQuiz.findViewById(R.id.third_score);
        animShow = viewScoreQuiz.findViewById(R.id.animation_quiz_score);
        quizScoreView = viewScoreQuiz.findViewById(R.id.quiz_score_view);
        cardFirst = viewScoreQuiz.findViewById(R.id.card_quiz_1st);
        myRankSerial = viewScoreQuiz.findViewById(R.id.my_rank_serial);
        myRankDetails = viewScoreQuiz.findViewById(R.id.my_rank_details);

        quizScoreRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        quizScoreRecycler.setNestedScrollingEnabled(false);
        quizItemAllArrayList = new ArrayList<>();
        checkScoreQuiz();
        return viewScoreQuiz;
    }

    private void topScoreView(JSONObject scoreObject, TextView nameView, TextView scoreView, TextView wordView) throws JSONException {
        int score = Integer.parseInt(scoreObject.getString("monthly_score"));
        String name = scoreObject.getString("name");
        nameView.setText(name);
        String value = String.valueOf(score*5);
        String textScore = "Score\n" +value;
        SpannableString spannableString = new SpannableString(textScore);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), textScore.indexOf(value), textScore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = ContextCompat.getColor(getContext(), R.color.teal_700);
        spannableString.setSpan(new ForegroundColorSpan(color), textScore.indexOf(value), textScore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        scoreView.setText(spannableString);

        char firstChar = name.charAt(0);
        String firstLetter = String.valueOf(firstChar);
        wordView.setText(firstLetter);
    }
    private void checkScoreQuiz() {

        CustomProgress.showProgressBar(getContext(),false,"wait..");
        if (quizItemAllArrayList!=null){
            quizItemAllArrayList.clear();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArrayResult = new JSONArray(response);
                    String monthlyScoreString = jsonArrayResult.getJSONObject(0).getString("monthly_score");
                    int matchingIndex;
                    for (int i = 3; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        String phoneC = jsonObject.getString("phone");
                        if (phone.equals(phoneC)) {
                            matchingIndex = i;
                            workRank(jsonArrayResult.getJSONObject(matchingIndex),matchingIndex,monthlyScoreString);
                            break;
                        }else {
                            if (cardFirst.getVisibility()==View.VISIBLE){
                                cardFirst.setVisibility(View.GONE);
                            }
                        }
                    }
                    for (int i = 3; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        String score = jsonObject.getString("monthly_score");
                        String name = jsonObject.getString("name");
                        QuizItemAll quizItemAll = new QuizItemAll();
                        quizItemAll.setSerial("4");
                        quizItemAll.setName(name);
                        quizItemAll.setScore(score);
                        quizItemAllArrayList.add(quizItemAll);
                        quizAllAdapter = new QuizAllAdapter(getContext(),quizItemAllArrayList);
                        quizScoreRecycler.setAdapter(quizAllAdapter);
                    }
                    if (quizScoreView.getVisibility()==View.GONE){
                        quizScoreView.setVisibility(View.VISIBLE);
                    }
                    if (animShow.getVisibility()==View.VISIBLE){
                        animShow.setVisibility(View.GONE);
                    }
                    topScoreView(jsonArrayResult.getJSONObject(0), firstName, firstScore, firstWord);
                    topScoreView(jsonArrayResult.getJSONObject(1), secondName, secondScore, secondWord);
                    topScoreView(jsonArrayResult.getJSONObject(2), thirdName, thirdScore, thirdWord);
                    CustomProgress.hideProgressBar();
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.hideProgressBar();
                    if (animShow.getVisibility()==View.GONE){
                        animShow.setVisibility(View.VISIBLE);
                    }
                    if (quizScoreView.getVisibility()==View.VISIBLE){
                        quizScoreView.setVisibility(View.GONE);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ContentValues", "error" + error.getMessage());
                CustomProgress.hideProgressBar();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "load_score");
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
//throws JSONException
    private void workRank(JSONObject jsonObject,int index,String firstRank) throws JSONException{
        if (cardFirst.getVisibility()==View.GONE){
            cardFirst.setVisibility(View.VISIBLE);
        }
        String scoreM = jsonObject.getString("monthly_score");
        String serial = OrdinalUtilis.getOrdinalSuffix(index+1);
        double rank_first = Double.parseDouble(firstRank);
        double my_rank = Double.parseDouble(scoreM);
        int percent = (int) (((double) my_rank / rank_first) * 100);
        myRankSerial.setText(serial);
        myRankDetails.setText("You are doing better then "+percent+"% of other player for this month");

    }

    @Override
    public void onResume() {
        super.onResume();
        if(quizAllAdapter != null) {
            if (checkNotify){
                quizAllAdapter.notifyDataSetChanged();
                checkNotify=false;
            }
        }
    }
}