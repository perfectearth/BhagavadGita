package com.perfectearth.bhagavadgita.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.perfectearth.bhagavadgita.MainActivity;
import com.perfectearth.bhagavadgita.QuizAll;
import com.perfectearth.bhagavadgita.R;
import com.perfectearth.bhagavadgita.UserRegister;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PasswordFragment extends Fragment {
    public PasswordFragment() {}
    String userNumber="0";
    private ViewGroup parent;
    private LinearLayout showAnim;
    private CardView showPassword;
    private TextInputEditText passwordInput;
    private TextInputLayout inputLayout;
    private Button checkButton;
    private TextView massage;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        parent = view.findViewById(R.id.text_box);
        url= getString(R.string.register_link);
        showAnim = view.findViewById(R.id.animation_view_password);
        showPassword = view.findViewById(R.id.card_pass);
        passwordInput = view.findViewById(R.id.phone_forget);
        checkButton = view.findViewById(R.id.user_password_in);
        inputLayout= view.findViewById(R.id.forget_input);
        massage = view.findViewById(R.id.forget_msg);
        SessionManager sessionManager = new SessionManager(getContext());
        String a_id = sessionManager.getId();
        phoneUser(a_id);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getBtn = checkButton.getText().toString();
                if (getBtn.equals("Set Password")){
                    savePassword();
                }else {
                    checkData();
                }
            }
        });

        return view;
    }

    private void savePassword() {
        String password = passwordInput.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Please fill");
            passwordInput.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Minimum 6 digits required");
            passwordInput.requestFocus();
            return;
        }
        else {
            CustomProgress.showProgressBar(getContext(),false,"Update\nPassword");
            updatePass(userNumber,password);
            keyboard();
        }
    }

    private void checkData() {
        String password = passwordInput.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Please fill");
            passwordInput.requestFocus();
            return;
        }
        else if (!isValidPhoneNumber(password)) {
            passwordInput.setError("Invalid phone number");
            passwordInput.requestFocus();
            return;
        }
        if (userNumber.equals(password)){
            passwordInput.getText().clear();
            passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            Drawable icon = getResources().getDrawable(R.drawable.lock_icon);
            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
            inputLayout.setHintAnimationEnabled(true);
            inputLayout.setHint("Please Input new Password");
            checkButton.setText("Set Password");
            parent.setVisibility(View.GONE);
            massage.setText("Please set new password");
           keyboard();
        }else {
            Toast.makeText(getContext(), "Don't Match", Toast.LENGTH_SHORT).show();
            keyboard();
        }
    }

    private void keyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(passwordInput.getWindowToken(), 0);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^01[0-9]{9}$";
        return phoneNumber.matches(regex);
    }
    private void updatePass(String phone,String newPass) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgress.hideProgressBar();
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle login error
                CustomProgress.hideProgressBar();
                Toast.makeText(getContext(), "Password Update failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "pass_update");
                params.put("new_password", newPass);
                params.put("phone", phone);
                return params;
            }
        };

        // Add request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private void phoneUser(String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        String phone = jsonObject.getString("phone");
                        userNumber = "0"+phone;
                        textShow();
                    } else {
                        if (showAnim.getVisibility() == View.GONE) {
                            showAnim.setVisibility(View.VISIBLE);
                            showPassword.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle login error
                Toast.makeText(getContext(), "Data gets failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "id_request");
                params.put("user_id", id);
                return params;
            }
        };

        // Add request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private void textShow() {
        int length = userNumber.length();
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        int margin = 5;
        for (int i = 0; i < length; i++) {
            char c = userNumber.charAt(i);
            TextView textView = new TextView(getContext());
            if (i < length - 7 || i >= length - 2) {
                textView.setText(Character.toString(c));
            } else {
                textView.setText("*");
            }
            textView.setTextSize(20f);
            textView.setPadding(15, 5, 15, 5);
            float strokeWidth = 2f;
            int strokeColor = Color.BLACK;
            textView.setShadowLayer(strokeWidth, 0, 0, strokeColor);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setStroke((int) strokeWidth, strokeColor);
            shape.setCornerRadius(8f);
            textView.setBackground(shape);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(margin, margin, margin, margin);
            layoutParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(layoutParams);
            linearLayout.addView(textView);
        }
        parent.addView(linearLayout);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
