package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private final int id;
    private final String number;
    private final double total;
    private final List<Payment> payments;

    @JsonCreator
    public Invoice(@JsonProperty("id") int id,
                   @JsonProperty("number") String number,
                   @JsonProperty("total") double total,
                   @JsonProperty("payments") List<Payment> payments) {
        this.id = id;
        this.number = number;
        this.total = total;
        this.payments = payments != null ? payments : new ArrayList<>();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public double getTotal() {
        return total;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    @JsonIgnore
    public double getBalance() {
        double totalPaid = payments.stream().mapToDouble(Payment::getAmount).sum();
        return total - totalPaid;
    }
}