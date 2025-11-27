package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    private List<Bill> weeklyBills;
    private List<Bill> monthlyBills;
    private List<Invoice> invoices;
    private double weeklyIncome;

    @JsonCreator
    public UserData(@JsonProperty("weeklyBills") List<Bill> weeklyBills,
                    @JsonProperty("monthlyBills") List<Bill> monthlyBills,
                    @JsonProperty("invoices") List<Invoice> invoices,
                    @JsonProperty("weeklyIncome") double weeklyIncome) {
        this.weeklyBills = weeklyBills != null ? weeklyBills : new ArrayList<>();
        this.monthlyBills = monthlyBills != null ? monthlyBills : new ArrayList<>();
        this.invoices = invoices != null ? invoices : new ArrayList<>();
        this.weeklyIncome = weeklyIncome;
    }

    // Getters and setters
    public List<Bill> getWeeklyBills() {
        return weeklyBills;
    }

    public void setWeeklyBills(List<Bill> weeklyBills) {
        this.weeklyBills = weeklyBills;
    }

    public List<Bill> getMonthlyBills() {
        return monthlyBills;
    }

    public void setMonthlyBills(List<Bill> monthlyBills) {
        this.monthlyBills = monthlyBills;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public double getWeeklyIncome() {
        return weeklyIncome;
    }

    public void setWeeklyIncome(double weeklyIncome) {
        this.weeklyIncome = weeklyIncome;
    }
}