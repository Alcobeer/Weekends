package enframer.display.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ErrorGui {
    @FXML
    private TextArea textAreaCrash;

    @FXML
    private void initialize() {
        textAreaCrash.setEditable(false);
    }

    public void fillCrashAreaWithText(String text) {
        textAreaCrash.setText(text);
    }
}
