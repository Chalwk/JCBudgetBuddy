package com.chalwk.controller;

import com.chalwk.model.IncomeStream;
import com.chalwk.model.OneTimePayment;
import com.chalwk.model.UserData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class IncomeController implements Initializable {

    @FXML
    private TableView<IncomeStream> incomeStreamsTable;
    @FXML
    private TableView<OneTimePayment> oneTimePaymentsTable;
    @FXML
    private Button addIncomeStreamBtn, addOneTimePaymentBtn;
    @FXML
    private Label totalWeeklyIncomeLabel, monthlyOneTimeLabel, activeStreamsLabel;

    private UserData userData;
    private MainController mainController;

    private static TableColumn<IncomeStream, LocalDate> getIncomeStreamLocalDateTableColumn() {
        TableColumn<IncomeStream, LocalDate> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startDateCol.setPrefWidth(80);
        startDateCol.setCellFactory(col -> new TableCell<IncomeStream, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                }
            }
        });
        return startDateCol;
    }

    private static TableColumn<IncomeStream, Double> getIncomeStreamDoubleTableColumn() {
        TableColumn<IncomeStream, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(80);
        amountCol.setCellFactory(col -> new TableCell<>() {
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
        setupIncomeStreamsTable();
        setupOneTimePaymentsTable();
        setupEventHandlers();
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
        refreshTables();
        updateSummary();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @SuppressWarnings("unchecked")
    private void setupIncomeStreamsTable() {
        incomeStreamsTable.getColumns().clear();

        TableColumn<IncomeStream, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(100);

        TableColumn<IncomeStream, Double> amountCol = getIncomeStreamDoubleTableColumn();

        TableColumn<IncomeStream, String> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        frequencyCol.setPrefWidth(80);

        TableColumn<IncomeStream, LocalDate> startDateCol = getIncomeStreamLocalDateTableColumn();

        TableColumn<IncomeStream, LocalDate> endDateCol = getStreamLocalDateTableColumn();

        TableColumn<IncomeStream, String> statusCol = getIncomeStreamStringTableColumn();

        TableColumn<IncomeStream, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesCol.setPrefWidth(200);

        TableColumn<IncomeStream, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(300);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button toggleBtn = new Button("Toggle");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.getStyleClass().addAll("warning-button", "table-button");
                toggleBtn.getStyleClass().addAll("primary-button", "table-button");
                deleteBtn.getStyleClass().addAll("danger-button", "table-button");

                editBtn.setOnAction(e -> {
                    IncomeStream stream = getTableView().getItems().get(getIndex());
                    editIncomeStream(stream);
                });

                toggleBtn.setOnAction(e -> {
                    IncomeStream stream = getTableView().getItems().get(getIndex());
                    toggleIncomeStream(stream);
                });

                deleteBtn.setOnAction(e -> {
                    IncomeStream stream = getTableView().getItems().get(getIndex());
                    deleteIncomeStream(stream);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    IncomeStream stream = getTableView().getItems().get(getIndex());
                    toggleBtn.setText(stream.isActive() ? "End" : "Activate");
                    HBox buttons = new HBox(5, editBtn, toggleBtn, deleteBtn);
                    buttons.getStyleClass().add("actions-container");
                    setGraphic(buttons);
                }
            }
        });

        incomeStreamsTable.getColumns().addAll(nameCol, amountCol, frequencyCol, startDateCol, endDateCol, statusCol, notesCol, actionsCol);
    }

    private static TableColumn<IncomeStream, String> getIncomeStreamStringTableColumn() {
        TableColumn<IncomeStream, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    IncomeStream stream = getTableView().getItems().get(getIndex());
                    if (stream.isActive()) {
                        setText("Active");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setText("Ended");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
        return statusCol;
    }

    private static TableColumn<IncomeStream, LocalDate> getStreamLocalDateTableColumn() {
        TableColumn<IncomeStream, LocalDate> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateCol.setPrefWidth(100);
        endDateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                } else {
                    if (date == null) {
                        setText("Ongoing");
                    } else {
                        setText(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                    }
                }
            }
        });
        return endDateCol;
    }

    @SuppressWarnings("unchecked")
    private void setupOneTimePaymentsTable() {
        oneTimePaymentsTable.getColumns().clear();

        TableColumn<OneTimePayment, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(80);

        TableColumn<OneTimePayment, Double> amountCol = getOneTimePaymentDoubleTableColumn();

        TableColumn<OneTimePayment, LocalDate> dateCol = getOneTimePaymentLocalDateTableColumn();

        TableColumn<OneTimePayment, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);

        TableColumn<OneTimePayment, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesCol.setPrefWidth(200);

        TableColumn<OneTimePayment, Void> actionsCol = getOneTimePaymentVoidTableColumn();

        oneTimePaymentsTable.getColumns().addAll(descCol, amountCol, dateCol, categoryCol, notesCol, actionsCol);
    }

    private TableColumn<OneTimePayment, Void> getOneTimePaymentVoidTableColumn() {
        TableColumn<OneTimePayment, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(315);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.getStyleClass().addAll("warning-button", "table-button");
                deleteBtn.getStyleClass().addAll("danger-button", "table-button");

                editBtn.setOnAction(e -> {
                    OneTimePayment payment = getTableView().getItems().get(getIndex());
                    editOneTimePayment(payment);
                });

                deleteBtn.setOnAction(e -> {
                    OneTimePayment payment = getTableView().getItems().get(getIndex());
                    deleteOneTimePayment(payment);
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

    private static TableColumn<OneTimePayment, LocalDate> getOneTimePaymentLocalDateTableColumn() {
        TableColumn<OneTimePayment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        dateCol.setPrefWidth(100);
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                }
            }
        });
        return dateCol;
    }

    private static TableColumn<OneTimePayment, Double> getOneTimePaymentDoubleTableColumn() {
        TableColumn<OneTimePayment, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);
        amountCol.setCellFactory(col -> new TableCell<>() {
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

    private void setupEventHandlers() {
        addIncomeStreamBtn.setOnAction(e -> showIncomeStreamDialog(null));
        addOneTimePaymentBtn.setOnAction(e -> showOneTimePaymentDialog(null));
    }

    private void refreshTables() {
        if (userData != null) {
            incomeStreamsTable.getItems().setAll(userData.getIncomeStreams());
            oneTimePaymentsTable.getItems().setAll(userData.getOneTimePayments());
        }
    }

    private void updateSummary() {
        if (userData != null) {
            double totalWeekly = userData.getTotalWeeklyIncome();
            double monthlyOneTime = userData.getOneTimePaymentsForCurrentMonth();
            long activeCount = userData.getIncomeStreams().stream().filter(IncomeStream::isActive).count();

            totalWeeklyIncomeLabel.setText(String.format("$%.2f", totalWeekly));
            monthlyOneTimeLabel.setText(String.format("$%.2f", monthlyOneTime));
            activeStreamsLabel.setText(String.valueOf(activeCount));
        }
    }

    private void showIncomeStreamDialog(IncomeStream stream) {
        Dialog<IncomeStream> dialog = new Dialog<>();
        dialog.setTitle(stream == null ? "Add Income Stream" : "Edit Income Stream");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        TextField nameField = new TextField();
        TextField amountField = new TextField();
        ComboBox<String> frequencyCombo = new ComboBox<>();
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        CheckBox activeCheckbox = new CheckBox("Active");
        TextField notesField = new TextField();

        frequencyCombo.getItems().addAll("weekly", "fortnightly", "monthly");
        frequencyCombo.setValue("weekly");
        startDatePicker.setValue(LocalDate.now());
        activeCheckbox.setSelected(true);

        if (stream != null) {
            nameField.setText(stream.getName());
            amountField.setText(String.valueOf(stream.getAmount()));
            frequencyCombo.setValue(stream.getFrequency());
            startDatePicker.setValue(stream.getStartDate());
            if (stream.getEndDate() != null) {
                endDatePicker.setValue(stream.getEndDate());
            }
            activeCheckbox.setSelected(stream.isActive());
            notesField.setText(stream.getNotes());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Frequency:"), 0, 2);
        grid.add(frequencyCombo, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(new Label("Active:"), 0, 5);
        grid.add(activeCheckbox, 1, 5);
        grid.add(new Label("Notes:"), 0, 6);
        grid.add(notesField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new IncomeStream(
                            stream != null ? stream.getId() : generateNewIncomeStreamId(),
                            nameField.getText(),
                            Double.parseDouble(amountField.getText()),
                            frequencyCombo.getValue(),
                            startDatePicker.getValue(),
                            endDatePicker.getValue(),
                            activeCheckbox.isSelected(),
                            notesField.getText()
                    );
                } catch (NumberFormatException e) {
                    showAlert();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (stream == null) {
                userData.getIncomeStreams().add(result);
            } else {
                int index = userData.getIncomeStreams().indexOf(stream);
                if (index != -1) {
                    userData.getIncomeStreams().set(index, result);
                }
            }
            refreshTables();
            updateSummary();
            mainController.saveUserData();
        });
    }

    private void showOneTimePaymentDialog(OneTimePayment payment) {
        Dialog<OneTimePayment> dialog = new Dialog<>();
        dialog.setTitle(payment == null ? "Add One-Time Payment" : "Edit One-Time Payment");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        TextField descriptionField = new TextField();
        TextField amountField = new TextField();
        DatePicker datePicker = new DatePicker();
        ComboBox<String> categoryCombo = new ComboBox<>();
        TextField notesField = new TextField();

        categoryCombo.getItems().addAll("Bonus", "Gift", "Refund", "Other");
        categoryCombo.setValue("Other");
        datePicker.setValue(LocalDate.now());

        if (payment != null) {
            descriptionField.setText(payment.getDescription());
            amountField.setText(String.valueOf(payment.getAmount()));
            datePicker.setValue(payment.getPaymentDate());
            categoryCombo.setValue(payment.getCategory());
            notesField.setText(payment.getNotes());
        }

        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);
        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new OneTimePayment(
                            payment != null ? payment.getId() : generateNewOneTimePaymentId(),
                            descriptionField.getText(),
                            Double.parseDouble(amountField.getText()),
                            datePicker.getValue(),
                            categoryCombo.getValue(),
                            notesField.getText()
                    );
                } catch (NumberFormatException e) {
                    showAlert();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (payment == null) {
                userData.getOneTimePayments().add(result);
            } else {
                int index = userData.getOneTimePayments().indexOf(payment);
                if (index != -1) {
                    userData.getOneTimePayments().set(index, result);
                }
            }
            refreshTables();
            updateSummary();
            mainController.saveUserData();
        });
    }

    private void editIncomeStream(IncomeStream stream) {
        showIncomeStreamDialog(stream);
    }

    private void toggleIncomeStream(IncomeStream stream) {
        stream.setActive(!stream.isActive());
        refreshTables();
        updateSummary();
        mainController.saveUserData();
    }

    private void deleteIncomeStream(IncomeStream stream) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Income Stream");
        alert.setContentText("Are you sure you want to delete this income stream: " + stream.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userData.getIncomeStreams().remove(stream);
                refreshTables();
                updateSummary();
                mainController.saveUserData();
            }
        });
    }

    private void editOneTimePayment(OneTimePayment payment) {
        showOneTimePaymentDialog(payment);
    }

    private void deleteOneTimePayment(OneTimePayment payment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete One-Time Payment");
        alert.setContentText("Are you sure you want to delete this payment: " + payment.getDescription() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userData.getOneTimePayments().remove(payment);
                refreshTables();
                updateSummary();
                mainController.saveUserData();
            }
        });
    }

    private int generateNewIncomeStreamId() {
        return userData.getIncomeStreams().stream()
                .mapToInt(IncomeStream::getId)
                .max()
                .orElse(0) + 1;
    }

    private int generateNewOneTimePaymentId() {
        return userData.getOneTimePayments().stream()
                .mapToInt(OneTimePayment::getId)
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