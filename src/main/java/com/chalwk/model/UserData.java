// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    private final List<Bill> weeklyBills;
    private final List<Bill> monthlyBills;
    private final List<Invoice> invoices;
    private final List<IncomeStream> incomeStreams;
    private double weeklyIncome;

    @JsonCreator
    public UserData(@JsonProperty("weeklyBills") List<Bill> weeklyBills,
                    @JsonProperty("monthlyBills") List<Bill> monthlyBills,
                    @JsonProperty("invoices") List<Invoice> invoices,
                    @JsonProperty("incomeStreams") List<IncomeStream> incomeStreams,
                    @JsonProperty("weeklyIncome") double weeklyIncome) {
        this.weeklyBills = weeklyBills != null ? weeklyBills : new ArrayList<>();
        this.monthlyBills = monthlyBills != null ? monthlyBills : new ArrayList<>();
        this.invoices = invoices != null ? invoices : new ArrayList<>();
        this.incomeStreams = incomeStreams != null ? incomeStreams : new ArrayList<>();
        this.weeklyIncome = weeklyIncome;

        if (this.incomeStreams.isEmpty() && weeklyIncome > 0) {
            IncomeStream mainIncome = new IncomeStream(1, "Main Income", weeklyIncome, "weekly",
                    java.time.LocalDate.now(), null, true, "Migrated from old weekly income");
            this.incomeStreams.add(mainIncome);
        }
    }

    public List<Bill> getWeeklyBills() {
        return weeklyBills;
    }

    public List<Bill> getMonthlyBills() {
        return monthlyBills;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public List<IncomeStream> getIncomeStreams() {
        return incomeStreams;
    }

    @Deprecated
    public double getWeeklyIncome() {
        return weeklyIncome;
    }

    @Deprecated
    public void setWeeklyIncome(double weeklyIncome) {
        this.weeklyIncome = weeklyIncome;
    }

    public double getTotalWeeklyIncome() {
        return incomeStreams.stream()
                .filter(IncomeStream::isActive)
                .mapToDouble(IncomeStream::getWeeklyAmount)
                .sum();
    }
}