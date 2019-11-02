package enframer.display.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MsgGui {

    @FXML
    private Button buttonOk;
    @FXML
    private Label msgLabel;

    @FXML
    private void initialize() {
        msgLabel.setContentDisplay(ContentDisplay.CENTER);
        msgLabel.setWrapText(true);
        buttonOk.setOnAction(event -> {
            Stage gui = (Stage) buttonOk.getScene().getWindow();
            gui.close();
        });
    }

    public void setMessage(String msg) {
        msgLabel.setText(msg);
    }
}
