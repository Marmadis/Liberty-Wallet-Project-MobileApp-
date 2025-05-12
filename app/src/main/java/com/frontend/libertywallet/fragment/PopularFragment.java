package com.frontend.libertywallet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.libertywallet.Adapter.PopularAdapter;
import com.frontend.libertywallet.Entity.PopularItem;
import com.frontend.libertywallet.Entity.Recommendation;
import com.frontend.libertywallet.R;
import com.frontend.libertywallet.service.RecommendationService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PopularFragment extends Fragment {
    private RecyclerView recyclerView;
    private PopularAdapter adapter;
    private OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        recyclerView = view.findViewById(R.id.popular_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PopularAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fetchPopularData();

        return view;
    }


    private void fetchPopularData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String BASE_URL = "http://10.0.2.2:9090/recommendation/popular";
        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    JSONArray jsonArray = new JSONArray(body);
                    List<PopularItem> result = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String title = obj.getString("title");
                        String text = obj.getString("text");
                        result.add(new PopularItem(title, text));
                    }

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> adapter.updateData(result));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
