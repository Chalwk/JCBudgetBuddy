package com.chalwk.controller;

import com.chalwk.model.IncomeStream;
import com.chalwk.model.UserData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label weeklyIncomeLabel;
    @FXML
    private Label weeklyExpensesLabel;
    @FXML
    private Label remainingBalanceLabel;
    @FXML
    private Label monthlyAverageLabel;
    @FXML
    private Label incomeStreamsCountLabel;
    @FXML
    private Button manageIncomeBtn;

    private UserData userData;
    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        updateCalculations();
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
        updateCalculations();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void setupEventHandlers() {
        manageIncomeBtn.setOnAction(e -> openIncomeTab());
    }

    public void updateCalculations() {
        if (userData == null) return;

        double totalWeeklyIncome = userData.getTotalWeeklyIncome();

        // Count active income streams
        long activeStreamsCount = userData.getIncomeStreams().stream()
                .filter(IncomeStream::isActive)
                .count();

        // Calculate weekly expenses from weekly bills (adjusting for frequency)
        double weeklyExpensesFromWeeklyBills = userData.getWeeklyBills().stream()
                .mapToDouble(bill -> switch (bill.getFrequency()) {
                    case "Bi-Weekly" -> bill.getAmount() / 2;
                    case "Monthly" -> bill.getAmount() / 4;
                    default -> bill.getAmount();
                })
                .sum();

        // Calculate weekly expenses from monthly bills (only automatic payments)
        double weeklyExpensesFromMonthlyBills = userData.getMonthlyBills().stream()
                .filter(bill -> "automatic".equals(bill.getPaymentMethod()))
                .mapToDouble(bill -> switch (bill.getFrequency()) {
                    case "Weekly" -> bill.getAmount();
                    case "Bi-Weekly" -> bill.getAmount() / 2; // Half the amount per week
                    default -> bill.getAmount() / 4;
                })
                .sum();

        double totalWeeklyExpenses = weeklyExpensesFromWeeklyBills + weeklyExpensesFromMonthlyBills;
        double remainingBalance = totalWeeklyIncome - totalWeeklyExpenses;
        double monthlyAverage = totalWeeklyExpenses * 4;

        weeklyIncomeLabel.setText(String.format("$%.2f", totalWeeklyIncome));
        weeklyExpensesLabel.setText(String.format("$%.2f", totalWeeklyExpenses));
        remainingBalanceLabel.setText(String.format("$%.2f", remainingBalance));
        monthlyAverageLabel.setText(String.format("$%.2f", monthlyAverage));

        // Update income streams count
        incomeStreamsCountLabel.setText(activeStreamsCount + " active income streams");

        // Set color based on balance
        if (remainingBalance >= 0) {
            remainingBalanceLabel.setStyle("-fx-text-fill: #2ecc71;");
        } else {
            remainingBalanceLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void openIncomeTab() {
        if (mainController != null) {
            mainController.openIncomeTab();
        }
    }
}