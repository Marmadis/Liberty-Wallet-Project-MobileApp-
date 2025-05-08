package com.frontend.libertywallet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;
import com.frontend.libertywallet.service.CategoryService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoryFragment extends Fragment {

    ImageButton back;

    Spinner categoryType;
    EditText nameEdit;
    Button saveButton;
    TextView nameError;
    private SharedPreferences prefs;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryType = view.findViewById(R.id.category_type_spinner);
        nameError = view.findViewById(R.id.name_category_Error);
        nameEdit = view.findViewById(R.id.name_category_edit);
        back = view.findViewById(R.id.back_btn_category);
        saveButton = view.findViewById(R.id.save_category_button);
        prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        showCategory();
        saveButton.setOnClickListener(v -> saveInformation());


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }


    private void saveInformation(){
        if(saveControl(nameEdit)){

            try{

                String userId = prefs.getString("userId",null);
                String token = prefs.getString("token",null);
                String BASE_URL = "http://10.0.2.2:9090/category/create/"+userId;
                String name = nameEdit.getText().toString();
                String type = categoryType.getSelectedItem().toString();
                JSONObject body = new JSONObject();
                body.put("name",name);
                body.put("type",type);
                sendRequest(body.toString(),BASE_URL,token);
            } catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(requireContext(), "JSON Error" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void sendRequest(String body,String BASE_URL,String token){
        RequestBody requestBody = RequestBody.create(body,JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        new Thread(()->{
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
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

    private boolean saveControl(EditText nameEdit){

        String name = nameEdit.getText().toString();
        if(name.trim().isEmpty()){
            nameEdit.setBackgroundResource(R.drawable.shape_error);
            nameError.setText("Fields must be not empty!");
            nameError.setVisibility(View.VISIBLE);
            return  false;
        }
        return true;


    }

    private void showCategory(){

        String[] categories = {"Choose category type","INCOME","EXPENSE"};

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
        categoryType.setAdapter(adapter);
    }


}
