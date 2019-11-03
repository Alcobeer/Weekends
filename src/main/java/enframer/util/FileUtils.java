package enframer.util;

import enframer.Enframer;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class FileUtils {
    /**
     * Проверяет, есть ли у нас доступ к изменению выбранного файла.
     * Если мы будем изменять уже открытый файл или файл, к которому нет доступа, мы создадим исключение.
     * Этот же метод позволяет проверить доступность до того, как мы попробуем изменить файл.
     *
     * @return true, если файл доступен для взаимодействия.
     */
    public static boolean isAccessible(File file) {
        return file.renameTo(file);
    }

    /**
     * Возвращает InputStream ресурса, находящегося внутри jar-архива этого приложения.
     *
     * @param resource путь до ресурса внутри jar-архива этого приложения, без указания промежуточной папки assets.
     */
    public static InputStream getStreamFor(String resource) {
        return Enframer.class.getClassLoader().getResourceAsStream("assets/" + resource);
    }

    /**
     * Возвращает URL ресурса, находящегося внутри jar-архива этого приложения.
     * @param resource путь до ресурса внутри jar-архива этого приложения, без указания промежуточной папки assets.
     */
    @Nullable
    public static URL getURLFor(String resource) {
        return Enframer.class.getClassLoader().getResource("assets/" + resource);
    }
}
