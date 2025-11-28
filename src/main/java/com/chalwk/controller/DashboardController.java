package com.chalwk.controller;

import com.chalwk.model.Bill;
import com.chalwk.model.UserData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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
    private Button editIncomeBtn;

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

    private void setupEventHandlers() {
        editIncomeBtn.setOnAction(e -> editWeeklyIncome());
    }

    public void updateCalculations() {
        if (userData == null) return;

        double weeklyIncome = userData.getWeeklyIncome();
        double weeklyExpensesFromWeeklyBills = userData.getWeeklyBills().stream()
                .mapToDouble(Bill::getAmount)
                .sum();

        double weeklyExpensesFromMonthlyBills = userData.getMonthlyBills().stream()
                .filter(bill -> "automatic".equals(bill.getPaymentMethod()))
                .mapToDouble(bill -> bill.getAmount() / 4)
                .sum();

        double totalWeeklyExpenses = weeklyExpensesFromWeeklyBills + weeklyExpensesFromMonthlyBills;
        double remainingBalance = weeklyIncome - totalWeeklyExpenses;
        double monthlyAverage = totalWeeklyExpenses * 4;

        weeklyIncomeLabel.setText(String.format("$%.2f", weeklyIncome));
        weeklyExpensesLabel.setText(String.format("$%.2f", totalWeeklyExpenses));
        remainingBalanceLabel.setText(String.format("$%.2f", remainingBalance));
        monthlyAverageLabel.setText(String.format("$%.2f", monthlyAverage));

        // Set color based on balance
        if (remainingBalance >= 0) {
            remainingBalanceLabel.setStyle("-fx-text-fill: #2ecc71;");
        } else {
            remainingBalanceLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void editWeeklyIncome() {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Set Weekly Income");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        TextField incomeField = new TextField();
        incomeField.setText(String.valueOf(userData.getWeeklyIncome()));

        grid.add(new Label("Weekly Income ($):"), 0, 0);
        grid.add(incomeField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return Double.parseDouble(incomeField.getText());
                } catch (NumberFormatException e) {
                    showAlert();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::setWeeklyIncome);
    }

    public void setWeeklyIncome(double income) {
        if (userData != null) {
            userData.setWeeklyIncome(income);
            updateCalculations();
            if (mainController != null) {
                mainController.saveUserData();
            }
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid amount format");
        alert.showAndWait();
    }
}