package enframer.display.controller;

import enframer.Enframer;
import enframer.display.IntConverterWithDefault;
import enframer.display.util.EditableCell;
import enframer.display.util.PatternTextFormatter;
import enframer.exception.CrashReport;
import enframer.exception.ReportedException;
import enframer.gen.AttributeCalculator;
import enframer.gen.PNGOverlayCreator;
import enframer.util.FileUtilities;
import enframer.util.RarityCategory;
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
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Класс отвечающий за всё то, что происходит на главном экране.
 */
public class MainGui {
    private static final Pattern PATTERN_RARITY_INDEX = Pattern.compile("[2-9]?");
    private static final Pattern PATTERN_LEVEL = Pattern.compile("(?!(0))[0-9]{0,2}$");
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
            if (tableAttributes.getItems().size() < 10) {
                tableAttributes.getItems().add(new Attribute());
            } else {
                Enframer.getEnframer().displayMessageWindow("В таблицу можно ввести не более 10 аттрибутов!");
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
        if (!file.exists()) {
            Enframer.getEnframer().displayMessageWindow("PNG-картинка не найдена.\nПроверьте введённый путь.");//TODO сменить на подчеркивание красным пути до картинки?
            return;
        } else {
            File out = FileUtilities.openDirChoosingDialog(file);
            if (!genAttributeFile(out)) {
                return;
            }

            genOverlayedImages(file, out);
            Enframer.getEnframer().displayMessageWindow("Файлы успешно сгенерированы!");
        }
    }

    private void genOverlayedImages(File fileIn, File dirOut) {
        try {
            BufferedImage image = ImageIO.read(fileIn);

            PNGOverlayCreator creator = new PNGOverlayCreator(image);
            for (RarityCategory rarity : RarityCategory.values()) {
                if (rarity.hasOverlayColor()) {
                    RarityCategory.OverlayColor color = rarity.getOverlayColor();
                    ByteArrayOutputStream overlayedImage = creator.genOverlayedImage(color);

                    File fileOut = new File(dirOut + "/" + FileUtilities.getFileName(fileIn) + "_" + rarity.getName().toLowerCase() + "." + FileUtilities.getFileExt(fileIn));
                    FileUtils.touch(fileOut);
                    if (fileOut.exists() && FileUtilities.checkWriteAccess(fileOut)) {
                        FileUtilities.writeTo(fileOut, overlayedImage);
                    }
                }
            }
        } catch (IOException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Не удалось наложить рамки на картинку."));
        }
    }

    private boolean genAttributeFile(File dirOut) {
        try {
            List<Attribute> attributes = tableAttributes.getItems();
            if (attributes.isEmpty()) {
                Enframer.getEnframer().displayMessageWindow("Необходимо указать хотя бы один атрибут в таблице.");
                return false;
            } else if (fieldLevel.getText().isEmpty()) {
                Enframer.getEnframer().displayMessageWindow("Необходимо указать Уровень.");
                return false;
            } else if (fieldRarityIndex.getText().isEmpty()) {
                Enframer.getEnframer().displayMessageWindow("Необходимо указать Индекс ценности.");
                return false;
            }

            for (int i = 0; i < attributes.size(); i++) {
                Attribute attribute = attributes.get(i);
                if (attribute.name.get().isEmpty()) {
                    Enframer.getEnframer().displayMessageWindow("Необходимо указать название атрибута " + (i + 1) + " в таблице атрибутов.");
                    return false;
                }
            }

            AttributeCalculator calculator = new AttributeCalculator(attributes, Integer.parseInt(fieldLevel.getText()), Integer.parseInt(fieldRarityIndex.getText()));
            String out = calculator.calcAttributeForAllCategories();

            File fileOut = new File(dirOut + "/Атрибуты.txt");
            FileUtils.writeStringToFile(fileOut, out, StandardCharsets.UTF_8, false);

        } catch (IOException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Не удалось сгенерировать файл с атрибутами."));
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
