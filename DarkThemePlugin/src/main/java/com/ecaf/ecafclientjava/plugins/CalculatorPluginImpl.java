package com.ecaf.ecafclientjava.plugins;

import com.ecaf.ecafclientjava.plugins.calculatrice.CalculatorPlugin;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class CalculatorPluginImpl implements CalculatorPlugin {

    private TextField display = new TextField();
    private double num1 = 0;
    private String operator = "";
    private boolean start = true;

    @Override
    public Pane getUI() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);

        display.setEditable(false);
        display.setPrefHeight(50);
        pane.add(display, 0, 0, 4, 1);

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };

        int row = 1;
        int col = 0;
        for (String label : buttonLabels) {
            Button button = new Button(label);
            button.setPrefSize(50, 50);
            button.setOnAction(e -> handleButtonClick(label));
            pane.add(button, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        return pane;
    }

    private void handleButtonClick(String label) {
        switch (label) {
            case "=":
                handleEquals();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                handleOperator(label);
                break;
            default:
                handleNumber(label);
                break;
        }
    }

    private void handleNumber(String value) {
        if (start) {
            display.clear();
            start = false;
        }
        display.appendText(value);
    }

    private void handleOperator(String value) {
        if (!operator.isEmpty()) {
            return;
        }
        operator = value;
        num1 = Double.parseDouble(display.getText());
        display.clear();
    }

    private void handleEquals() {
        double num2 = Double.parseDouble(display.getText());
        double result = calculate(num1, num2, operator);
        display.setText(String.valueOf(result));
        operator = "";
        start = true;
    }

    private double calculate(double num1, double num2, String operator) {
        switch (operator) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                if (num2 == 0) {
                    return 0;
                }
                return num1 / num2;
            default:
                return 0;
        }
    }
}
