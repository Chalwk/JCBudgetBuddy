module com.chalwk {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    opens com.chalwk to javafx.fxml;
    opens com.chalwk.controller to javafx.fxml;
    opens com.chalwk.model to com.fasterxml.jackson.databind, javafx.base;
    opens com.chalwk.util to com.fasterxml.jackson.databind;

    exports com.chalwk;
    exports com.chalwk.controller;
    exports com.chalwk.model;
    exports com.chalwk.util;
}