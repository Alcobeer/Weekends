package enframer.gen;

import enframer.util.RarityCategory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PNGOverlayCreator {
    private BufferedImage imageIn;

    public PNGOverlayCreator(BufferedImage imageIn) {
        this.imageIn = imageIn;
    }

    public ByteArrayOutputStream genOverlayedImage(RarityCategory.OverlayColor color) throws IOException {
        int w = imageIn.getWidth();
        int h = imageIn.getHeight();

        BufferedImage overlay;
        if (color.getOverlay().getWidth() != w || color.getOverlay().getHeight() != h) {
            overlay = resize(color.getOverlay(), w, h);
        } else overlay = color.getOverlay();

        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(imageIn, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "PNG", out);
        return out;
    }

    /**
     * Изменяет размер картинки.
     *
     * @param in       входная картинка, размер которой будет изменен.
     * @param widthTo  выходная ширина картинки.
     * @param heightTo выходная высота картинки.
     * @return картинку {@code in}, подстроенную под нужный размер.
     */
    private BufferedImage resize(final BufferedImage in, int widthTo, int heightTo) {
        BufferedImage outputImage = new BufferedImage(widthTo, heightTo, in.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(in, 0, 0, widthTo, heightTo, null);
        g2d.dispose();

        return outputImage;
    }
}
