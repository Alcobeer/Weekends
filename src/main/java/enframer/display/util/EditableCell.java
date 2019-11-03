package enframer.display.util;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import org.jetbrains.annotations.Nullable;

/**
 * Ячейка для таблицы, которая автоматически сохраняет текст внутри, при потере фокуса,
 * а также имеет поддержку перехода по другим ячейка посредством "стрелочек.
 *
 * @param <S> тип, из которого состоят строки таблицы
 * @param <T> тип столбца, в котором находится ячейка
 */
public class EditableCell<S, T> extends TableCell<S, T> {

    /**
     * Пустой конвертер для строк.
     */
    public static final StringConverter<String> IDENTITY_CONVERTER = new StringConverter<String>() {

        @Override
        public String toString(String object) {
            return object;
        }

        @Override
        public String fromString(String string) {
            return string == null ? "" : string;
        }

    };
    // Внутреннее текстовое поле
    private final TextField textField = new TextField();
    // Конвертер для конвертации текста в текстовое поле и обратно
    private final StringConverter<T> converter;

    public EditableCell(StringConverter<T> converter) {
        this(converter, null);
    }

    /**
     * @param formatter Необходим для контроля ввода в текстовое поле. Если установлен на null, то будет использоваться форматтер по умолчанию.
     */
    public EditableCell(StringConverter<T> converter, @Nullable TextFormatter<T> formatter) {
        this.converter = converter;

        itemProperty().addListener((obx, oldItem, newItem) -> {
            if (newItem == null) {
                setText(null);
            } else {
                setText(converter.toString(newItem));
            }
        });
        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        textField.setOnAction(evt -> commitEdit(this.converter.fromString(textField.getText())));
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitEdit(this.converter.fromString(textField.getText()));
            }
        });

        //Код, отвечающий за перемещение по таблице посредством нажатия "стрелочек",
        //а также за отмену ввода посредством клавиши Esc
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                textField.setText(converter.toString(getItem()));
                cancelEdit();
                event.consume();
            } else if (event.getCode() == KeyCode.RIGHT) {
                getTableView().getSelectionModel().selectRightCell();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                getTableView().getSelectionModel().selectLeftCell();
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                getTableView().getSelectionModel().selectAboveCell();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                getTableView().getSelectionModel().selectBelowCell();
                event.consume();
            }
        });

        if (formatter != null) {
            textField.setTextFormatter(formatter);
        }
    }

    /**
     * Метод для создания EditableCell, поддерживающиего ввод строк
     */
    public static <S> EditableCell<S, String> createStringEditableCell() {
        return new EditableCell<>(IDENTITY_CONVERTER);
    }

    // устанавливаем текст в текстовое поле и отображаем графические элементы
    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(converter.toString(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
    }

    // Возвращаемся к отображению текста
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void commitEdit(T item) {
        // Этот блок кода необходим, для того чтобы принимать результат при убирания фокуса с ячейки
        // потому что встроенный механизм выключает редактирующий режим до того, как мы можем перехватить потерю фокуса.
        // Раньше реализация метода commitEdit(...) просто не работала в тот момент, когда мы не редактируем поле...
        if (!isEditing() && !item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                CellEditEvent<S, T> event = new CellEditEvent<>(table,
                        new TablePosition<>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

}