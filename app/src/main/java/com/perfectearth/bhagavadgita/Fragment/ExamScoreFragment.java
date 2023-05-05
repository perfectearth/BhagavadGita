package com.perfectearth.bhagavadgita.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExamScoreFragment extends Fragment {

    public ExamScoreFragment() {

    }
    private QuizAllAdapter examAllAdapter;
    private ArrayList<QuizItemAll> examItemAllArrayList;
    private RecyclerView examScoreRecycler;
    private String url ;
    private boolean checkNotify = true;
    private RelativeLayout animShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View examScoreQuiz = inflater.inflate(R.layout.fragment_exam_score, container, false);
        url = getString(R.string.exam_link);
        animShow = examScoreQuiz.findViewById(R.id.animation_exam_score);
        examScoreRecycler = examScoreQuiz.findViewById(R.id.recycler_exam_score);
        examScoreRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        examScoreRecycler.setNestedScrollingEnabled(false);
        examItemAllArrayList = new ArrayList<>();
        checkScoreExam();
        return examScoreQuiz;
    }
    private void checkScoreExam() {
        CustomProgress.showProgressBar(getContext(),false,"Please\nwait..");
        if (examItemAllArrayList!=null){
            examItemAllArrayList.clear();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArrayResult = new JSONArray(response);
                    String nameString = jsonArrayResult.getJSONObject(0).getString("name");
                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String total = jsonObject.getString("total");
                        String correct = jsonObject.getString("correct");
                        QuizItemAll quizItemAll = new QuizItemAll();
                        quizItemAll.setTotal(total);
                        quizItemAll.setScore(correct);
                        quizItemAll.setSerial("1");
                        quizItemAll.setName(name);
                        examItemAllArrayList.add(quizItemAll);
                        examAllAdapter = new QuizAllAdapter(getContext(), examItemAllArrayList);
                        examScoreRecycler.setAdapter(examAllAdapter);
                    }
                    if (examScoreRecycler.getVisibility()==View.GONE){
                        examScoreRecycler.setVisibility(View.VISIBLE);
                    }
                    if (animShow.getVisibility()==View.VISIBLE){
                        animShow.setVisibility(View.GONE);
                    }
                    CustomProgress.hideProgressBar();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (examScoreRecycler.getVisibility()==View.VISIBLE){
                        examScoreRecycler.setVisibility(View.GONE);
                    }
                    if (animShow.getVisibility()==View.GONE){
                        animShow.setVisibility(View.VISIBLE);
                    }
                    CustomProgress.hideProgressBar();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ContentValues", "error" + error.getMessage());
                if (examScoreRecycler.getVisibility()==View.VISIBLE){
                    examScoreRecycler.setVisibility(View.GONE);
                }
                if (animShow.getVisibility()==View.GONE){
                    animShow.setVisibility(View.VISIBLE);
                }
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

    @Override
    public void onResume() {
        super.onResume();
        if(examAllAdapter != null) {
            if (checkNotify){
                examAllAdapter.notifyDataSetChanged();
                checkNotify=false;
            }

        }
    }
}