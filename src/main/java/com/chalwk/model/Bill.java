package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Bill {
    private int id;
    private String name;
    private double amount;
    private String frequency;
    private String day;
    private String notes;
    private String paymentMethod;

    @JsonCreator
    public Bill(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("amount") double amount,
                @JsonProperty("frequency") String frequency,
                @JsonProperty("day") String day,
                @JsonProperty("notes") String notes,
                @JsonProperty("paymentMethod") String paymentMethod) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.day = day;
        this.notes = notes;
        this.paymentMethod = paymentMethod;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}