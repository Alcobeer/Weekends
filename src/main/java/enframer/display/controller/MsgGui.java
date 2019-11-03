package enframer.display.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Класс отвечающий за всё то, что происходит в Окне сообщения.
 */
public class MsgGui {

    @FXML
    private Button buttonOk;
    @FXML
    private Label msgLabel;

    /**
     * Вызывается при инициализации окна через Reflection API
     */
    @FXML
    private void initialize() {
        msgLabel.setContentDisplay(ContentDisplay.CENTER);
        msgLabel.setWrapText(true);
        buttonOk.setOnAction(event -> {
            Stage gui = (Stage) buttonOk.getScene().getWindow();
            gui.close();
        });
    }

    /**
     * Устанавливает сообщение, которое будет показано в окне сообщения.
     */
    public void setMessage(String msg) {
        msgLabel.setText(msg);
    }
}
