package enframer.util;

import enframer.Enframer;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;

public class FileUtilities {
    /**
     * Проверяет, есть ли у нас доступ к изменению выбранного файла.
     * Если мы будем изменять уже открытый файл или файл, к которому нет доступа, мы создадим исключение.
     * Этот же метод позволяет проверить доступность до того, как мы попробуем изменить файл.
     *
     * В случае, если мы не можем получить к файлу доступ, пользователю также выведется сообщение об отсутствии доступа.
     *
     * @return true, если файл доступен для взаимодействия.
     */
    public static boolean checkWriteAccess(File file) {
        boolean b = file.canWrite();
        if (!b) {
            Enframer.getEnframer().displayMessageWindow("Невозможно получить доступ к файлу. Проверьте, какие программы используют этот файл, закройте их, а затем попробуйте заново.");
        }

        return b;
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
     *
     * @param resource путь до ресурса внутри jar-архива этого приложения, без указания промежуточной папки assets.
     */
    @Nullable
    public static URL getURLFor(String resource) {
        return Enframer.class.getClassLoader().getResource("assets/" + resource);
    }

    /**
     * Возвращает имя файла.
     */
    public static String getFileName(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        }

        return name;
    }

    /**
     * Возвращает расширение файла.
     */
    public static String getFileExt(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(pos + 1);
        }

        return name;
    }

    /**
     * Производит запись из массива байтов в файл.
     */
    public static void writeTo(File fileTo, ByteArrayOutputStream from) throws IOException {
        FileUtils.touch(fileTo);
        try (FileOutputStream outputStream = new FileOutputStream(fileTo)) {
            from.writeTo(outputStream);
        }
    }

    /**
     * Открывает окно проводника для выбора папки.
     *
     * @param initialDir - папка, в которой откроется окно проводника.
     * @return выбранную папку.
     */
    public static File openDirChoosingDialog(File initialDir) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Выбор папки для сохранения");
        dirChooser.setInitialDirectory(initialDir.isDirectory() ? initialDir : new File(initialDir.getParent()));
        return dirChooser.showDialog(Enframer.getEnframer().getRootWindow());
    }
}
