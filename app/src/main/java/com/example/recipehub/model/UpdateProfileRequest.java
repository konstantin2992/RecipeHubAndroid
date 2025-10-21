package com.example.recipehub.model;

public class UpdateProfileRequest {
    private String first_name;
    private String last_name;
    private String about_user;

    public UpdateProfileRequest(String first_name, String last_name, String about_user) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.about_user = about_user;
    }

    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getAbout_user() { return about_user; }
}