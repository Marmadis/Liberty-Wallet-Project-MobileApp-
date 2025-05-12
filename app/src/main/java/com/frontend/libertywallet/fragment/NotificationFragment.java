package com.frontend.libertywallet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;

public class NotificationFragment extends Fragment {
    private View filterSection;

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
        filterSection = view.findViewById(R.id.filter_section);
        // Нижние фильтры
        btnPersonalized = view.findViewById(R.id.btn_personalized);
        btnPopular = view.findViewById(R.id.btn_popular);
        btnHistory = view.findViewById(R.id.btn_history);

        // Обработчики верхних кнопок
        btnRecommendations.setOnClickListener(v -> {
            loadFragment(new PersonalizedFragment());
            updateTopButtonStyles(btnRecommendations);
            filterSection.setVisibility(View.VISIBLE);
            updateFilterStyles(btnPersonalized);
        });

        btnForecast.setOnClickListener(v -> {
            loadFragment(new ForeCastFragment());
            updateTopButtonStyles(btnForecast);
            filterSection.setVisibility(View.GONE);
        });

        // Обработчики нижних фильтров
        btnPersonalized.setOnClickListener(v -> {
            loadFragment(new PersonalizedFragment());
            updateFilterStyles(btnPersonalized);
        });

        btnPopular.setOnClickListener(v -> {
            loadFragment(new PopularFragment());
            updateFilterStyles(btnPopular);
        });

        btnHistory.setOnClickListener(v -> {
            loadFragment(new HistoryFragment());
            updateFilterStyles(btnHistory);
        });


        updateTopButtonStyles(btnRecommendations);
        updateFilterStyles(btnPersonalized);
        loadFragment(new PersonalizedFragment());

        return view;
    }

    private void loadFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.recommendation_content_container, fragment)
                .commit();
    }

    private void updateTopButtonStyles(Button activeButton) {
        int blue = ContextCompat.getColor(requireContext(), R.color.cornflowerBlue);
        int gray = ContextCompat.getColor(requireContext(), R.color.tundora);

        btnRecommendations.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), activeButton == btnRecommendations ? R.color.cornflowerBlue : R.color.tundora));
        btnForecast.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), activeButton == btnForecast ? R.color.cornflowerBlue : R.color.tundora));
    }

    //  Нижние кнопки: Personalized / Popular / History
    private void updateFilterStyles(Button activeButton) {
        int blue = ContextCompat.getColor(requireContext(), R.color.cornflowerBlue);
        int gray = ContextCompat.getColor(requireContext(), R.color.tundora);

        btnPersonalized.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), activeButton == btnPersonalized ? R.color.cornflowerBlue : R.color.tundora));
        btnPopular.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), activeButton == btnPopular ? R.color.cornflowerBlue : R.color.tundora));
        btnHistory.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), activeButton == btnHistory ? R.color.cornflowerBlue : R.color.tundora));
    }
}
