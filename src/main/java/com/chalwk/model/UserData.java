// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData {
    private final List<Bill> weeklyBills;
    private final List<Bill> monthlyBills;
    private final List<Invoice> invoices;
    private final List<IncomeStream> incomeStreams;
    private final List<PlanItem> planItems;
    private double weeklyIncome;

    @JsonCreator
    public UserData(@JsonProperty("weeklyBills") List<Bill> weeklyBills,
                    @JsonProperty("monthlyBills") List<Bill> monthlyBills,
                    @JsonProperty("invoices") List<Invoice> invoices,
                    @JsonProperty("incomeStreams") List<IncomeStream> incomeStreams,
                    @JsonProperty("planItems") List<PlanItem> planItems,
                    @JsonProperty("weeklyIncome") double weeklyIncome) {
        this.weeklyBills = weeklyBills != null ? weeklyBills : new ArrayList<>();
        this.monthlyBills = monthlyBills != null ? monthlyBills : new ArrayList<>();
        this.invoices = invoices != null ? invoices : new ArrayList<>();
        this.incomeStreams = incomeStreams != null ? incomeStreams : new ArrayList<>();
        this.planItems = planItems != null ? planItems : new ArrayList<>();
        this.weeklyIncome = weeklyIncome;
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

    public List<PlanItem> getPlanItems() {
        return planItems;
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

    private double getWeeklyExpenses() {
        double weeklyExpenses = getWeeklyBills().stream()
                .mapToDouble(bill -> switch (bill.getFrequency()) {
                    case "Bi-Weekly" -> bill.getAmount() / 2;
                    case "Monthly" -> bill.getAmount() / 4;
                    default -> bill.getAmount();
                })
                .sum();

        double monthlyExpensesAsWeekly = getMonthlyBills().stream()
                .filter(bill -> "automatic".equals(bill.getPaymentMethod()))
                .mapToDouble(bill -> switch (bill.getFrequency()) {
                    case "Weekly" -> bill.getAmount();
                    case "Bi-Weekly" -> bill.getAmount() / 2;
                    default -> bill.getAmount() / 4;
                })
                .sum();

        return weeklyExpenses + monthlyExpensesAsWeekly;
    }
}