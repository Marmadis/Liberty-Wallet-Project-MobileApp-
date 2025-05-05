package com.frontend.libertywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        if(!checkerAuth()){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

    }

    public boolean checkerAuth(){
        return !TextUtils.isEmpty(prefs.getString("refresh_token", null)) &&
                !TextUtils.isEmpty(prefs.getString("access_token", null)) &&
                !TextUtils.isEmpty(prefs.getString("userId", null));
    }


}