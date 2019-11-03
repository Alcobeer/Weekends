package enframer.gen;

import enframer.exception.CrashReport;
import enframer.exception.ReportedException;
import enframer.util.FileUtilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PNGOverlayCreator {
    private BufferedImage imageIn;

    public PNGOverlayCreator(BufferedImage imageIn) {
        this.imageIn = imageIn;
    }

    public ByteArrayOutputStream genOverlayedImage(OverlayColor color) throws IOException {
        int w = imageIn.getWidth();
        int h = imageIn.getHeight();

        BufferedImage overlay;
        if (color.overlay.getWidth() != w || color.overlay.getHeight() != h) {
            overlay = resize(color.overlay, w, h);
        } else overlay = color.overlay;

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

    public enum OverlayColor {
        BLUE("blue", FileUtilities.getStreamFor("overlay/blue.png")),
        GREEN("green", FileUtilities.getStreamFor("overlay/green.png")),
        PURPLE("purple", FileUtilities.getStreamFor("overlay/purple.png")),
        ORANGE("orange", FileUtilities.getStreamFor("overlay/orange.png"));

        private BufferedImage overlay;
        private String name;

        OverlayColor(String name, InputStream stream) {
            try {
                this.overlay = ImageIO.read(stream);
            } catch (IOException e) {
                throw new ReportedException(CrashReport.makeCrashReport(e, "Невозможно создать перечисление цветов в PNGOverlayCreator."));
            }

            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
