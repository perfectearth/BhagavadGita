package com.perfectearth.bhagavadgita.Utilis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name, String phone) {
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putBoolean("isLoggedIn", true);
        editor.commit();
    }
    public void createId(String id) {
        editor.putString("a_id",id);
        editor.commit();
    }


    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public String getName() {
        return sharedPreferences.getString("name", null);
    }
    public String getId() {
        return sharedPreferences.getString("a_id", null);
    }
    public String getPhone() {
        return sharedPreferences.getString("phone", null);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
