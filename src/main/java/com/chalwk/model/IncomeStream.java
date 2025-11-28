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

        if ("one-off".equals(frequency) && amount == 0) {
            this.active = false;
        }
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
        if ("one-off".equals(this.frequency) && amount == 0) {
            this.active = false;
        }
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
        if ("one-off".equals(frequency)) {
            return amount > 0;
        }
        return active;
    }

    public void setActive(boolean active) {
        if ("one-off".equals(frequency)) {
            if (active && this.amount > 0) {
                this.active = true;
            } else if (!active) {
                this.active = false;
            }
        } else {
            this.active = active;
        }
    }

    public String getNotes() {
        return notes;
    }

    public double getWeeklyAmount() {
        if (!isActive()) return 0.0;

        return switch (frequency) {
            case "fortnightly" -> amount / 2;
            case "monthly" -> amount / 4.33;
            case "yearly" -> amount / 52;
            case "one-off" -> {
                if (amount > 0) {
                    yield amount;
                } else {
                    yield 0.0;
                }
            }
            default -> amount;
        };
    }
}