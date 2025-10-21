package com.example.recipehub.model;

public class RegisterRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String password;

    public RegisterRequest(String first, String last, String email, String password) {
        this.first_name = first;
        this.last_name = last;
        this.email = email;
        this.password = password;
    }
}