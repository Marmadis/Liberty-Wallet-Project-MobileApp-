package com.frontend.libertywallet.Entity;

public class PopularItem {
    private String title;
    private String text;

    public PopularItem(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
