package com.frontend.libertywallet.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private SharedPreferences prefs;

    String userId;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();

    TextView transactionView,paymentView,currentBalanceText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionView = view.findViewById(R.id.transaction_button);
        paymentView = view.findViewById(R.id.payment_button);
        currentBalanceText = view.findViewById(R.id.balance_view);
        prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        transactionView.setOnClickListener(v -> openTransaction());
        paymentView.setOnClickListener(v -> openPayment());
    }


    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        String token  = prefs.getString("access_token",null);
        userId = prefs.getString("userId",null);
        String BASE_URL = "http://10.0.2.2:9090/budget/get/"+userId;
        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        new Thread(() -> {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    String current_balance = json.getString("current_balance");


                    requireActivity().runOnUiThread(() -> {
                        SharedPreferences prefs = requireActivity().getSharedPreferences("budget", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("current_balance", current_balance);
                        editor.apply();
                        currentBalanceText.setText("Your Balance \n" + current_balance + " ₸");
                    });
                }else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e){
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "IOException! ", Toast.LENGTH_SHORT).show();
                });
            } catch (JSONException e){
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "JSON Exception! ", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

    }



    private void openTransaction(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TransactionFragment())
                .addToBackStack(null)
                .commit();
    }

    private void openPayment(){

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PaymentFragment())
                .addToBackStack(null)
                .commit();
    }
}
