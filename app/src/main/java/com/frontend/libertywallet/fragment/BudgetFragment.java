package com.frontend.libertywallet.fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

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


        prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        back = view.findViewById(R.id.back_btn_budget);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }
}
