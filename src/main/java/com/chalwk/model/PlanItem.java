// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanItem {
    private final int id;
    private final String name;
    private final String description;
    private final double targetAmount;
    private final double currentSavings;
    private final String timeframeUnit;
    private final int timeframeValue;
    private final LocalDate targetDate;
    private final boolean useHirePurchase;
    private final double hirePurchaseDeposit;
    private final double hirePurchaseInterest;
    private final int hirePurchaseMonths;

    @JsonCreator
    public PlanItem(@JsonProperty("id") int id,
                    @JsonProperty("name") String name,
                    @JsonProperty("description") String description,
                    @JsonProperty("targetAmount") double targetAmount,
                    @JsonProperty("currentSavings") double currentSavings,
                    @JsonProperty("timeframeUnit") String timeframeUnit,
                    @JsonProperty("timeframeValue") int timeframeValue,
                    @JsonProperty("targetDate") LocalDate targetDate,
                    @JsonProperty("useHirePurchase") boolean useHirePurchase,
                    @JsonProperty("hirePurchaseDeposit") double hirePurchaseDeposit,
                    @JsonProperty("hirePurchaseInterest") double hirePurchaseInterest,
                    @JsonProperty("hirePurchaseMonths") int hirePurchaseMonths) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentSavings = currentSavings;
        this.timeframeUnit = timeframeUnit;
        this.timeframeValue = timeframeValue;
        this.targetDate = targetDate;
        this.useHirePurchase = useHirePurchase;
        this.hirePurchaseDeposit = hirePurchaseDeposit;
        this.hirePurchaseInterest = hirePurchaseInterest;
        this.hirePurchaseMonths = hirePurchaseMonths;
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

    public double getCurrentSavings() {
        return currentSavings;
    }

    public String getTimeframeUnit() {
        return timeframeUnit;
    }

    public int getTimeframeValue() {
        return timeframeValue;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public boolean isUseHirePurchase() {
        return useHirePurchase;
    }

    public double getHirePurchaseDeposit() {
        return hirePurchaseDeposit;
    }

    public double getHirePurchaseInterest() {
        return hirePurchaseInterest;
    }

    public int getHirePurchaseMonths() {
        return hirePurchaseMonths;
    }

    public double getRemainingAmount() {
        return Math.max(0, targetAmount - currentSavings);
    }

    public double getRequiredWeeklySavings() {
        if (useHirePurchase) {
            return getHirePurchaseWeeklyPayment();
        }

        int weeks = getTimeframeInWeeks();
        if (weeks <= 0) return getRemainingAmount();
        return getRemainingAmount() / weeks;
    }

    public double getHirePurchaseTotal() {
        if (!useHirePurchase) return targetAmount;

        double remainingAfterDeposit = targetAmount - hirePurchaseDeposit;
        double interestAmount = remainingAfterDeposit * (hirePurchaseInterest / 100);
        return hirePurchaseDeposit + remainingAfterDeposit + interestAmount;
    }

    public double getHirePurchaseWeeklyPayment() {
        if (!useHirePurchase) return 0;

        double totalCost = getHirePurchaseTotal();
        double remainingAfterDeposit = totalCost - hirePurchaseDeposit;
        int weeks = hirePurchaseMonths * 4;
        return remainingAfterDeposit / weeks;
    }

    public String getAffordabilityStatus(double availableWeeklyFunds) {
        double requiredSavings = getRequiredWeeklySavings();
        double affordabilityRatio = availableWeeklyFunds / requiredSavings;

        if (affordabilityRatio >= 1.5) {
            return "Easily Affordable";
        } else if (affordabilityRatio >= 1.0) {
            return "Affordable";
        } else if (affordabilityRatio >= 0.7) {
            return "Challenging";
        } else {
            return "Not Affordable";
        }
    }

    public String getProgressPercentage() {
        if (targetAmount <= 0) return "0%";
        double percentage = (currentSavings / targetAmount) * 100;
        return String.format("%.1f%%", Math.min(100, percentage));
    }

    private int getTimeframeInWeeks() {
        return switch (timeframeUnit) {
            case "Days" -> (int) Math.ceil(timeframeValue / 7.0);
            case "Weeks" -> timeframeValue;
            case "Months" -> timeframeValue * 4;
            default -> 1;
        };
    }
}