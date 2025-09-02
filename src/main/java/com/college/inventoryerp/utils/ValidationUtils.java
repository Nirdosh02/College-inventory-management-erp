package com.college.inventoryerp.utils;

//package com.college.inventory.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[\\+]?[0-9]{10,15}$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    public static void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
    }

    public static void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Warning", message);
    }

    public static void highlightError(TextField field) {
        field.getStyleClass().add("error-field");
    }

    public static void clearError(TextField field) {
        field.getStyleClass().remove("error-field");
    }

    public static boolean validateTextField(TextField field, String fieldName) {
        if (!isNotEmpty(field.getText())) {
            highlightError(field);
            showError(fieldName + " is required.");
            return false;
        }
        clearError(field);
        return true;
    }

    public static boolean validateEmail(TextField field) {
        if (!isNotEmpty(field.getText())) {
            highlightError(field);
            showError("Email is required.");
            return false;
        }
        if (!isValidEmail(field.getText())) {
            highlightError(field);
            showError("Please enter a valid email address.");
            return false;
        }
        clearError(field);
        return true;
    }

    public static boolean validatePhone(TextField field) {
        if (isNotEmpty(field.getText()) && !isValidPhone(field.getText())) {
            highlightError(field);
            showError("Please enter a valid phone number.");
            return false;
        }
        clearError(field);
        return true;
    }
}

