package com.example.ecommerceapp.models;

public class Category {
    private String label;


    public Category() {
    }

    public Category(String label, int total) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
