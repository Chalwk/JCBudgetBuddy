package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public record Invoice(int id, String number, double total, List<Payment> payments) {
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

    @JsonIgnore
    public double getBalance() {
        double totalPaid = payments.stream().mapToDouble(Payment::getAmount).sum();
        return total - totalPaid;
    }
}