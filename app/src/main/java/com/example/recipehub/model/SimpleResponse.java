package com.example.recipehub.model;

public class SimpleResponse {
    private FavoriteItem favorite;
    private String message;
    private String avatar;
    private boolean success;


    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setFavorite(FavoriteItem favorite) {
        this.favorite = favorite;
    }

}