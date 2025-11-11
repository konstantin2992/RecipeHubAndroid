package com.example.recipehub.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String name;
    private String quantity;
    private String unit;

    public Ingredient(String name, String quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}