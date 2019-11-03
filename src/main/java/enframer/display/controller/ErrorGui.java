package enframer.display.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Класс отвечающий за всё то, что происходит на Окне ошибки.
 */
public class ErrorGui {
    @FXML
    private TextArea textAreaCrash;

    /**
     * Вызывается при инициализации окна через Reflection API
     */
    @FXML
    private void initialize() {
        textAreaCrash.setEditable(false);
    }

    /**
     * Заполняет окно ошибки заданным текстом.
     */
    public void fillCrashAreaWithText(String text) {
        textAreaCrash.setText(text);
    }
}
