package com.example.recipehub.model;

public class User {
    private int user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String about_user;
    private String avatar;
    private String avatar_public_id;
    private String role;

    public User() { }

    // Геттеры и сеттеры
    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAbout_user() { return about_user; }
    public void setAbout_user(String about_user) { this.about_user = about_user; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getAvatar_public_id() { return avatar_public_id; }
    public void setAvatar_public_id(String avatar_public_id) { this.avatar_public_id = avatar_public_id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}