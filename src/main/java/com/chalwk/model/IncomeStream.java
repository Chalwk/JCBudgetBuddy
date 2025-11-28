// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class IncomeStream {
    private final int id;
    private final String name;
    private final String frequency;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String notes;
    private double amount;
    private boolean active;

    @JsonCreator
    public IncomeStream(@JsonProperty("id") int id,
                        @JsonProperty("name") String name,
                        @JsonProperty("amount") double amount,
                        @JsonProperty("frequency") String frequency,
                        @JsonProperty("startDate") LocalDate startDate,
                        @JsonProperty("endDate") LocalDate endDate,
                        @JsonProperty("active") boolean active,
                        @JsonProperty("notes") String notes) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.notes = notes;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public double getWeeklyAmount() {
        if (!active) return 0.0;

        return switch (frequency) {
            case "fortnightly" -> amount / 2;
            case "monthly" -> amount / 4.33;
            case "yearly" -> amount / 52;
            default -> amount;
        };
    }
}