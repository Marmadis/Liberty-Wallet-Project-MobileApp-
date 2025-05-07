package com.frontend.libertywallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEdit,emailEdit,passwordEdit,password_rptEdit;
    TextView nameError,passwordError,password_rptError,emailError;
    Button register_btn;
    ImageButton backToLogin;


    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();
    String BASE_URL = "http://10.0.2.2:9090/user/register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameEdit = findViewById(R.id.name_edit);
        emailEdit = findViewById(R.id.email_edit);
        passwordEdit = findViewById(R.id.password_edit);
        password_rptEdit = findViewById(R.id.password_again_edit);
        register_btn = findViewById(R.id.register_button);
        backToLogin = findViewById(R.id.back_btn);

        nameError = findViewById(R.id.nameError);
        emailError= findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);
        password_rptError = findViewById(R.id.passwordAgainError);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
        register_btn.setOnClickListener(v -> attemptRegister());
    }


    private void attemptRegister() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String password_rpt = password_rptEdit.getText().toString().trim();

        if(CheckingFields(password,password_rpt,name,email)){
            JSONObject json  = new JSONObject();
            try{
                json.put("username",name);
                json.put("email",email);
                json.put("password",password);
                sendRegisterRequest(json.toString());
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this,"Json error",Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void sendRegisterRequest(String  jsonBody){
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        });
                    } else {
                        Toast.makeText(this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error connection!", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

    }

    private boolean CheckingFields(String password,String password_rpt,String name,String email){
        String pattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$";

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || password_rpt.isEmpty()){
            showError(nameEdit,nameError,"Fields must not be empty");
            showError(emailEdit,emailError,"Fields must not be empty");
            showError(passwordEdit,passwordError,"Fields must not be empty");
            showError(password_rptEdit,password_rptError,"Fields must not be empty");
            return false;
        }

        if(password.length() < 8){
            showError(passwordEdit,passwordError,"Password must be longer than 8 characters");
            showError(password_rptEdit,password_rptError,"Password must be longer than 8 characters");
            return false;
        }
        if(!password.equals(password_rpt)){
            showError(password_rptEdit,password_rptError,"Passwords must match");
            return  false;
        }
        if(password.matches(pattern)){
            showError(passwordEdit,passwordError,"The password must contain capital letters, symbols and numbers.");
            showError(password_rptEdit,password_rptError,"The password must contain capital letters, symbols and numbers.");
            return false;
        }
        return true;
    }

    private void showError(EditText editText, TextView errorText, String message){
        editText.setBackgroundResource(R.drawable.shape_error);
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }
}