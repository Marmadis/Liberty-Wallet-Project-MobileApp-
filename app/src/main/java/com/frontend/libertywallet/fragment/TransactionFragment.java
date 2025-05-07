package com.frontend.libertywallet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


import com.frontend.libertywallet.R;
import com.frontend.libertywallet.service.CategoryService;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransactionFragment extends Fragment {

    ImageButton back;


    private CategoryService categoryService;
    private SharedPreferences prefs;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();


    Button saveButton;

    Spinner categorySpinner;
    EditText dateEdit,amountEdit,nameEdit;


    TextView dateError,amountError,nameError;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dateEdit = view.findViewById(R.id.date_transaction_edit);
        amountEdit = view.findViewById(R.id.amount_transaction_edit);
        nameEdit = view.findViewById(R.id.name_edit);
        back = view.findViewById(R.id.back_btn_transactionf);
        saveButton = view.findViewById(R.id.save_transaction_button);
        categorySpinner = view.findViewById(R.id.category_spinner);
        prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        saveButton.setOnClickListener(v -> saveInformation());


        dateError  = view.findViewById(R.id.date_transaction_Error);
        amountError = view.findViewById(R.id.amount_transaction_Error);
        nameError = view.findViewById(R.id.name_transaction_Error);


        dateEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        dateEdit.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        categoryService = new CategoryService();
        String userId = prefs.getString("userId",null);
        String token = prefs.getString("access_token",null);
        updateCategory(categoryService.getCategory(userId,token));
    }


    private void updateCategory(Map<String,String> categoryMap){
        List<String> categories = new ArrayList<>();
        categories.add("Choose category");
        for(String name : categoryMap.values()){
            categories.add(name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, categories
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

    }
    private void saveInformation(){
        try {
            String name = nameEdit.getText().toString();
            String amount = amountEdit.getText().toString();
            String date = dateEdit.getText().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();


            String userId = prefs.getString("userId", null);
            String token = prefs.getString("access_token", null);
            Map<String, String> categoryMap = categoryService.getCategory(userId, token);

            String categoryId = categoryService.getKeyByValue(categoryMap, selectedCategory);
            String BASE_URL = "http://10.0.2.2:9090/transaction/create/" + categoryId + "/" + userId;


            if (saveControl(name, amount, date, selectedCategory)) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("amount", amount);
                    json.put("date", date);
                    json.put("description", name);
                    sendRequest(json.toString(), BASE_URL, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "JSON Error" + e, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(requireContext(), "Fields must be not empty" , Toast.LENGTH_SHORT).show();
        }
    }


    private void sendRequest(String json,String BASE_URL,String token){
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        new Thread(()-> {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Transaction added successfully", Toast.LENGTH_SHORT).show();
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
            }
        }).start();
    }
    private boolean saveControl(String name,String amount,String date,String selectedCategory){

        if(name.isEmpty() || amount.isEmpty() || date.isEmpty() ){
            showError(nameEdit,nameError,"Fields must not be empty");
            showError(amountEdit,amountError,"Fields must not be empty");
            showError(dateEdit,dateError,"Fields must not be empty");
            return false;
        }
        if(selectedCategory.equals("Choose category")){
            showError(nameEdit,nameError,"No categories found");
            showError(amountEdit,amountError,"No categories found");
            showError(dateEdit,dateError,"No categories found");
            return false;
        }

        try {
            int amountControl = Integer.parseInt(amount);
            if(amountControl <= 0){
                showError(amountEdit, amountError, "Incorrect amount");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(amountEdit, amountError, "Amount must be a number");
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
