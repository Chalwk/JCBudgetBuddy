package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Payment {
    private String date;
    private double amount;

    @JsonCreator
    public Payment(@JsonProperty("date") String date,
                   @JsonProperty("amount") double amount) {
        this.date = date;
        this.amount = amount;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}