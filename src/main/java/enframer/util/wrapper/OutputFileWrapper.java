package enframer.util.wrapper;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class OutputFileWrapper extends AbstractWrapper<File> {
    public OutputFileWrapper(File content) {
        super(content);
    }

    @Override
    @NotNull String checkValidImpl() {
        if (getContent().exists()) {
            if (!getContent().isFile()) {
                return String.format("Объект %s не является файлом.", getContent().getPath());
            } else if (!getContent().canWrite()) {
                return String.format("Файл %s закрыт для записи. Проверьте доступна ли папка с файлом для записи, а также открыт ли файл в какой-то другой программе.",
                        getContent().getPath());
            }
        } else if (!getContent().getParentFile().canWrite()) {
            return String.format("Папка, в которой находится %s, закрыта для записи. Проверьте можно ли записывать файлы в эту папку, а также открыт ли файл в какой-то другой программе.",
                    getContent().getPath());
        }

        return "";
    }

    @Override
    public String toString() {
        return getContent().getPath();
    }
}
