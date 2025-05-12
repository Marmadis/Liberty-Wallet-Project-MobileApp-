package com.frontend.libertywallet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.Adapter.RecommendationPagerAdapter;
import com.frontend.libertywallet.R;
import com.google.android.material.tabs.TabLayoutMediator;

public class RecommendationFragment extends Fragment {

    private Button btnForecast, btnRecommendations;
    private Button btnPersonalized, btnPopular, btnHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        return  view;
    }

}


