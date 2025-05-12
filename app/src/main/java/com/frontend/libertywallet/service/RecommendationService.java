package com.frontend.libertywallet.service;

import com.frontend.libertywallet.Entity.Recommendation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendationService {


    public interface RecommendationCallback {
        void onRecommendationLoaded( List<Recommendation> recommendationList);
    }
    public static List<Recommendation> getPopularRecommendation(String token){
        OkHttpClient client  = new OkHttpClient();
        List<Recommendation> recommendationList = new ArrayList<>();

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
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recObj = jsonArray.getJSONObject(i);
                        Recommendation recommendation = new Recommendation();
                        recommendation.setId(recObj.getString("id"));
                        recommendation.setText(recObj.getString("text"));
                        if(recObj.getString("image") != null){
                            recommendation.setImage(recObj.getString("image"));
                        }
                        recommendationList.add(recommendation);

                    }


                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();

        return recommendationList;
    }

    public static List<Recommendation> getPersonalizedRec(String userId,String token){
        List<Recommendation> recommendationList = new ArrayList<>();
        OkHttpClient client  = new OkHttpClient();
        String BASE_URL = "http://10.0.2.2:9090/recommendation/personalized/"+userId;
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
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recObj = jsonArray.getJSONObject(i);
                        Recommendation recommendation = new Recommendation();
                        recommendation.setId(recObj.getString("id"));
                        recommendation.setText(recObj.getString("text"));
                        if(recObj.getString("image") != null){
                            recommendation.setImage(recObj.getString("image"));
                        }
                        recommendationList.add(recommendation);

                    }


                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
        return recommendationList;
    }

    public static List<Recommendation> getLikedRec(String token,String userId){
        List<Recommendation> recommendationList = new ArrayList<>();
        OkHttpClient client  = new OkHttpClient();
        String BASE_URL = "http://10.0.2.2:9090/recommendation/liked_rec/"+userId;
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
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recObj = jsonArray.getJSONObject(i);
                        Recommendation recommendation = new Recommendation();
                        recommendation.setId(recObj.getString("id"));
                        recommendation.setText(recObj.getString("text"));
                        if(recObj.getString("image") != null){
                            recommendation.setImage(recObj.getString("image"));
                        }
                        recommendationList.add(recommendation);

                    }


                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
        return recommendationList;
    }

    public static List<Recommendation> getFavoriteRec(String userId,String token){
        OkHttpClient client  = new OkHttpClient();
        List<Recommendation> recommendationList = new ArrayList<>();
        String BASE_URL = "http://10.0.2.2:9090/recommendation/favorite_rec/"+userId;
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
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recObj = jsonArray.getJSONObject(i);
                        Recommendation recommendation = new Recommendation();
                        recommendation.setId(recObj.getString("id"));
                        recommendation.setText(recObj.getString("text"));
                        if(recObj.getString("image") != null){
                            recommendation.setImage(recObj.getString("image"));
                        }
                        recommendationList.add(recommendation);

                    }


                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
        return recommendationList;
    }


}
