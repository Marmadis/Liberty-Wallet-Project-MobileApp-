package com.frontend.libertywallet.service;

import java.util.Map;

public interface CategoryCallback {
    void onCategoriesLoaded(Map<String, String> categories);
}