package com.perfectearth.bhagavadgita.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;
import com.perfectearth.bhagavadgita.Adapter.ChapterAdapter;
import com.perfectearth.bhagavadgita.AdapterItem.ChapterItem;
import com.perfectearth.bhagavadgita.AdapterItem.QuizItemAll;
import com.perfectearth.bhagavadgita.Quizprocess;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QuizScoreFragment extends Fragment {

    public QuizScoreFragment() {
        // Required empty public constructor
    }

    private String url , phone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View viewScoreQuiz = inflater.inflate(R.layout.fragment_quiz_score, container, false);
        url = getString(R.string.quiz_link);
        return viewScoreQuiz;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkScoreQuiz();
    }

    private void checkScoreQuiz() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArrayResult = new JSONArray(response);
                    for (int i = 0; i < jsonArrayResult.length(); i++) {
                        JSONObject jsonObject = jsonArrayResult.getJSONObject(i);
                        String phone = jsonObject.getString("phone");
                        String score = jsonObject.getString("monthly_score");
                        String name = jsonObject.getString("name");
                        QuizItemAll quizItemAll = new QuizItemAll();
                        quizItemAll.setName(name);
                        quizItemAll.setScore(score);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.hideProgressBar();
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ContentValues", "error" + error.getMessage());
                Toast.makeText(getContext(), "Check "+error, Toast.LENGTH_SHORT).show();
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
}