package com.example.recipehub.model;


import java.io.Serializable;

public class Step implements Serializable {
    private String description;
    private String image_url;
    private int step_number;

    public Step(String description, String image_url, int step_number) {
        this.description = description;
        this.image_url = image_url;
        this.step_number = step_number;
    }

    public String getDescription() { return description; }
    public String getImage_url() { return image_url; }
    public int getStep_number() { return step_number; }
}