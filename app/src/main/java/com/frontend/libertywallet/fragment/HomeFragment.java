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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.libertywallet.Adapter.NotificationAdapter;
import com.frontend.libertywallet.Entity.NotificationItem;
import com.frontend.libertywallet.R;
import com.frontend.libertywallet.service.ForceLogOut;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private SharedPreferences prefs;

    String userId;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();

    TextView transactionView,paymentView,currentBalanceText,budgetView,categoryView;

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
        budgetView = view.findViewById(R.id.budget_add_btn);
        categoryView = view.findViewById(R.id.category_add_btn);
        prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);

        categoryView.setOnClickListener(v -> openCategory());
        budgetView.setOnClickListener(v -> openBudget());
        transactionView.setOnClickListener(v -> openTransaction());
        paymentView.setOnClickListener(v -> openPayment());



        RecyclerView recyclerView = view.findViewById(R.id.notification_list);
        List<NotificationItem> notifications = new ArrayList<>();
        NotificationAdapter adapter = new NotificationAdapter(notifications);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        updatePayment(adapter);

    }


    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }


    private void updatePayment( NotificationAdapter adapter){
        String token  = prefs.getString("access_token",null);
        userId = prefs.getString("userId",null);
        String BASE_URL = "http://10.0.2.2:9090/payment/get/"+userId;


        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        new Thread(() -> {
            try{
                Response response = client.newCall(request).execute();

                if(response.code() == 403){
                    ForceLogOut.forceLogout(getContext());
                }


                if(response.isSuccessful()){
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);

                    List<NotificationItem> notificationItems = new ArrayList<>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject categoryObj = jsonArray.getJSONObject(i);


                        String dateString = categoryObj.getString("date");
                        Date date = null;
                        try {
                            date = dateFormat.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        NotificationItem notificationItem = new NotificationItem(
                                categoryObj.getString("id"),
                                categoryObj.getString("name"),
                                date,
                                categoryObj.getString("monthSum"),
                                categoryObj.getString("numberOfMonths"),
                                categoryObj.getString("currentNumberOfMonths"),
                                categoryObj.getString("currentSum"),
                                categoryObj.getString("generalSum"),
                                categoryObj.getBoolean("completed")
                        );

                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        today.set(Calendar.MINUTE, 0);
                        today.set(Calendar.SECOND, 0);
                        today.set(Calendar.MILLISECOND, 0);

                        Calendar paymentDate = Calendar.getInstance();
                        paymentDate.setTime(date);
                        paymentDate.set(Calendar.HOUR_OF_DAY, 0);
                        paymentDate.set(Calendar.MINUTE, 0);
                        paymentDate.set(Calendar.SECOND, 0);
                        paymentDate.set(Calendar.MILLISECOND, 0);

                        if (today.equals(paymentDate)) {
                            notificationItems.add(notificationItem);
                        }


                        requireActivity().runOnUiThread(() -> {
                            adapter.setNotifications(notificationItems);

                        });
                    }
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

                if(response.code() == 403){
                    ForceLogOut.forceLogout(getContext());
                }


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



    public void openBudget(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BudgetFragment())
                .addToBackStack(null)
                .commit();
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

    private void openCategory(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CategoryFragment())
                .addToBackStack(null)
                .commit();
    }
}
