package com.frontend.libertywallet.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;

import android.widget.Button;

public class NotificationFragment extends Fragment {

    private Button btnRecommendations, btnForecast;
    private Button btnPersonalized, btnPopular, btnHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Верхние кнопки
        btnRecommendations = view.findViewById(R.id.btn_recommendations);
        btnForecast = view.findViewById(R.id.btn_spending_forecasts);

        // Нижние фильтры
        btnPersonalized = view.findViewById(R.id.btn_personalized);
        btnPopular = view.findViewById(R.id.btn_popular);
        btnHistory = view.findViewById(R.id.btn_history);

        // Обработчики
        btnRecommendations.setOnClickListener(v -> loadFragment(new PersonalizedFragment())); // default
        btnForecast.setOnClickListener(v -> loadFragment(new ForeCastFragment()));

        btnPersonalized.setOnClickListener(v -> loadFragment(new PersonalizedFragment()));
        btnPopular.setOnClickListener(v -> loadFragment(new PopularFragment()));
        btnHistory.setOnClickListener(v -> loadFragment(new HistoryFragment()));

        // По умолчанию показываем Personalized
        loadFragment(new PersonalizedFragment());

        return view;
    }

    private void loadFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.recommendation_content_container, fragment)
                .commit();
    }
}



