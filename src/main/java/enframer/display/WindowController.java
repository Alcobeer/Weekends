package enframer.display;

import enframer.Enframer;
import enframer.exception.CrashReport;
import enframer.exception.ReportedException;
import enframer.util.FileUtilities;
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

    /**
     * Если true, то не окно не будет давать перейти к главному окну, пока не будет закрыто.
     */
    private final boolean isBoundToMainWindow;

    public WindowController(String fxmlFilePath) {
        this(fxmlFilePath, null);
    }

    public WindowController(String fxmlFilePath, String title) {
        this(fxmlFilePath, title, true);
    }

    public WindowController(String fxmlFilePath, @Nullable String title, boolean isBoundToMainWindow) {
        this.title = title;
        this.loader = new FXMLLoader(FileUtilities.getURLFor(fxmlFilePath));
        this.isBoundToMainWindow = isBoundToMainWindow;
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
        try {
            Parent root = load();
            if (title != null) {
                window.setTitle(title);
            }

            window.initModality(Modality.WINDOW_MODAL);
            if (isBoundToMainWindow) {
                window.initOwner(Enframer.getEnframer().getRootWindow());
            }

            if (doOnInit != null) {
                doOnInit.accept(window, getController());
            }

            window.setScene(new Scene(root));
            window.show();
        } catch (IOException ex) {
            throw new ReportedException(CrashReport.makeCrashReport(ex, "Произошла ошибка при попытке инициализировать и отобразить окно " + (title != null ? "" : getController() == null ? "" : getClass().getName())));
        }
    }
}
