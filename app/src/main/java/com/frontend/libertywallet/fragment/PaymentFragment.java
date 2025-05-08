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

public class PaymentFragment extends Fragment {

    ImageButton back;
    Button saveButton;

    EditText nameEdit,dateEdit,mouthlyPaymentEdit,generalSumEdit,numberOfMonthsEdit;

    TextView  nameError,dateError,mouthlyError,generalSumError,numberOfMonthsError;

    private SharedPreferences prefs;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);

        back = view.findViewById(R.id.back_btn_payment);
        saveButton = view.findViewById(R.id.save_payment_button);

        nameEdit = view.findViewById(R.id.name_payment_edit);
        dateEdit = view.findViewById(R.id.date_payment_edit);
        mouthlyPaymentEdit = view.findViewById(R.id.monthlyPayment_edit);
        generalSumEdit = view.findViewById(R.id.generalSum_edit);
        numberOfMonthsEdit = view.findViewById(R.id.numberOfMounths_edit);

        nameError =  view.findViewById(R.id.name_payment_Error);
        dateError =  view.findViewById(R.id.date_payment_Error);
        mouthlyError =  view.findViewById(R.id.monthlyPayment_Error);
        generalSumError =  view.findViewById(R.id.generalSum_Error);
        numberOfMonthsError =  view.findViewById(R.id.numberOfMounths_Error);


        saveButton.setOnClickListener(v -> saveInformation());

        dateEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year1, month1, dayOfMonth) -> {

                        String formattedDate = String.format(Locale.US, "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        dateEdit.setText(formattedDate);
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



    private void saveInformation(){

        String name = nameEdit.getText().toString();
        String date = dateEdit.getText().toString();
        String generalSum = generalSumEdit.getText().toString();
        String numberOfMonths = numberOfMonthsEdit.getText().toString();
        String mouthlyPayment = mouthlyPaymentEdit.getText().toString();


        String userId = prefs.getString("userId",null);
        String token = prefs.getString("access_token",null);

        String BASE_URL = "http://10.0.2.2:9090/payment/create/"+userId;

        if(saveControl(name,date,generalSum,numberOfMonths,mouthlyPayment)){

            try{
                JSONObject json = new JSONObject();
                json.put("name",name);
                json.put("date",date);
                json.put("monthSum",mouthlyPayment);
                json.put("numberOfMonths",numberOfMonths);
                json.put("generalSum",generalSum);
                json.put("currentSum",generalSum);
                json.put("currentNumberOfMonths",0);
                sendRequest(json.toString(),BASE_URL,token);
            }catch (JSONException e){
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

    private boolean saveControl(String name,String date,String generalSum,String numberOfMonths,String mouthlyPayment){

        if(name.trim().isEmpty() || date.trim().isEmpty() || generalSum.trim().isEmpty() || numberOfMonths.trim().isEmpty()
        || mouthlyPayment.trim().isEmpty()){
            showError(nameEdit,nameError,"Fields must not be empty");
            showError(dateEdit,dateError,"Fields must not be empty");
            showError(mouthlyPaymentEdit,mouthlyError,"Fields must not be empty");
            showError(generalSumEdit,generalSumError,"Fields must not be empty");
            showError(numberOfMonthsEdit,numberOfMonthsError,"Fields must not be empty");
            return false;
        }
        try{
            int general = Integer.parseInt(generalSum);
            int number = Integer.parseInt(numberOfMonths);
            int payment = Integer.parseInt(mouthlyPayment);
            if(general <= 0 || number <= 0 || payment <= 0){
                showError(mouthlyPaymentEdit,mouthlyError,"Number must be more than 0");
                showError(generalSumEdit,generalSumError,"Number must be more than 0");
                showError(numberOfMonthsEdit,numberOfMonthsError,"Number must be more than 0");
               return false;
            }
            if(number > 60){
                showError(numberOfMonthsEdit,numberOfMonthsError,"Number too high");
                return  false;
            }
            if(payment > general || payment*2 > general){
                showError(mouthlyPaymentEdit,mouthlyError,"Incorrect data");
                showError(generalSumEdit,generalSumError,"Incorrect data");
                return false;
            }


        }catch (NumberFormatException e) {
            showError(mouthlyPaymentEdit,mouthlyError,"Amount must be a number");
            showError(generalSumEdit,generalSumError,"Amount must be a number");
            showError(numberOfMonthsEdit,numberOfMonthsError,"Amount must be a number");
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
