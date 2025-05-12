package com.frontend.libertywallet.Entity;

import java.util.UUID;

public class Recommendation {
    private String id;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private String title;

    public void setImage(String image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String image;

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    private String text;
}
