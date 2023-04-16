package com.perfectearth.bhagavadgita;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.perfectearth.bhagavadgita.Fragment.PasswordFragment;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserRegister extends AppCompatActivity {
    private View currentView,login_view,register_view;
    private LinearLayout loginSingBtn;

    private TextInputEditText upTextName ,upTextPhone,
            upTextPassword,inTextPhone,inTextPassword;
    private Button loginBtn,registerBtn,userSingInBtn,userSingUpBtn;
    private TextView btnTextLogin,btnTextRegister,btnTextPassword;
    private SessionManager sessionManager;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        currentView = findViewById(R.id.login_register_layout);
        login_view = findViewById(R.id.include_login);
        register_view = findViewById(R.id.include_sing_up);
        loginSingBtn = findViewById(R.id.login_register_layout);

        upTextName = findViewById(R.id.name_sing_up);
        upTextPhone = findViewById(R.id.phone_sing_up);
        upTextPassword = findViewById(R.id.password_sing_up);

        inTextPhone = findViewById(R.id.phone_sing_in);
        inTextPassword = findViewById(R.id.password_sing_in);

        btnTextLogin = findViewById(R.id.text_btn_sing_in);
        btnTextRegister = findViewById(R.id.text_btn_register);
        btnTextPassword =findViewById(R.id.password_forget);
        loginBtn = findViewById(R.id.btn_login);
        registerBtn = findViewById(R.id.btn_register);
        userSingInBtn = findViewById(R.id.user_sing_in);
        userSingUpBtn = findViewById(R.id.user_sing_up);
        TextView textDay = findViewById(R.id.text_day);
        url= getString(R.string.register_link);
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 6 && timeOfDay < 12) {
            greeting = "Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            greeting = "Afternoon";}
        else if (timeOfDay >= 18 && timeOfDay < 20) {
            greeting = "Evening";
        } else {
            greeting = "Night";
        }
        textDay.setText(greeting);

        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(UserRegister.this, QuizAll.class);
            startActivity(intent);
            finish();
        } else {
          if (loginSingBtn.getVisibility() == View.GONE) {
              loginSingBtn.setVisibility(View.VISIBLE);
          }
          String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
          sessionManager.createId(androidId);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSingBtn.setVisibility(View.GONE);
                switchLayouts(login_view);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSingBtn.setVisibility(View.GONE);
                switchLayouts(register_view);
            }
        });
        btnTextLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchLayouts(login_view);
            }
        });
        btnTextRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchLayouts(register_view);
            }
        });
        btnTextPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.fragment_password, new PasswordFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                login_view.setVisibility(View.GONE);
            }
        });

        userSingUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCollectData();
            }
        });

        userSingInBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userSinIn();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_password);
        if (fragment instanceof PasswordFragment) {
            getSupportFragmentManager().popBackStack();
            login_view.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void userSinIn() {
        String phone = inTextPhone.getText().toString().trim();
        String password = inTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
           inTextPhone.setError("Please fill");
           inTextPhone.requestFocus();
        }
        if (!isValidPhoneNumber(phone)) {
            inTextPhone.setError("Invalid phone number");
            inTextPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inTextPassword.setError("Please fill");
            inTextPassword.requestFocus();
        }
        CustomProgress.showProgressBar(this,false,"Please\nWait....");
        loginUser(phone, password);
    }
    private void userCollectData() {
        String name = upTextName.getText().toString().trim();
        String phone = upTextPhone.getText().toString().trim();
        String password = upTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            upTextName.setError("Please fill");
            upTextName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            upTextPhone.setError("Please fill");
            upTextPhone.requestFocus();
            return;
        }
        else if (!isValidPhoneNumber(phone)) {
            upTextPhone.setError("Invalid phone number");
            upTextPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            upTextPassword.setError("Please fill");
            upTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            upTextPassword.setError("Minimum 6 digits required");
            upTextPassword.requestFocus();
            return;
        }
        String a_id = sessionManager.getId();
        CustomProgress.showProgressBar(this,false,"Please\nWait....");
        registerUser(name, phone, password,a_id);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^01[0-9]{9}$";
        return phoneNumber.matches(regex);
    }

    private void loginUser(String phone, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        String name = jsonObject.getString("name");
                        String phone = jsonObject.getString("phone");
                        sessionManager.createSession(name, phone);
                        CustomProgress.hideProgressBar();
                        Intent intent = new Intent(UserRegister.this, QuizAll.class);
                        startActivity(intent);
                        finish();
                    } else {
                        CustomProgress.hideProgressBar();
                        String error = jsonObject.getString("error");
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle login error
                CustomProgress.hideProgressBar();
                Toast.makeText(getApplicationContext(), "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "login");
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };

        // Add request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void registerUser(String name,String phone, String password,String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("Registration Success")) {
                    CustomProgress.hideProgressBar();
                    upTextName.getText().clear();
                    upTextPhone.getText().clear();
                    upTextPassword.getText().clear();
                    Toast.makeText(getApplicationContext(), "Registration Success", Toast.LENGTH_LONG).show();
                    switchLayouts(login_view);
                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle registration error
                CustomProgress.hideProgressBar();
                Toast.makeText(getApplicationContext(), "Registration failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to request
                Map<String, String> params = new HashMap<>();
                params.put("action", "register");
                params.put("user_id", id);
                params.put("name", name);
                params.put("phone", phone);
                params.put("password", password);
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
}