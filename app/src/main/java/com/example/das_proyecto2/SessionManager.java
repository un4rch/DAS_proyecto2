package com.example.das_proyecto2;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String SESSION_PREF_NAME = "mySession";
    private static final String USERNAME = "";
    private static final String EMAIL = "";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SESSION_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String username, String email) {
        editor.putString(USERNAME, username);
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}

