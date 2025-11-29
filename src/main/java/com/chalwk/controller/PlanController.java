// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.controller;

import com.chalwk.model.PlanItem;
import com.chalwk.model.UserData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PlanController implements Initializable {

    @FXML
    private TableView<PlanItem> plansTable;
    @FXML
    private Button addPlanBtn;
    @FXML
    private Label affordabilityAdviceLabel;
    @FXML
    private Label weeklySavingsLabel;
    @FXML
    private Label timeframeLabel;
    @FXML
    private VBox advicePanel;

    private UserData userData;
    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupEventHandlers();
        setupColumnResizing();
        advicePanel.setVisible(false);
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
        refreshTable();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        plansTable.getColumns().clear();

        TableColumn<PlanItem, String> nameCol = new TableColumn<>("Item");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<PlanItem, Double> targetCol = new TableColumn<>("Target Amount");
        targetCol.setCellValueFactory(new PropertyValueFactory<>("targetAmount"));
        targetCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });

        TableColumn<PlanItem, Double> savingsCol = new TableColumn<>("Current Savings");
        savingsCol.setCellValueFactory(new PropertyValueFactory<>("currentSavings"));
        savingsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });

        TableColumn<PlanItem, String> progressCol = new TableColumn<>("Progress");
        progressCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    setText(plan.getProgressPercentage());

                    double progress = plan.getCurrentSavings() / plan.getTargetAmount();
                    if (progress >= 1.0) {
                        setTextFill(Color.GREEN);
                    } else if (progress >= 0.5) {
                        setTextFill(Color.ORANGE);
                    } else {
                        setTextFill(Color.RED);
                    }
                }
            }
        });

        TableColumn<PlanItem, String> timeframeCol = new TableColumn<>("Timeframe");
        timeframeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    setText(plan.getTimeframeValue() + " " + plan.getTimeframeUnit());
                }
            }
        });

        TableColumn<PlanItem, Double> weeklySavingsCol = new TableColumn<>("Weekly Savings");
        weeklySavingsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    setText(String.format("$%.2f/wk", plan.getRequiredWeeklySavings()));
                }
            }
        });

        TableColumn<PlanItem, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button analyzeBtn = new Button("Analyze");

            {
                editBtn.getStyleClass().addAll("warning-button", "table-button");
                deleteBtn.getStyleClass().addAll("danger-button", "table-button");
                analyzeBtn.getStyleClass().addAll("primary-button", "table-button");

                editBtn.setOnAction(e -> {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    editPlan(plan);
                });

                deleteBtn.setOnAction(e -> {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    deletePlan(plan);
                });

                analyzeBtn.setOnAction(e -> {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    showAffordabilityAnalysis(plan);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, analyzeBtn, editBtn, deleteBtn);
                    buttons.getStyleClass().add("actions-container");
                    setGraphic(buttons);
                }
            }
        });

        plansTable.getColumns().addAll(nameCol, targetCol, savingsCol, progressCol, timeframeCol, weeklySavingsCol, actionsCol);
        plansTable.setRowFactory(tv -> {
            TableRow<PlanItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    PlanItem plan = row.getItem();
                    showAffordabilityAnalysis(plan);
                }
            });
            return row;
        });
    }

    private void setupEventHandlers() {
        addPlanBtn.getStyleClass().add("primary-button");
        addPlanBtn.setOnAction(e -> showPlanDialog(null));
    }

    private void setupColumnResizing() {
        adjustColumnWidths();
        plansTable.widthProperty().addListener((obs, oldVal, newVal) -> adjustColumnWidths());
    }

    private void adjustColumnWidths() {
        if (plansTable.getColumns().isEmpty()) return;

        double totalWidth = plansTable.getWidth();
        if (totalWidth <= 0) return;

        double actionsWidth = 265;
        double availableWidth = totalWidth - actionsWidth;

        TableColumn<?, ?>[] columns = plansTable.getColumns().toArray(new TableColumn[0]);

        columns[0].setPrefWidth(availableWidth * 0.17); // Name
        columns[1].setPrefWidth(availableWidth * 0.15); // Target Amount
        columns[2].setPrefWidth(availableWidth * 0.15); // Current Savings
        columns[3].setPrefWidth(availableWidth * 0.10); // Progress
        columns[4].setPrefWidth(availableWidth * 0.15); // Timeframe
        columns[5].setPrefWidth(availableWidth * 0.15); // Weekly Savings

        columns[6].setPrefWidth(actionsWidth);
        columns[6].setMinWidth(actionsWidth);
        columns[6].setMaxWidth(actionsWidth);
    }

    private void refreshTable() {
        if (userData != null) {
            plansTable.getItems().setAll(userData.getPlanItems());
        }
    }

    private void showPlanDialog(PlanItem plan) {
        Dialog<PlanItem> dialog = new Dialog<>();
        dialog.setTitle(plan == null ? "Add Plan Item" : "Edit Plan Item");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField nameField = new TextField();
        TextArea descriptionField = new TextArea();
        descriptionField.setPrefRowCount(3);
        TextField targetAmountField = new TextField();
        TextField currentSavingsField = new TextField();
        ComboBox<String> timeframeUnitCombo = new ComboBox<>();
        TextField timeframeValueField = new TextField();
        DatePicker targetDatePicker = new DatePicker();
        CheckBox hirePurchaseCheckbox = new CheckBox("Use Hire Purchase");
        TextField depositField = new TextField();
        TextField interestField = new TextField();
        TextField monthsField = new TextField();

        timeframeUnitCombo.getItems().addAll("Days", "Weeks", "Months");
        timeframeUnitCombo.setValue("Weeks");

        depositField.setDisable(true);
        interestField.setDisable(true);
        monthsField.setDisable(true);

        hirePurchaseCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            depositField.setDisable(!newVal);
            interestField.setDisable(!newVal);
            monthsField.setDisable(!newVal);
        });

        if (currentSavingsField.getText().isEmpty()) currentSavingsField.setText("0");
        if (timeframeValueField.getText().isEmpty()) timeframeValueField.setText("4");
        if (depositField.getText().isEmpty()) depositField.setText("0");
        if (interestField.getText().isEmpty()) interestField.setText("0");
        if (monthsField.getText().isEmpty()) monthsField.setText("12");
        if (targetDatePicker.getValue() == null) targetDatePicker.setValue(LocalDate.now().plusWeeks(4));

        if (plan != null) {
            nameField.setText(plan.getName());
            descriptionField.setText(plan.getDescription());
            targetAmountField.setText(String.valueOf(plan.getTargetAmount()));
            currentSavingsField.setText(String.valueOf(plan.getCurrentSavings()));
            timeframeUnitCombo.setValue(plan.getTimeframeUnit());
            timeframeValueField.setText(String.valueOf(plan.getTimeframeValue()));
            targetDatePicker.setValue(plan.getTargetDate());
            hirePurchaseCheckbox.setSelected(plan.isUseHirePurchase());
            depositField.setText(String.valueOf(plan.getHirePurchaseDeposit()));
            interestField.setText(String.valueOf(plan.getHirePurchaseInterest()));
            monthsField.setText(String.valueOf(plan.getHirePurchaseMonths()));
        }

        grid.add(new Label("Item Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Target Amount:"), 0, 2);
        grid.add(targetAmountField, 1, 2);
        grid.add(new Label("Current Savings:"), 0, 3);
        grid.add(currentSavingsField, 1, 3);
        grid.add(new Label("Timeframe Unit:"), 0, 4);
        grid.add(timeframeUnitCombo, 1, 4);
        grid.add(new Label("Timeframe Value:"), 0, 5);
        grid.add(timeframeValueField, 1, 5);
        grid.add(new Label("Target Date:"), 0, 6);
        grid.add(targetDatePicker, 1, 6);
        grid.add(hirePurchaseCheckbox, 0, 7, 2, 1);
        grid.add(new Label("Deposit:"), 0, 8);
        grid.add(depositField, 1, 8);
        grid.add(new Label("Interest (%):"), 0, 9);
        grid.add(interestField, 1, 9);
        grid.add(new Label("Months:"), 0, 10);
        grid.add(monthsField, 1, 10);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    if (nameField.getText().trim().isEmpty()) {
                        showAlert("Invalid input", "Item Name is required.");
                        return null;
                    }
                    if (targetAmountField.getText().trim().isEmpty()) {
                        showAlert("Invalid input", "Target Amount is required.");
                        return null;
                    }

                    String name = nameField.getText().trim();
                    String description = descriptionField.getText().trim();
                    double targetAmount = Double.parseDouble(targetAmountField.getText());
                    double currentSavings = Double.parseDouble(currentSavingsField.getText());
                    String timeframeUnit = timeframeUnitCombo.getValue();
                    int timeframeValue = Integer.parseInt(timeframeValueField.getText());
                    LocalDate targetDate = targetDatePicker.getValue();

                    if (targetDate == null) {
                        showAlert("Invalid input", "Target Date is required.");
                        return null;
                    }

                    boolean useHirePurchase = hirePurchaseCheckbox.isSelected();

                    double deposit = 0;
                    double interest = 0;
                    int months = 0;

                    if (useHirePurchase) {
                        deposit = Double.parseDouble(depositField.getText());
                        interest = Double.parseDouble(interestField.getText());
                        months = Integer.parseInt(monthsField.getText());
                    }

                    return new PlanItem(
                            plan != null ? plan.getId() : generateNewId(),
                            name,
                            description,
                            targetAmount,
                            currentSavings,
                            timeframeUnit,
                            timeframeValue,
                            targetDate,
                            useHirePurchase,
                            deposit,
                            interest,
                            months
                    );
                } catch (NumberFormatException e) {
                    showAlert("Invalid input", "Please check all numeric fields are valid numbers.");
                    return null;
                } catch (Exception e) {
                    showAlert("Error", "Please fill in all required fields: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (plan == null) {
                userData.getPlanItems().add(result);
            } else {
                int index = userData.getPlanItems().indexOf(plan);
                if (index != -1) {
                    userData.getPlanItems().set(index, result);
                }
            }
            refreshTable();
            mainController.saveUserData();
        });
    }

    private void editPlan(PlanItem plan) {
        showPlanDialog(plan);
    }

    private void deletePlan(PlanItem plan) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Plan Item");
        alert.setContentText("Are you sure you want to delete this plan: " + plan.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userData.getPlanItems().remove(plan);
                refreshTable();
                mainController.saveUserData();
            }
        });
    }

    private void showAffordabilityAnalysis(PlanItem plan) {
        double availableWeeklyFunds = userData.getTotalWeeklyIncome() - getWeeklyExpenses();
        double requiredWeekly = plan.getRequiredWeeklySavings();
        String affordability = plan.getAffordabilityStatus(availableWeeklyFunds);

        weeklySavingsLabel.setText(String.format("$%.2f", requiredWeekly));
        timeframeLabel.setText(plan.getTimeframeValue() + " " + plan.getTimeframeUnit());
        affordabilityAdviceLabel.setText(affordability);

        switch (affordability) {
            case "Easily Affordable":
                affordabilityAdviceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                break;
            case "Affordable":
                affordabilityAdviceLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                break;
            case "Challenging":
                affordabilityAdviceLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                break;
            default:
                affordabilityAdviceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }

        advicePanel.setVisible(true);
    }

    private double getWeeklyExpenses() {
        return userData.getWeeklyBills().stream()
                .mapToDouble(bill -> switch (bill.getFrequency()) {
                    case "Bi-Weekly" -> bill.getAmount() / 2;
                    case "Monthly" -> bill.getAmount() / 4;
                    default -> bill.getAmount();
                })
                .sum();
    }

    private int generateNewId() {
        return userData.getPlanItems().stream()
                .mapToInt(PlanItem::getId)
                .max()
                .orElse(0) + 1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}