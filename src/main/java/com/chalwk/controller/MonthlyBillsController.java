package com.chalwk.controller;

import com.chalwk.model.Bill;
import com.chalwk.model.UserData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MonthlyBillsController implements Initializable {

    @FXML
    private TableView<Bill> billsTable;
    @FXML
    private Button addBillBtn;

    private UserData userData;
    private MainController mainController;

    private static TableColumn<Bill, Double> getBillDoubleTableColumn() {
        TableColumn<Bill, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(col -> new TableCell<Bill, Double>() {
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
        return amountCol;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupEventHandlers();
        setupColumnResizing();
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
        billsTable.getColumns().clear();

        TableColumn<Bill, String> nameCol = new TableColumn<>("Bill");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Bill, Double> amountCol = getBillDoubleTableColumn();

        TableColumn<Bill, String> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        TableColumn<Bill, String> dayCol = new TableColumn<>("Payment Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));

        TableColumn<Bill, String> notesCol = new TableColumn<>("Additional Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        TableColumn<Bill, String> paymentMethodCol = new TableColumn<>("Payment Method");
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<Bill, Void> actionsCol = getBillVoidTableColumn();

        billsTable.getColumns().addAll(nameCol, amountCol, frequencyCol, dayCol, notesCol, paymentMethodCol, actionsCol);

        // Add double-click handler
        billsTable.setRowFactory(tv -> {
            TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Bill bill = row.getItem();
                    editBill(bill);
                }
            });
            return row;
        });
    }

    private TableColumn<Bill, Void> getBillVoidTableColumn() {
        TableColumn<Bill, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<Bill, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.getStyleClass().addAll("warning-button", "table-button");
                deleteBtn.getStyleClass().addAll("danger-button", "table-button");

                editBtn.setOnAction(e -> {
                    Bill bill = getTableView().getItems().get(getIndex());
                    editBill(bill);
                });

                deleteBtn.setOnAction(e -> {
                    Bill bill = getTableView().getItems().get(getIndex());
                    deleteBill(bill);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    buttons.getStyleClass().add("actions-container");
                    setGraphic(buttons);
                }
            }
        });
        return actionsCol;
    }

    private void setupEventHandlers() {
        addBillBtn.getStyleClass().add("primary-button");
        addBillBtn.setOnAction(e -> showBillDialog(null));
    }

    private void setupColumnResizing() {
        // Initial adjustment
        adjustColumnWidths();

        // Listen for table width changes
        billsTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustColumnWidths();
        });

        // Also adjust when table becomes visible
        billsTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                adjustColumnWidths();
            }
        });
    }

    private void adjustColumnWidths() {
        if (billsTable.getColumns().isEmpty()) return;

        double totalWidth = billsTable.getWidth();
        if (totalWidth <= 0) return;

        // Reserve fixed space for action buttons
        double actionsWidth = 200;
        double availableWidth = totalWidth - actionsWidth;

        TableColumn<?, ?>[] columns = billsTable.getColumns().toArray(new TableColumn[0]);

        // Set widths based on preferred ratios
        columns[0].setPrefWidth(availableWidth * 0.25); // Bill name
        columns[1].setPrefWidth(availableWidth * 0.12); // Amount
        columns[2].setPrefWidth(availableWidth * 0.12); // Frequency
        columns[3].setPrefWidth(availableWidth * 0.15); // Payment Day
        columns[4].setPrefWidth(availableWidth * 0.18); // Notes
        columns[5].setPrefWidth(availableWidth * 0.10); // Payment Method

        // Set fixed width for actions column
        columns[6].setPrefWidth(actionsWidth);
        columns[6].setMinWidth(actionsWidth);
        columns[6].setMaxWidth(actionsWidth);
    }

    private void refreshTable() {
        if (userData != null) {
            billsTable.getItems().setAll(userData.getMonthlyBills());
        }
    }

    private void showBillDialog(Bill bill) {
        Dialog<Bill> dialog = new Dialog<>();
        dialog.setTitle(bill == null ? "Add Monthly Bill" : "Edit Monthly Bill");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        TextField nameField = new TextField();
        TextField amountField = new TextField();
        ComboBox<String> frequencyCombo = new ComboBox<>();
        TextField dayField = new TextField();
        TextField notesField = new TextField();
        ComboBox<String> paymentMethodCombo = new ComboBox<>();

        // Update frequency options to include Bi-Weekly
        frequencyCombo.getItems().addAll("Weekly", "Bi-Weekly", "Monthly");
        frequencyCombo.setValue("Monthly");

        paymentMethodCombo.getItems().addAll("manual", "automatic");
        paymentMethodCombo.setValue("manual");

        if (bill != null) {
            nameField.setText(bill.getName());
            amountField.setText(String.valueOf(bill.getAmount()));
            frequencyCombo.setValue(bill.getFrequency());
            dayField.setText(bill.getDay());
            notesField.setText(bill.getNotes());
            paymentMethodCombo.setValue(bill.getPaymentMethod());
        }

        grid.add(new Label("Bill Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Frequency:"), 0, 2);
        grid.add(frequencyCombo, 1, 2);
        grid.add(new Label("Payment Day:"), 0, 3);
        grid.add(dayField, 1, 3);
        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesField, 1, 4);
        grid.add(new Label("Payment Method:"), 0, 5);
        grid.add(paymentMethodCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Bill(
                            bill != null ? bill.getId() : generateNewId(),
                            nameField.getText(),
                            Double.parseDouble(amountField.getText()),
                            frequencyCombo.getValue(),
                            dayField.getText(),
                            notesField.getText(),
                            paymentMethodCombo.getValue()
                    );
                } catch (NumberFormatException e) {
                    showAlert();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (bill == null) {
                userData.getMonthlyBills().add(result);
            } else {
                int index = userData.getMonthlyBills().indexOf(bill);
                if (index != -1) {
                    userData.getMonthlyBills().set(index, result);
                }
            }
            refreshTable();
            mainController.saveUserData();
        });
    }

    private void editBill(Bill bill) {
        showBillDialog(bill);
    }

    private void deleteBill(Bill bill) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Bill");
        alert.setContentText("Are you sure you want to delete this bill: " + bill.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userData.getMonthlyBills().remove(bill);
                refreshTable();
                mainController.saveUserData();
            }
        });
    }

    private int generateNewId() {
        return userData.getMonthlyBills().stream()
                .mapToInt(Bill::getId)
                .max()
                .orElse(0) + 1;
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid amount format");
        alert.showAndWait();
    }
}