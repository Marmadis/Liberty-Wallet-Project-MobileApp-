package com.frontend.libertywallet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.libertywallet.Adapter.HistoryAdapter;
import com.frontend.libertywallet.Entity.HistoryItem;
import com.frontend.libertywallet.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WalletFragment extends Fragment {

    private PieChart pieChart;
    private RecyclerView historyList;
    private HistoryAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        pieChart = view.findViewById(R.id.pie_chart);
        historyList = view.findViewById(R.id.history_list);

        adapter = new HistoryAdapter(new ArrayList<>());
        historyList.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList.setAdapter(adapter);

        fetchHistory();
        fetchStatistics();

        return view;
    }

    private void fetchHistory() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("userId", null);

        String url = "http://10.0.2.2:9090/transaction/get/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    JSONArray jsonArray = new JSONArray(body);
                    List<HistoryItem> historyItems = new ArrayList<>();

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String category = obj.getString("description");
                        String amount = obj.getString("amount");
                        String rawDate = obj.getString("date");

                        Date parsedDate = inputFormat.parse(rawDate);
                        String formattedDate = outputFormat.format(parsedDate);

                        historyItems.add(new HistoryItem(category, formattedDate, "- " + amount + " ₸"));
                    }

                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            adapter.updateData(historyItems);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchStatistics() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("userId", null);

        String url = "http://10.0.2.2:9090/statistics/get/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) return;

                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                JSONObject expensePercents = json.getJSONObject("expensePercents");

                List<PieEntry> entries = new ArrayList<>();

                for (int i = 0; i < expensePercents.names().length(); i++) {
                    String key = expensePercents.names().getString(i);
                    float value = (float) expensePercents.getDouble(key) * 100;
                    entries.add(new PieEntry(value, key));
                }

                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        PieDataSet dataSet = new PieDataSet(entries, "Categories");
                        dataSet.setColors(new int[]{
                                R.color.blue,
                                R.color.orange,
                                R.color.red,
                                R.color.green,
                                R.color.purple,
                                R.color.yellow
                        }, requireContext());

                        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                        dataSet.setValueTextSize(12f);

                        PieData pieData = new PieData(dataSet);
                        pieChart.setData(pieData);
                        pieChart.setEntryLabelColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                        pieChart.setUsePercentValues(true);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.setCenterText("Expenses");
                        pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                        Legend legend = pieChart.getLegend();
                        legend.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                        legend.setTextSize(12f);

                        pieChart.invalidate();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
