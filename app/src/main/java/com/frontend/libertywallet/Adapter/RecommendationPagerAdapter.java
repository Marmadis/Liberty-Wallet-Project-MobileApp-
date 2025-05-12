package com.frontend.libertywallet.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.frontend.libertywallet.fragment.HistoryFragment;
import com.frontend.libertywallet.fragment.PersonalizedFragment;
import com.frontend.libertywallet.fragment.PopularFragment;

public class RecommendationPagerAdapter extends FragmentStateAdapter {

    public RecommendationPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HistoryFragment();
            case 1: return new PopularFragment();
            case 2: return new PersonalizedFragment();
            default: return new HistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
