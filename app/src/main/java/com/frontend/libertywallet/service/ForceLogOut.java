package com.frontend.libertywallet.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.frontend.libertywallet.LoginActivity;

public class ForceLogOut {
    public static void forceLogout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
