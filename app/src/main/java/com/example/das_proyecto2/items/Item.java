package com.example.das_proyecto2.items;

public class Item {
    private String imageUrl;
    private Integer likes;

    public Item(String imageUrl, Integer likes) {
        this.imageUrl = imageUrl;
        this.likes = likes;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Integer getLikes() {
        return this.likes;
    }

    public void setLikes(int pLikes) {
        this.likes = pLikes;
    }
}