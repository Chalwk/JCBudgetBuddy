package com.chalwk.controller;

import com.chalwk.model.Invoice;
import com.chalwk.model.Payment;
import com.chalwk.model.UserData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class InvoicesController implements Initializable {

    @FXML
    private TableView<Invoice> invoicesTable;
    @FXML
    private Button addInvoiceBtn;

    private UserData userData;
    private MainController mainController;

    private static TableColumn<Invoice, Double> getInvoiceDoubleTableColumn() {
        TableColumn<Invoice, Double> totalCol = new TableColumn<>("Total Amount");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(120);
        totalCol.setCellFactory(col -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
            }
        });
        return totalCol;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupEventHandlers();
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
        invoicesTable.getColumns().clear();

        TableColumn<Invoice, String> numberCol = new TableColumn<>("Invoice Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        numberCol.setPrefWidth(150);

        TableColumn<Invoice, Double> totalCol = getInvoiceDoubleTableColumn();

        TableColumn<Invoice, Double> balanceCol = new TableColumn<>("Balance Owing");
        balanceCol.setPrefWidth(120);
        balanceCol.setCellFactory(col -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty) {
                    setText(null);
                } else {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    double bal = invoice.getBalance();
                    setText(String.format("$%.2f", bal));
                }
            }
        });

        TableColumn<Invoice, Void> paymentsCol = new TableColumn<>("Payments");
        paymentsCol.setPrefWidth(100);
        paymentsCol.setCellFactory(col -> new TableCell<Invoice, Void>() {
            private final Button viewPaymentsBtn = new Button("View Payments");

            {
                viewPaymentsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                viewPaymentsBtn.setOnAction(e -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    viewPayments(invoice);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewPaymentsBtn);
                }
            }
        });

        TableColumn<Invoice, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<Invoice, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button addPaymentBtn = new Button("Add Payment");

            {
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                addPaymentBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

                editBtn.setOnAction(e -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    editInvoice(invoice);
                });

                deleteBtn.setOnAction(e -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    deleteInvoice(invoice);
                });

                addPaymentBtn.setOnAction(e -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    addPayment(invoice);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, addPaymentBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        invoicesTable.getColumns().addAll(numberCol, totalCol, balanceCol, paymentsCol, actionsCol);
    }

    private void setupEventHandlers() {
        addInvoiceBtn.setOnAction(e -> showInvoiceDialog(null));
    }

    private void refreshTable() {
        if (userData != null) {
            invoicesTable.getItems().setAll(userData.getInvoices());
        }
    }

    private void showInvoiceDialog(Invoice invoice) {
        Dialog<Invoice> dialog = new Dialog<>();
        dialog.setTitle(invoice == null ? "Add Invoice" : "Edit Invoice");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        TextField numberField = new TextField();
        TextField totalField = new TextField();

        if (invoice != null) {
            numberField.setText(invoice.getNumber());
            totalField.setText(String.valueOf(invoice.getTotal()));
        }

        grid.add(new Label("Invoice Number:"), 0, 0);
        grid.add(numberField, 1, 0);
        grid.add(new Label("Total Amount:"), 0, 1);
        grid.add(totalField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Invoice(
                            invoice != null ? invoice.getId() : generateNewId(),
                            numberField.getText(),
                            Double.parseDouble(totalField.getText()),
                            invoice != null ? invoice.getPayments() : null
                    );
                } catch (NumberFormatException e) {
                    showAlert();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (invoice == null) {
                userData.getInvoices().add(result);
            } else {
                int index = userData.getInvoices().indexOf(invoice);
                if (index != -1) {
                    userData.getInvoices().set(index, result);
                }
            }
            refreshTable();
            mainController.saveUserData();
        });
    }

    private void editInvoice(Invoice invoice) {
        showInvoiceDialog(invoice);
    }

    private void deleteInvoice(Invoice invoice) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Invoice");
        alert.setContentText("Are you sure you want to delete this invoice: " + invoice.getNumber() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userData.getInvoices().remove(invoice);
                refreshTable();
                mainController.saveUserData();
            }
        });
    }

    private void viewPayments(Invoice invoice) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Payments for Invoice " + invoice.getNumber());

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        Label totalLabel = new Label("Total: $" + String.format("%.2f", invoice.getTotal()));
        Label balanceLabel = new Label("Balance: $" + String.format("%.2f", invoice.getBalance()));

        TableView<Payment> paymentsTable = new TableView<>();
        paymentsTable.setPrefHeight(300);

        TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(150);

        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);
        amountCol.setCellFactory(col -> new TableCell<Payment, Double>() {
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

        paymentsTable.getColumns().addAll(dateCol, amountCol);
        paymentsTable.getItems().addAll(invoice.getPayments());

        vbox.getChildren().addAll(totalLabel, balanceLabel, paymentsTable);
        dialog.getDialogPane().setContent(vbox);

        dialog.showAndWait();
    }

    private void addPayment(Invoice invoice) {
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Add Payment for Invoice " + invoice.getNumber());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        TextField amountField = new TextField();

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Payment(
                            datePicker.getValue().format(DateTimeFormatter.ISO_DATE),
                            Double.parseDouble(amountField.getText())
                    );
                } catch (NumberFormatException e) {
                    showAlert();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(payment -> {
            invoice.getPayments().add(payment);
            refreshTable();
            mainController.saveUserData();
        });
    }

    private int generateNewId() {
        return userData.getInvoices().stream()
                .mapToInt(Invoice::getId)
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