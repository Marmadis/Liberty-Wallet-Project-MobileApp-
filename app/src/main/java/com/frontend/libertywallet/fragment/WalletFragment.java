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
import com.frontend.libertywallet.Entity.GraphItem;
import com.frontend.libertywallet.Entity.HistoryItem;
import com.frontend.libertywallet.Entity.PopularItem;
import com.frontend.libertywallet.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

    private LineChart lineChart;
    private RecyclerView historyList;
    private HistoryAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        lineChart = view.findViewById(R.id.line_chart);
        historyList = view.findViewById(R.id.history_list);

        adapter = new HistoryAdapter(new ArrayList<>());
        historyList.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList.setAdapter(adapter);

        fetchHistory();
        fetchChartAndHistory();

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












    private void fetchChartAndHistory() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("userId", null);

        String url = "http://10.0.2.2:9090/statistics/get/alltime/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) return;

                String body = response.body().string();
                JSONArray jsonArray = new JSONArray(body);

                List<GraphItem> graphItems = new ArrayList<>();

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String sum = obj.getString("sum");
                    String dateStr = obj.getString("date");
                    System.out.println(sum);
                    Date date = inputFormat.parse(dateStr);
                    String formattedDate = displayFormat.format(date);

                    GraphItem graphItem = new GraphItem();
                    graphItem.setSum(sum);
                    graphItem.setDate(date);
                    graphItems.add(graphItem);


                }

                Map<String, Float> monthlySum = new LinkedHashMap<>();
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());

                for (GraphItem item : graphItems) {
                    String month = monthFormat.format(item.getDate());
                    float sum = Float.parseFloat(item.getSum());
                    monthlySum.put(month, monthlySum.getOrDefault(month, 0f) + sum);
                }

                List<Entry> entries = new ArrayList<>();
                List<String> monthLabels = new ArrayList<>();
                int index = 0;

                for (Map.Entry<String, Float> entry : monthlySum.entrySet()) {
                    entries.add(new Entry(index, entry.getValue()));
                    monthLabels.add(entry.getKey());
                    index++;
                }

                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        LineDataSet dataSet = new LineDataSet(entries, "Monthly expenses");
                        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.cornflowerBlue));
                        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.cornflowerBlue));
                        LineData lineData = new LineData(dataSet);

                        lineChart.setData(lineData);
                        lineChart.getDescription().setEnabled(false);

                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setGranularity(1f);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                int i = (int) value;
                                return i >= 0 && i < monthLabels.size() ? monthLabels.get(i) : "";
                            }
                        });

                        YAxis yAxisLeft = lineChart.getAxisLeft();
                        yAxisLeft.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                        lineChart.getAxisRight().setEnabled(false);
                        lineChart.invalidate();

                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
