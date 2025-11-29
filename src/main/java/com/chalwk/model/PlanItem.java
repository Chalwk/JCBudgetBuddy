// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanItem {
    private final int id;
    private final String name;
    private final String description;
    private final double targetAmount;
    private final double deposit;
    private final double weeklyPayment;

    @JsonCreator
    public PlanItem(@JsonProperty("id") int id,
                    @JsonProperty("name") String name,
                    @JsonProperty("description") String description,
                    @JsonProperty("targetAmount") double targetAmount,
                    @JsonProperty("deposit") double deposit,
                    @JsonProperty("weeklyPayment") double weeklyPayment) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.deposit = deposit;
        this.weeklyPayment = weeklyPayment;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getDeposit() {
        return deposit;
    }

    public double getWeeklyPayment() {
        return weeklyPayment;
    }

    public double getRequiredWeeklySavings() {
        return weeklyPayment;
    }

    public double getRemainingAmount() {
        return Math.max(0, targetAmount - deposit);
    }

    public int getCalculatedTimeframeWeeks() {
        if (weeklyPayment <= 0) return 0;
        double remaining = getRemainingAmount();
        return (int) Math.ceil(remaining / weeklyPayment);
    }

    public String getTimeframeDescription() {
        int weeks = getCalculatedTimeframeWeeks();
        return formatTimeframe(weeks);
    }

    public String getAffordabilityStatus(double availableWeeklyFunds) {
        if (weeklyPayment <= 0) {
            return "Invalid Payment Amount";
        }

        if (availableWeeklyFunds >= weeklyPayment) {
            return "Affordable";
        } else {
            return "Not Affordable";
        }
    }

    public String getDetailedAffordabilityInfo(double availableWeeklyFunds) {
        double shortfall = weeklyPayment - availableWeeklyFunds;

        if (availableWeeklyFunds >= weeklyPayment) {
            return String.format("You can afford this item. Required: $%.2f/wk, Available: $%.2f/wk",
                    weeklyPayment, availableWeeklyFunds);
        } else {
            return String.format("You cannot afford this item. Shortfall: $%.2f/wk (Required: $%.2f/wk, Available: $%.2f/wk)",
                    shortfall, weeklyPayment, availableWeeklyFunds);
        }
    }

    public String getPaymentBreakdown() {
        if (deposit > 0) {
            return String.format("$%.2f deposit + $%.2f/wk", deposit, weeklyPayment);
        } else {
            return String.format("$%.2f/wk", weeklyPayment);
        }
    }

    private String formatTimeframe(int weeks) {
        if (weeks >= 52) {
            int years = weeks / 52;
            int remainingWeeks = weeks % 52;
            if (remainingWeeks > 0) {
                return years + " years, " + remainingWeeks + " weeks";
            }
            return years + " years";
        } else if (weeks >= 4) {
            int months = weeks / 4;
            int remainingWeeks = weeks % 4;
            if (remainingWeeks > 0) {
                return months + " months, " + remainingWeeks + " weeks";
            }
            return months + " months";
        }
        return weeks + " weeks";
    }
}