package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Bill {
    private final int id;
    private final String name;
    private double amount;
    private final String frequency;
    private final String day;
    private final String notes;
    private final String paymentMethod;

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public String getDay() {
        return day;
    }

    public String getNotes() {
        return notes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}