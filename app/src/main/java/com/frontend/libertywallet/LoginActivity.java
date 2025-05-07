package com.frontend.libertywallet;

import static com.frontend.libertywallet.RegisterActivity.JSON;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText emailEdit,passwordEdit;

    TextView goToRegister;
    Button loginButton;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();
    String BASE_URL = "http://10.0.2.2:9090/auth/sign-in";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        goToRegister = findViewById(R.id.click_here_txt);
        loginButton = findViewById(R.id.login_button);

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(v -> attemptLogin());


    }

    private void attemptLogin(){

        if(emailEdit != null&&passwordEdit != null) {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            try {
                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("password", password);
                sendRequest(json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON Error" + e, Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this, "Fields must be not empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest(String jsonBody){

        RequestBody body = RequestBody.create(jsonBody,JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {

                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    String token = json.getString("token");
                    String refreshToken = json.getString("refreshToken");
                    String userId = json.getString("userId");

                    SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("access_token", token);
                    editor.putString("refresh_token", refreshToken);
                    editor.putString("userId", userId);
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e){
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "IOException! ", Toast.LENGTH_SHORT).show();
                });
            } catch (JSONException e){
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "JSON Exception! ", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}