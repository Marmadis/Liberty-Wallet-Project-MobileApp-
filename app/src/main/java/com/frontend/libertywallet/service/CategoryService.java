package com.frontend.libertywallet.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.frontend.libertywallet.Entity.Category;
import com.frontend.libertywallet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import  java.util.Map;
public class CategoryService {

    private SharedPreferences prefs;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();

    public Map<String,String> getCategory(String userId,String token){

        String BASE_URL = "http://10.0.2.2:9090/category/get/"+userId;
        Map<String,String> category = new HashMap<>();
        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        new Thread(() ->{
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);

                    JSONArray jsonArray = new JSONArray(responseBody);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject categoryObj = jsonArray.getJSONObject(i);

                        String id = String.valueOf(categoryObj.getInt("id"));
                        String name = categoryObj.getString("name");
                        category.put(id, name);
                    }

                }else {
                    return;
                }
                } catch (IOException e) {
                e.printStackTrace();
                return;
                } catch (JSONException e){
                e.printStackTrace();
                return;
                }

        }).start();


        return category;

    }


    public String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
