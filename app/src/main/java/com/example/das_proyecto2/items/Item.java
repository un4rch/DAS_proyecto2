package com.example.das_proyecto2.items;

import android.widget.Button;

public class Item {
    private String imageUrl;
    private Button button;
    private Integer likes;

    public Item(String imageUrl, Button pButton, Integer pLikes) {
        this.imageUrl = imageUrl;
        this.button = pButton;
        this.likes = pLikes;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Button getButton() {
        return this.button;
    }

    public Integer getLikes() {
        return this.likes;
    }
}

