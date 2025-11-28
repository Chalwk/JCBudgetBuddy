package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class OneTimePayment {
    private final int id;
    private final String description;
    private final LocalDate paymentDate;
    private final String category;
    private final String notes;
    private double amount;

    @JsonCreator
    public OneTimePayment(@JsonProperty("id") int id,
                          @JsonProperty("description") String description,
                          @JsonProperty("amount") double amount,
                          @JsonProperty("paymentDate") LocalDate paymentDate,
                          @JsonProperty("category") String category,
                          @JsonProperty("notes") String notes) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.category = category;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }
}