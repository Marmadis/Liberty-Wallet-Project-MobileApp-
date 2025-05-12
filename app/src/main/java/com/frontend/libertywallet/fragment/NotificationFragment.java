package com.frontend.libertywallet.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;
public class NotificationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // По умолчанию показываем RecommendationFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.recommendation_content_container, new RecommendationFragment())
                .commit();

        return view;
    }



    private void openForeCast(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ForeCastFragment())
                .addToBackStack(null)
                .commit();
    }

    private void openHistory(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HistoryFragment())
                .addToBackStack(null)
                .commit();
    }
    private void openPernsonalized(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PersonalizedFragment())
                .addToBackStack(null)
                .commit();
    }
    private void openPopular(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PopularFragment())
                .addToBackStack(null)
                .commit();
    }

}
