package com.perfectearth.bhagavadgita.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.perfectearth.bhagavadgita.Adapter.ScoreAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.ItemScore;
import com.perfectearth.bhagavadgita.QuizAll;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.UserRegister;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }
    private SessionManager sessionManager;
    private ScoreAdapter scoreAdapter;
    private ArrayList<ItemScore> itemScoreList;
    private RecyclerView scoreRecycler;
    private String url , phone;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView =  inflater.inflate(R.layout.fragment_profile, container, false);
        sessionManager = new SessionManager(getContext());
        url = getString(R.string.quiz_link);
        phone = sessionManager.getPhone();
        scoreRecycler = profileView.findViewById(R.id.recycler_score);
        scoreRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        scoreRecycler.setNestedScrollingEnabled(false);
        itemScoreList = new ArrayList<>();
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
                    for (int i = 1; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        String scoreChapter= jsonObject.getString("chapter");
                        String scoreTotal = jsonObject.getString("total");
                        String scoreCorrect = jsonObject.getString("correct");
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
}