package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private int id;
    private String number;
    private double total;
    private List<Payment> payments;

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

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    @JsonIgnore
    public double getBalance() {
        double totalPaid = payments.stream().mapToDouble(Payment::getAmount).sum();
        return total - totalPaid;
    }
}