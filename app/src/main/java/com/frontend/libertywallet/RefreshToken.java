package com.frontend.libertywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RefreshToken {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = "http://10.0.2.2:9090/auth/refresh"; // localhost для эмулятора

    private final SharedPreferences prefs;
    private final Context context;

    public RefreshToken(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public void refreshToken() {
        String refreshToken = prefs.getString("refresh_token", null);

        if (refreshToken == null) {
            Log.e("Refresh", "No refresh token found");
            return;
        }

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + refreshToken)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);

                    String newAccessToken = json.getString("token");
                    String newRefreshToken = json.getString("refreshToken");


                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("access_token", newAccessToken);
                    editor.putString("refresh_token", newRefreshToken);
                    editor.apply();

                    Log.d("Refresh", "Token updated");

                } else {
                    Log.e("Refresh", "Failed to refresh: " + response.code());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }


}
