package enframer.util.wrapper;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InputImageWrapper extends InputFileWrapper {
    private BufferedImage image;

    public InputImageWrapper(File fileIn) {
        super(fileIn);
    }

    @Override
    protected @NotNull String checkValidImpl() {
        String out = super.checkValidImpl();
        if (out.isEmpty()) {
            try {
                image = ImageIO.read(getContent());
                if (image == null) {
                    return String.format("Файл %s не является картинкой.", getContent().getPath());
                }
            } catch (IOException e) {
                return String.format("Не удалось считать входной файл %s как картинку.", getContent().getPath());
            }

            return "";
        } else return out;
    }

    /**
     * Должно быть вызвано после проверки методов {@link #checkValid()}.
     */
    public BufferedImage getImage() {
        return image;
    }
}
