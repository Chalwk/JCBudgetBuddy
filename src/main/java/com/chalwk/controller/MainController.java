// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.controller;

import com.chalwk.model.UserData;
import com.chalwk.util.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public VBox income;
    public VBox weeklyBills;
    public VBox monthlyBills;
    public VBox invoices;
    public GridPane dashboard;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Button exportBtn, importBtn;
    @FXML
    private Tab incomeTab;
    @FXML
    private DashboardController dashboardController;
    @FXML
    private WeeklyBillsController weeklyBillsController;
    @FXML
    private MonthlyBillsController monthlyBillsController;
    @FXML
    private InvoicesController invoicesController;
    @FXML
    private IncomeController incomeController;
    private DataManager dataManager;
    private UserData userData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataManager = DataManager.getInstance();
        loadUserData();

        // Initialize included controllers
        if (dashboardController != null) {
            dashboardController.setUserData(userData);
            dashboardController.setMainController(this);
        }
        if (weeklyBillsController != null) {
            weeklyBillsController.setUserData(userData);
            weeklyBillsController.setMainController(this);
        }
        if (monthlyBillsController != null) {
            monthlyBillsController.setUserData(userData);
            monthlyBillsController.setMainController(this);
        }
        if (invoicesController != null) {
            invoicesController.setUserData(userData);
            invoicesController.setMainController(this);
        }
        if (incomeController != null) {
            incomeController.setUserData(userData);
            incomeController.setMainController(this);
        }

        setupEventHandlers();
    }

    private void loadUserData() {
        userData = dataManager.loadUserData();
    }

    private void setupEventHandlers() {
        exportBtn.setOnAction(e -> exportData());
        importBtn.setOnAction(e -> importData());
    }

    private void exportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Budget Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        fileChooser.setInitialFileName("JCBudgetBuddy-export.json");

        File file = fileChooser.showSaveDialog(exportBtn.getScene().getWindow());
        if (file != null) {
            try {
                dataManager.exportData(userData, file);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Data exported successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to export data: " + e.getMessage());
            }
        }
    }

    private void importData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Budget Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        File file = fileChooser.showOpenDialog(importBtn.getScene().getWindow());
        if (file != null) {
            try {
                userData = dataManager.importData(file);
                updateAllControllers();
                saveUserData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Data imported successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to import data: " + e.getMessage());
            }
        }
    }

    public void saveUserData() {
        dataManager.saveUserData(userData);
        if (dashboardController != null) {
            dashboardController.updateCalculations();
        }
        if (incomeController != null) {
            incomeController.setUserData(userData);
        }
    }

    public void openIncomeTab() {
        if (incomeTab != null && mainTabPane != null) {
            mainTabPane.getSelectionModel().select(incomeTab);
        }
    }

    private void updateAllControllers() {
        if (dashboardController != null) dashboardController.setUserData(userData);
        if (weeklyBillsController != null) weeklyBillsController.setUserData(userData);
        if (monthlyBillsController != null) monthlyBillsController.setUserData(userData);
        if (invoicesController != null) invoicesController.setUserData(userData);
        if (incomeController != null) incomeController.setUserData(userData);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}