package enframer.display.controller;

import enframer.Enframer;
import enframer.common.gen.AttributeFileGenerator;
import enframer.common.gen.PNGOverlayGenerator;
import enframer.display.IntConverterWithDefault;
import enframer.display.util.EditableCell;
import enframer.display.util.PatternTextFormatter;
import enframer.util.FileUtilities;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Класс отвечающий за всё то, что происходит на главном экране.
 */
public class MainGui {
    private static final Pattern PATTERN_RARITY_INDEX = Pattern.compile("[2-9]?");
    private static final Pattern PATTERN_LEVEL = Pattern.compile("(?!(0))[0-9]{0,2}$");
    private static final Pattern PATTERN_ATTRIBUTE_VALUE = Pattern.compile("^(?!(0))[0-9]{0,3}$");
    private static final Pattern PATTERN_ATTRIBUTE_NAME = Pattern.compile("^[a-zA-Zа-яА-Я]*$");
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField fieldImagePath;
    @FXML
    private Button buttonImagePath;
    @FXML
    private TextField fieldLevel;
    @FXML
    private TextField fieldRarityIndex;
    @FXML
    private TableView<Attribute> tableAttributes;
    @FXML
    private TableColumn<Attribute, String> columnName;
    @FXML
    private TableColumn<Attribute, Integer> columnValue;
    @FXML
    private TableColumn<Attribute, Button> columnButton;
    @FXML
    private Button buttonAddAttribute;
    @FXML
    private Button buttonDone;
    @FXML
    private CheckBox checkBoxFullyOverlayed;
    @FXML
    private Label wrapAllImageTooltipHolder;

    /**
     * Вызывается при инициализации окна через Reflection API
     */
    @FXML
    private void initialize() {
        buttonImagePath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выбор входной картинки");
            fileChooser.setInitialDirectory(new File("./"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Изображение", "*.png", "*.jpg"));
            File file = fileChooser.showOpenDialog(Enframer.getEnframer().getRootWindow());
            if (file != null) {
                fieldImagePath.setText(file.toPath().toString());
            }
        });

        Tooltip checkBoxTp = new Tooltip("Если не нажимать на флажок, конечный файл будет того же размера, " +
                "что и исходный, однако какой-то процент картинки будет занят рамкой, что может перекрыть часть исходной картинки.\n" +
                "Если флажок нажат, изображение останется того же размера, но рамка, как и картинка на выходе, " +
                "будут расширены таким образом, чтобы не перекрывать исходную картинку.");
        checkBoxTp.setWrapText(true);
        checkBoxTp.setMaxWidth(700);
        wrapAllImageTooltipHolder.setTooltip(checkBoxTp);
        wrapAllImageTooltipHolder.setTextFill(Color.BLUE);

        setupAttributeTable();

        buttonAddAttribute.setOnAction(event -> {
            if (tableAttributes.getItems().size() < 10) {
                tableAttributes.getItems().add(new Attribute());
            } else {
                Enframer.getEnframer().displayMessageWindow("В таблицу можно ввести не более 10 атрибутов!");
            }
        });

        // Задаём допустимые символы для ввода в поля уровня и индекса ценности при помощи регулярных выражений.
        fieldRarityIndex.setTextFormatter(new PatternTextFormatter(PATTERN_RARITY_INDEX));
        fieldLevel.setTextFormatter(new PatternTextFormatter(PATTERN_LEVEL));

        buttonDone.setOnAction(this::onDoneButtonClick);
    }

    private void setupAttributeTable() {
        tableAttributes.setEditable(true);
        tableAttributes.getSelectionModel().setCellSelectionEnabled(true);
        tableAttributes.setFixedCellSize(40.1);

        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnName.setCellFactory(callback -> new EditableCell<>(EditableCell.IDENTITY_CONVERTER, new PatternTextFormatter<>(PATTERN_ATTRIBUTE_NAME)));

        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        columnValue.setCellFactory(callback -> new EditableCell<>(new IntConverterWithDefault(1), new PatternTextFormatter<>(PATTERN_ATTRIBUTE_VALUE)));

        columnButton.setCellValueFactory(new PropertyValueFactory<>("deleteButton"));

        tableAttributes.getItems().add(new Attribute());
    }

