package com.frontend.libertywallet.Entity;

import java.util.Date;

public class HistoryItem {
    private final String category;
    private final String date;
    private final String amount;

    public HistoryItem(String category, String date, String amount) {
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
}

