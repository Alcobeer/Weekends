package enframer.util.wrapper;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DirWrapper extends AbstractWrapper<File> {

    public DirWrapper(File fileIn) {
        super(fileIn);
    }

    @Override
    @NotNull String checkValidImpl() {
        if (!getContent().exists()) {
            return String.format("Папка %s не существует.", getContent().getPath());
        } else if (!getContent().isDirectory()) {
            return String.format("Объект %s не является папкой.", getContent().getPath());
        }
        return "";
    }

    @Override
    public String toString() {
        return getContent().getPath();
    }
}
