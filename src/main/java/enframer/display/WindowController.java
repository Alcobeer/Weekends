package enframer.display;

import enframer.exception.CrashReport;
import enframer.exception.ReportedException;
import enframer.util.FileUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Используется для создания окон посредством вызова метода {@link #display(BiConsumer)}
 *
 * @param <T> - тип контроллера, на который ссылается fxml-файл.
 */
public class WindowController<T> {
    private final FXMLLoader loader;
    private final String title;

    public WindowController(String fxmlFilePath) {
        this(fxmlFilePath, null);
    }

    public WindowController(String fxmlFilePath, @Nullable String title) {
        this.title = title;
        this.loader = new FXMLLoader(FileUtils.getURLFor(fxmlFilePath));
    }

    private <S> S load() throws IOException {
        return loader.load();
    }

    private T getController() {
        return loader.getController();
    }

    /**
     * Инициализирует и запускает окно, к которому привязан контроллер.
     *
     * @param doOnInit отвечает за то, что будет происходить во время подготовки окна к отрисовке.<p>
     *                 Stage - это непосредственно объект, отвечающий за параметры окна, например, за его название, ширину и т.д.<p>
     *                 T - контроллер пользовательского интерфейса. Отвечает за всё, что отрисовывается ВНУТРИ окна.
     */
    public void display(@Nullable BiConsumer<Stage, T> doOnInit) {
        Stage window = new Stage();
        window.initModality(Modality.WINDOW_MODAL);
        try {
            Parent root = load();
            if (doOnInit != null) {
                doOnInit.accept(window, getController());
            }
            if (title != null) {
                window.setTitle(title);
            }
            window.setScene(new Scene(root));
            window.show();
        } catch (IOException ex) {
            throw new ReportedException(CrashReport.makeCrashReport(ex, "Произошла ошибка при попытке инициализировать и отобразить окно " + (title != null ? "" : getController() == null ? "" : getClass().getName())));
        }
    }
}
