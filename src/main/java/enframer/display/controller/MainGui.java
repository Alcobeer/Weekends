package enframer.display.controller;

import enframer.Enframer;
import enframer.display.IntConverterWithDefault;
import enframer.display.util.EditableCell;
import enframer.display.util.PatternTextFormatter;
import enframer.exception.CrashReport;
import enframer.exception.ReportedException;
import enframer.gen.PNGOverlayCreator;
import enframer.util.FileUtilities;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Класс отвечающий за всё то, что происходит на главном экране.
 */
public class MainGui {
    private static final Pattern PATTERN_RARITY_INDEX = Pattern.compile("[2-9]?");
    private static final Pattern PATTERN_LEVEL = Pattern.compile("[1-9]\\d?");
    private static final Pattern PATTERN_ATTRIBUTE_VALUE = Pattern.compile("^(?!(0))[0-9]{0,4}$");
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
    /**
     * Не показывается пользователю.
     */
    private VBox functional = new VBox();

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

        setupAttributeTable();

        buttonAddAttribute.setOnAction(event -> {
            if (tableAttributes.getItems().size() != 10) {
                tableAttributes.getItems().add(new Attribute());
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
        columnName.setCellFactory(callback -> EditableCell.createStringEditableCell());

        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        columnValue.setCellFactory(callback -> new EditableCell<>(new IntConverterWithDefault(1), new PatternTextFormatter<>(PATTERN_ATTRIBUTE_VALUE)));

        columnButton.setCellValueFactory(new PropertyValueFactory<>("deleteButton"));

        tableAttributes.getItems().add(new Attribute());//TODO если атрибут без названия, игнорировать его при подсчете
    }

    private void onDoneButtonClick(ActionEvent event) {
        File file = new File(fieldImagePath.getText());
        if (file.exists()) {
            File out = FileUtilities.openDirChoosingDialog(file);
            genOverlayedImages(file, out);
            Enframer.getEnframer().displayMessageWindow("Файлы успешно сгенерированы!");
        } else {
            Enframer.getEnframer().displayMessageWindow("PNG-картинка не найдена.\nПроверьте введённый путь.");//TODO сменить на подчеркивание красным пути до картинки?
        }
    }

    private void genOverlayedImages(File fileIn, File dirOut) {
        try {
            BufferedImage image = ImageIO.read(fileIn);

            PNGOverlayCreator creator = new PNGOverlayCreator(image);
            for (PNGOverlayCreator.OverlayColor color : PNGOverlayCreator.OverlayColor.values()) {
                ByteArrayOutputStream overlayedImage = creator.genOverlayedImage(color);

                File fileOut = new File(dirOut + "/" + FileUtilities.getFileName(fileIn) + "_" + color.getName() + "." + FileUtilities.getFileExt(fileIn));

                if (fileOut.exists() && FileUtilities.checkWriteAccess(fileOut)) {
                    FileUtilities.writeTo(fileOut, overlayedImage);
                }
            }
        } catch (IOException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Не удалось наложить рамки на картинку."));
        }
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
