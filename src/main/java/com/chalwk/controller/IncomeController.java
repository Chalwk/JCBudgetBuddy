// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.controller;

import com.chalwk.model.IncomeStream;
import com.chalwk.model.UserData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
    private Button addIncomeStreamBtn;

    private UserData userData;
    private MainController mainController;

    private static TableColumn<IncomeStream, LocalDate> getIncomeStreamLocalDateTableColumn() {
        TableColumn<IncomeStream, LocalDate> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
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

    private static TableColumn<IncomeStream, String> getIncomeStreamStringTableColumn() {
        TableColumn<IncomeStream, String> statusCol = new TableColumn<>("Status");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupIncomeStreamsTable();
        setupEventHandlers();
        setupColumnResizing();
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
        refreshTables();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @SuppressWarnings("unchecked")
    private void setupIncomeStreamsTable() {
        incomeStreamsTable.getColumns().clear();

        TableColumn<IncomeStream, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<IncomeStream, Double> amountCol = getIncomeStreamDoubleTableColumn();

        TableColumn<IncomeStream, String> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        TableColumn<IncomeStream, LocalDate> startDateCol = getIncomeStreamLocalDateTableColumn();

        TableColumn<IncomeStream, LocalDate> endDateCol = getStreamLocalDateTableColumn();

        TableColumn<IncomeStream, String> statusCol = getIncomeStreamStringTableColumn();

        TableColumn<IncomeStream, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        TableColumn<IncomeStream, Void> actionsCol = new TableColumn<>("Actions");
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

    private void setupEventHandlers() {
        addIncomeStreamBtn.setOnAction(e -> showIncomeStreamDialog(null));
    }

    private void setupColumnResizing() {
        adjustColumnWidths();
        incomeStreamsTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustColumnWidths();
        });
        incomeStreamsTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                adjustColumnWidths();
            }
        });
    }

    private void adjustColumnWidths() {
        if (incomeStreamsTable.getColumns().isEmpty()) return;

        double totalWidth = incomeStreamsTable.getWidth();
        if (totalWidth <= 0) return;

        double actionsWidth = 275;
        double availableWidth = totalWidth - actionsWidth;

        TableColumn<?, ?>[] columns = incomeStreamsTable.getColumns().toArray(new TableColumn[0]);

        columns[0].setPrefWidth(availableWidth * 0.20); // Name
        columns[1].setPrefWidth(availableWidth * 0.10); // Amount
        columns[2].setPrefWidth(availableWidth * 0.10); // Frequency
        columns[3].setPrefWidth(availableWidth * 0.15); // Start Date
        columns[4].setPrefWidth(availableWidth * 0.15); // End Date
        columns[5].setPrefWidth(availableWidth * 0.10); // Status
        columns[6].setPrefWidth(availableWidth * 0.20); // Notes

        columns[7].setPrefWidth(actionsWidth);
        columns[7].setMinWidth(actionsWidth);
        columns[7].setMaxWidth(actionsWidth);
    }

    private void refreshTables() {
        if (userData != null) {
            incomeStreamsTable.getItems().setAll(userData.getIncomeStreams());
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
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField nameField = new TextField();
        TextField amountField = new TextField();
        ComboBox<String> frequencyCombo = new ComboBox<>();
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        CheckBox activeCheckbox = new CheckBox("Active");
        TextField notesField = new TextField();

        frequencyCombo.getItems().addAll("weekly", "fortnightly", "monthly", "yearly", "one-off");
        frequencyCombo.setValue("weekly");
        startDatePicker.setValue(LocalDate.now());
        activeCheckbox.setSelected(true);

        frequencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isOneOff = "one-off".equals(newVal);

            startDatePicker.setDisable(isOneOff);
            endDatePicker.setDisable(isOneOff);
            activeCheckbox.setDisable(isOneOff);

            if (isOneOff) {
                startDatePicker.setValue(LocalDate.now());
                endDatePicker.setValue(null);
                activeCheckbox.setSelected(true);
            }
        });

        if (stream != null) {
            nameField.setText(stream.getName());
            amountField.setText(String.valueOf(stream.getAmount()));
            frequencyCombo.setValue(stream.getFrequency());

            boolean isOneOff = "one-off".equals(stream.getFrequency());

            if (!isOneOff) {
                startDatePicker.setValue(stream.getStartDate());
                if (stream.getEndDate() != null) {
                    endDatePicker.setValue(stream.getEndDate());
                }
                activeCheckbox.setSelected(stream.isActive());
            } else {
                startDatePicker.setValue(LocalDate.now());
                startDatePicker.setDisable(true);
                endDatePicker.setDisable(true);
                activeCheckbox.setDisable(true);
                activeCheckbox.setSelected(stream.getAmount() > 0);
            }

            notesField.setText(stream.getNotes());
        }

        boolean initialIsOneOff = "one-off".equals(frequencyCombo.getValue());
        startDatePicker.setDisable(initialIsOneOff);
        endDatePicker.setDisable(initialIsOneOff);
        activeCheckbox.setDisable(initialIsOneOff);

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
                    String frequency = frequencyCombo.getValue();
                    boolean isOneOff = "one-off".equals(frequency);
                    boolean active = isOneOff ? (Double.parseDouble(amountField.getText()) > 0) : activeCheckbox.isSelected();

                    LocalDate startDate = isOneOff ? LocalDate.now() : startDatePicker.getValue();
                    LocalDate endDate = isOneOff ? null : endDatePicker.getValue();

                    return new IncomeStream(
                            stream != null ? stream.getId() : generateNewIncomeStreamId(),
                            nameField.getText(),
                            Double.parseDouble(amountField.getText()),
                            frequency,
                            startDate,
                            endDate,
                            active,
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
            mainController.saveUserData();
        });
    }

    private void editIncomeStream(IncomeStream stream) {
        showIncomeStreamDialog(stream);
    }

    private void toggleIncomeStream(IncomeStream stream) {
        if ("one-off".equals(stream.getFrequency())) {
            if (stream.isActive() && stream.getAmount() > 0) {
                stream.setActive(false);
            } else if (!stream.isActive() && stream.getAmount() > 0) {
                stream.setActive(true);
            }
        } else {
            stream.setActive(!stream.isActive());
        }
        refreshTables();
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

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid amount format");
        alert.showAndWait();
    }
}