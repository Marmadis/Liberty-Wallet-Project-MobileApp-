package com.frontend.libertywallet;


import com.frontend.libertywallet.fragment.HomeFragment;
import com.frontend.libertywallet.fragment.NotificationFragment;
import com.frontend.libertywallet.fragment.SettingsFragment;
import com.frontend.libertywallet.fragment.WalletFragment;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.wallet) {
                selectedFragment = new WalletFragment();
            } else if (itemId == R.id.notification) {
                selectedFragment = new NotificationFragment();
            } else if (itemId == R.id.settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Выбор экрана по умолчанию
        bottomNavigationView.setSelectedItemId(R.id.home);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        boolean forceLogout = prefs.getBoolean("forceLogout", false);

        if (forceLogout) {
            prefs.edit().clear().apply(); // Очищаем все токены

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public boolean checkerAuth () {
        return !TextUtils.isEmpty(prefs.getString("refresh_token", null)) &&
                !TextUtils.isEmpty(prefs.getString("access_token", null)) &&
                !TextUtils.isEmpty(prefs.getString("userId", null));
    }
}


