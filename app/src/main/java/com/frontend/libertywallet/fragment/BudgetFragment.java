package com.frontend.libertywallet.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;
import com.frontend.libertywallet.service.ForceLogOut;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BudgetFragment extends Fragment {

    ImageButton back;
    private SharedPreferences prefs;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();
    Button saveButton;
    TextView amountError,balanceError,startDateError,endDateError;

    EditText amountEdit,balanceEdit,startDateEdit,endDateEdit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        amountEdit = view.findViewById(R.id.limit_amount_edit);
        amountError = view.findViewById(R.id.limit_amount_Error);
        balanceEdit = view.findViewById(R.id.current_balance_edit);
        balanceError = view.findViewById(R.id.current_balance_Error);
        startDateEdit = view.findViewById(R.id.start_date_edit);
        startDateError = view.findViewById(R.id.start_date_Error);
        endDateError = view.findViewById(R.id.end_date_Error);
        endDateEdit = view.findViewById(R.id.end_date_edit);

        prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        back = view.findViewById(R.id.back_btn_budget);
        saveButton = view.findViewById(R.id.save_budget_button);

        startDateEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        // ISO формат: yyyy-MM-dd
                        String formattedDate = String.format(Locale.US, "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        startDateEdit.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });


        endDateEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year1, month1, dayOfMonth) -> {

                        String formattedDate = String.format(Locale.US, "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        endDateEdit.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        saveButton.setOnClickListener(v -> saveInformation());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void saveInformation(){

        if(saveControl(amountEdit,balanceEdit,startDateEdit,endDateEdit)){
            try {
                String amount = amountEdit.getText().toString();
                String balance = balanceEdit.getText().toString();
                String startDate = startDateEdit.getText().toString();
                String endDate = endDateEdit.getText().toString();
                String userId = prefs.getString("userId",null);
                String token = prefs.getString("access_token",null);
                String BASE_URL = "http://10.0.2.2:9090/budget/create/"+userId;
                JSONObject json = new JSONObject();
                json.put("amountLimit",amount);
                json.put("start_date",startDate);
                json.put("end_date",endDate);
                json.put("current_balance",balance);
                sendRequest(json.toString(),BASE_URL,token);
            } catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(requireContext(), "JSON Error" + e, Toast.LENGTH_SHORT).show();
            }
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

                if(response.code() == 403){
                    ForceLogOut.forceLogout(getContext());
                }

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Budget added successfully", Toast.LENGTH_SHORT).show();
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

    private boolean saveControl(EditText amountEdit,EditText balanceEdit,EditText startDateEdit,EditText endDateEdit){
        String amount = amountEdit.getText().toString();
        String balance = balanceEdit.getText().toString();
        String startDate = startDateEdit.getText().toString();
        String endDate = endDateEdit.getText().toString();
        if(amount.trim().isEmpty() || balance.trim().isEmpty() || startDate.trim().isEmpty() || endDate.trim().isEmpty()){
            showError(amountEdit, amountError, "Fields must be not empty");
            showError(startDateEdit, startDateError, "Fields must be not empty");
            showError(endDateEdit, endDateError, "Fields must be not empty");
            showError(balanceEdit, balanceError, "Fields must be not empty");

            return false;
        }
        try {
            int amountControl = Integer.parseInt(amount);
            int balanceControl = Integer.parseInt(balance);
            if(balanceControl <= 0){
                showError(amountEdit, amountError, "Incorrect balance");
                return false;
            }
            if(amountControl <= 0 ){
                showError(amountEdit, amountError, "Incorrect amount");
                return false;
            }
            if(balanceControl < amountControl){
                showError(balanceEdit, balanceError, "Incorrect balance");
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
