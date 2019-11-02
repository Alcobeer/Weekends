package enframer;

import enframer.display.WindowController;
import enframer.display.controller.ErrorGui;
import enframer.display.controller.MsgGui;
import enframer.exception.CrashReport;
import enframer.util.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class Enframer extends Application {
    public static final String NAME = "Enframer";
    public static final String VERSION = "1.0.0";
    private static Enframer instance;

    public static void launch(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> onErrorOccured(CrashReport.makeCrashReport(e)));

        instance = new Enframer();

        Application.launch(Enframer.class, args);
    }

    public static Enframer getEnframer() {
        return instance;
    }

    private static void onErrorOccured(CrashReport report) {
        report.getCause().printStackTrace();
        getEnframer().displayWindow(new WindowController<ErrorGui>("gui/errorGui.fxml", Enframer.NAME + " - Ошибка"),
                (stage, errorGui) -> {
                    stage.setResizable(false);
                    errorGui.fillCrashAreaWithText(report.toString());
                });
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(FileUtils.getURLFor("gui/mainGui.fxml"));
        try {
            Parent root = loader.load();
            primaryStage.setTitle(NAME + "-" + VERSION);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMaxWidth(1850);//TODO изменить
            primaryStage.show();
        } catch (Throwable t) {
            onErrorOccured(CrashReport.makeCrashReport(t));
        }
    }

    /**
     * Отображает окно предупреждения с указанным сообщением.
     *
     * @param message сообщение, которое будет отображено в окне предупреждения.
     */
    public void displayWarningWindow(String message) {
        displayWindow(new WindowController<MsgGui>("gui/msgGui.fxml", Enframer.NAME + " - Предупреждение"), (stage, msgGui) -> {
            msgGui.setMessage(message);
            stage.setResizable(false);
        });
    }

    public <T> void displayWindow(WindowController<T> windowController) {
        displayWindow(windowController, null);
    }

    public <T> void displayWindow(WindowController<T> windowController, @Nullable BiConsumer<Stage, T> doOnInit) {
        windowController.display(doOnInit);
    }
}
