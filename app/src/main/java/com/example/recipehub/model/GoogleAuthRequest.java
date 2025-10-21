package com.example.recipehub.model;

public class GoogleAuthRequest {
    private String idToken;

    public GoogleAuthRequest() {}

    public GoogleAuthRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}