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

import java.net.URL;
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
    private Label availableFundsLabel;
    @FXML
    private Label detailedAdviceLabel;
    @FXML
    private Label paymentBreakdownLabel;
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

        TableColumn<PlanItem, Double> targetCol = new TableColumn<>("Total Cost");
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

        TableColumn<PlanItem, String> depositCol = new TableColumn<>("Deposit");
        depositCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    if (plan.getDeposit() > 0) {
                        setText(String.format("$%.2f", plan.getDeposit()));
                    } else {
                        setText("$0");
                    }
                }
            }
        });

        TableColumn<PlanItem, String> paymentCol = new TableColumn<>("Weekly Payment");
        paymentCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    PlanItem plan = getTableView().getItems().get(getIndex());
                    setText(String.format("$%.2f", plan.getWeeklyPayment()));
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
                    setText(plan.getTimeframeDescription());
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

        plansTable.getColumns().addAll(nameCol, targetCol, depositCol, paymentCol, timeframeCol, actionsCol);
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

        columns[0].setPrefWidth(availableWidth * 0.25); // Name
        columns[1].setPrefWidth(availableWidth * 0.20); // Total Cost
        columns[2].setPrefWidth(availableWidth * 0.15); // Deposit
        columns[3].setPrefWidth(availableWidth * 0.15); // Weekly Payment
        columns[4].setPrefWidth(availableWidth * 0.25); // Timeframe

        columns[5].setPrefWidth(actionsWidth);
        columns[5].setMinWidth(actionsWidth);
        columns[5].setMaxWidth(actionsWidth);
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
        TextField depositField = new TextField();
        TextField weeklyPaymentField = new TextField();

        depositField.setText("0");
        weeklyPaymentField.setText("50");

        if (plan != null) {
            nameField.setText(plan.getName());
            descriptionField.setText(plan.getDescription());
            targetAmountField.setText(String.valueOf(plan.getTargetAmount()));
            depositField.setText(String.valueOf(plan.getDeposit()));
            weeklyPaymentField.setText(String.valueOf(plan.getWeeklyPayment()));
        }

        grid.add(new Label("Item Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Total Cost:"), 0, 2);
        grid.add(targetAmountField, 1, 2);
        grid.add(new Label("Deposit (Optional):"), 0, 3);
        grid.add(depositField, 1, 3);
        grid.add(new Label("Weekly Payment:"), 0, 4);
        grid.add(weeklyPaymentField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    if (nameField.getText().trim().isEmpty()) {
                        showAlert("Invalid input", "Item Name is required.");
                        return null;
                    }
                    if (targetAmountField.getText().trim().isEmpty()) {
                        showAlert("Invalid input", "Total Cost is required.");
                        return null;
                    }
                    if (weeklyPaymentField.getText().trim().isEmpty()) {
                        showAlert("Invalid input", "Weekly Payment is required.");
                        return null;
                    }

                    String name = nameField.getText().trim();
                    String description = descriptionField.getText().trim();
                    double targetAmount = Double.parseDouble(targetAmountField.getText());
                    double deposit = Double.parseDouble(depositField.getText());
                    double weeklyPayment = Double.parseDouble(weeklyPaymentField.getText());

                    if (targetAmount <= 0) {
                        showAlert("Invalid input", "Total cost must be greater than 0.");
                        return null;
                    }

                    if (weeklyPayment <= 0) {
                        showAlert("Invalid input", "Weekly payment must be greater than 0.");
                        return null;
                    }

                    if (deposit < 0) {
                        showAlert("Invalid input", "Deposit cannot be negative.");
                        return null;
                    }

                    if (deposit >= targetAmount) {
                        showAlert("Invalid input", "Deposit cannot be greater than or equal to total cost.");
                        return null;
                    }

                    return new PlanItem(
                            plan != null ? plan.getId() : generateNewId(),
                            name,
                            description,
                            targetAmount,
                            deposit,
                            weeklyPayment
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
        double availableWeeklyFunds = getAvailableWeeklyFunds();
        double requiredWeekly = plan.getRequiredWeeklySavings();
        String affordability = plan.getAffordabilityStatus(availableWeeklyFunds);
        String detailedInfo = plan.getDetailedAffordabilityInfo(availableWeeklyFunds);

        weeklySavingsLabel.setText(String.format("$%.2f/wk", requiredWeekly));
        availableFundsLabel.setText(String.format("$%.2f/wk", availableWeeklyFunds));
        timeframeLabel.setText(plan.getTimeframeDescription());
        paymentBreakdownLabel.setText(plan.getPaymentBreakdown());
        affordabilityAdviceLabel.setText(affordability);
        detailedAdviceLabel.setText(detailedInfo);

        if ("Affordable".equals(affordability)) {
            affordabilityAdviceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            detailedAdviceLabel.setStyle("-fx-text-fill: #2ecc71;");
        } else {
            affordabilityAdviceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            detailedAdviceLabel.setStyle("-fx-text-fill: #e74c3c;");
        }

        advicePanel.setVisible(true);
    }

    private double getAvailableWeeklyFunds() {
        double totalWeeklyIncome = userData.getTotalWeeklyIncome();

        double weeklyExpenses = userData.getWeeklyBills().stream()
                .mapToDouble(bill -> switch (bill.getFrequency()) {
                    case "Bi-Weekly" -> bill.getAmount() / 2;
                    case "Monthly" -> bill.getAmount() / 4;
                    default -> bill.getAmount();
                })
                .sum();

        double monthlyExpensesAsWeekly = userData.getMonthlyBills().stream()
                .filter(bill -> "automatic".equals(bill.getPaymentMethod()))
                .mapToDouble(bill -> switch (bill.getFrequency()) {
                    case "Weekly" -> bill.getAmount();
                    case "Bi-Weekly" -> bill.getAmount() / 2;
                    default -> bill.getAmount() / 4;
                })
                .sum();

        return totalWeeklyIncome - (weeklyExpenses + monthlyExpensesAsWeekly);
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