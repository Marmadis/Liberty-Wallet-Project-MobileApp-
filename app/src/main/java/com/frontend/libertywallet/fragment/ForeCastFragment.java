package com.frontend.libertywallet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.Entity.GraphItem;
import com.frontend.libertywallet.R;
import com.github.mikephil.charting.charts.BarChart;
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

public class ForeCastFragment extends Fragment {

    TextView forecastView;
    private OkHttpClient client = new OkHttpClient();
    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        barChart = view.findViewById(R.id.line_chart);
        barChart.getLegend().setEnabled(true);
        barChart.setExtraOffsets(5f, 10f, 5f, 5f);
        barChart.getDescription().setEnabled(false);

        forecastView = view.findViewById(R.id.forecast_view);

        fetchForecast();
        fetchSumAndData();
        return view;
    }

    private void fetchSumAndData() {
        if (!isAdded()) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("userId", null);
        String BASE_URL = "http://10.0.2.2:9090/statistics/get/alltime/" + userId;

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
                    List<GraphItem> result = new ArrayList<>();

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String sum = obj.getString("sum");
                        String dateStr = obj.getString("date");
                        Date date = inputFormat.parse(dateStr);
                        GraphItem item = new GraphItem();
                        item.setSum(sum);
                        item.setDate(date);
                        result.add(item);
                    }

                    Map<String, Float> monthlySum = new LinkedHashMap<>();
                    SimpleDateFormat sdfMonth = new SimpleDateFormat("MMMM", Locale.getDefault());

                    for (GraphItem item : result) {
                        String month = sdfMonth.format(item.getDate());
                        float sum = Float.parseFloat(item.getSum());
                        monthlySum.put(month, monthlySum.getOrDefault(month, 0f) + sum);
                    }

                    List<com.github.mikephil.charting.data.BarEntry> entries = new ArrayList<>();
                    List<String> monthLabels = new ArrayList<>();
                    int index = 0;

                    for (Map.Entry<String, Float> entry : monthlySum.entrySet()) {
                        entries.add(new com.github.mikephil.charting.data.BarEntry(index, entry.getValue()));
                        monthLabels.add(entry.getKey());
                        index++;
                    }

                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            com.github.mikephil.charting.data.BarDataSet dataSet =
                                    new com.github.mikephil.charting.data.BarDataSet(entries, "Monthly Total");
                            dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.cornflowerBlue));
                            dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                            com.github.mikephil.charting.data.BarData barData =
                                    new com.github.mikephil.charting.data.BarData(dataSet);
                            barData.setBarWidth(0.9f);


                            barChart.setData(barData);
                            barChart.setFitBars(true);
                            barChart.getDescription().setEnabled(false);

                            XAxis xAxis = barChart.getXAxis();
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

                            YAxis yAxisLeft = barChart.getAxisLeft();
                            yAxisLeft.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                            barChart.getAxisRight().setEnabled(false);

                            barChart.invalidate(); // Обновить график
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка при получении графика", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void fetchForecast() {
        if (!isAdded()) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("userId", null);
        String BASE_URL = "http://10.0.2.2:9090/predict/get/" + userId;

        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    double forecast = Double.parseDouble(responseBody);
                    int forecastEasy = (int) forecast;
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            forecastView.setText("Forecast for next month:"+forecastEasy+" ₸");
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка при получении графика", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }
}
