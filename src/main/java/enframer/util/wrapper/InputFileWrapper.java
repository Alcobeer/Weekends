package enframer.util.wrapper;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Враппер для файла, который нужно считать.
 */
public class InputFileWrapper extends AbstractWrapper<File> {
    public InputFileWrapper(File fileIn) {
        super(fileIn);
    }

    @Override
    protected @NotNull String checkValidImpl() {
        if (!getContent().exists()) {
            return String.format("Файл %s не существует.", getContent().getPath());
        } else if (!getContent().isFile()) {
            return String.format("Объект %s не является файлом.", getContent().getPath());
        } else if (!getContent().canRead()) {
            return String.format("Файл %s закрыт для считывания.", getContent().getPath());
        }
        return "";
    }

    @Override
    public String toString() {
        return getContent().getPath();
    }
}