    private void onDoneButtonClick(ActionEvent event) {
        System.out.println(fieldRarityIndex.getFont());
        if (checkFields()) {
            File in = new File(fieldImagePath.getText());
            File dirOut = FileUtilities.openDirChoosingDialog(in);

            List<Attribute> attributes = tableAttributes.getItems();
            AttributeFileGenerator calculator = new AttributeFileGenerator(attributes, Integer.parseInt(fieldLevel.getText()), Integer.parseInt(fieldRarityIndex.getText()), dirOut);
            PNGOverlayGenerator creator = new PNGOverlayGenerator(in, dirOut, checkBoxFullyOverlayed.isSelected());

            String calcInit = calculator.initWithMessage();
            if (!calcInit.isEmpty()) {
                Enframer.getEnframer().displayMessageWindow("Не удалось сгенерировать файл с атрибутами.\n" + calcInit);
                return;
            }
            String imageCreatorInit = creator.initWithMessage();
            if (!imageCreatorInit.isEmpty()) {
                Enframer.getEnframer().displayMessageWindow("Не удалось наложить рамки на картинку.\n" + imageCreatorInit);
                return;
            }

            calculator.gen();
            creator.gen();

            Enframer.getEnframer().displayMessageWindow("Файлы успешно сгенерированы!");
        }
    }

    /**
     * Вернёт true, если значения во всех полях будут корректны.
     */
    private boolean checkFields() {
        File file = new File(fieldImagePath.getText());
        if (!file.exists()) {
            Enframer.getEnframer().displayMessageWindow("Пути, указанного в текстовом поле \"Путь до картинки\" не существует! Исправьте это текстовое поле.");
            return false;
        } else if (file.isDirectory()) {
            Enframer.getEnframer().displayMessageWindow("Введённый путь в текстовом поле \"Путь до картинки\" ведёт к папке. Исправьте это текстовое поле.");
            return false;
        }

        List<Attribute> attributes = tableAttributes.getItems();
        if (attributes.isEmpty()) {
            Enframer.getEnframer().displayMessageWindow("Необходимо указать хотя бы один атрибут в таблице.");
            return false;
        }

        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            if (attribute.name.get() == null || attribute.name.get().isEmpty()) {
                int index = i + 1;
                Enframer.getEnframer().displayMessageWindow("Необходимо указать название " + index + (index % 10 == 3 ? "-его" : "-ого") + " атрибута в таблице атрибутов.");
                return false;
            }
        }

        if (fieldLevel.getText().isEmpty()) {
            Enframer.getEnframer().displayMessageWindow("Необходимо указать Уровень.");
            return false;
        } else if (fieldRarityIndex.getText().isEmpty()) {
            Enframer.getEnframer().displayMessageWindow("Необходимо указать Индекс ценности.");
            return false;
        }

        return true;
    }


    /**
     * Элемент таблицы {@link #tableAttributes}
     */
    public class Attribute {
        private StringProperty name;
        private IntegerProperty value;
        private Button deleteButton;

        private Attribute() {
            name = new SimpleStringProperty();
            value = new SimpleIntegerProperty(1);

            deleteButton = new Button("Удалить");
            deleteButton.setPrefWidth(800);
            deleteButton.setOnAction(event -> {
                tableAttributes.getItems().remove(Attribute.this);
                //Нужно для того, чтобы сбросить фокус после удаления элемента таблицы. По умолчанию фокус отходил верхнему полю.
                mainPane.requestFocus();
            });
        }

        /**
         * Необходимо для автоматического изменения значения внутри Property и в ячейке таблицы. Вызывается через Reflection API.
         */
        public StringProperty nameProperty() {
            return name;
        }

        /**
         * Необходимо для автоматического изменения значения внутри Property и в ячейке таблицы. Вызывается через Reflection API.
         */
        public IntegerProperty valueProperty() {
            return value;
        }

        /**
         * Необходимо для отображения кнопки в ячейке таблицы. Вызывается через Reflection API.
         */
        public Button getDeleteButton() {
            return deleteButton;
        }
    }
}
