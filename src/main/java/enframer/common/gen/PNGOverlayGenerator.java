package enframer.common.gen;

import enframer.common.RarityCategory;
import enframer.util.FileUtilities;
import enframer.util.wrapper.InputImageWrapper;
import enframer.util.wrapper.OutputFileWrapper;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PNGOverlayGenerator implements IFileGenerator {
    private InputImageWrapper imageWrapper;
    private HashMap<String, OutputFileWrapper> outWrappers = new HashMap<>();

    public PNGOverlayGenerator(File fileIn, File dirOut) {
        this.imageWrapper = new InputImageWrapper(fileIn);

        for (RarityCategory rarityCategory : RarityCategory.values()) {
            if (rarityCategory.hasOverlayColor()) {
                File fileOut = new File(dirOut + "/" + FileUtilities.getFileName(imageWrapper.getContent()) + "_" + rarityCategory.getName().toLowerCase() + ".png");
                outWrappers.put(rarityCategory.getName().toLowerCase(), new OutputFileWrapper(fileOut));
            }
        }
    }

    @Override
    @NotNull
    public String initWithMessage() {
        String i = imageWrapper.checkValid();
        if (i.isEmpty()) {
            String s = "";
            for (Map.Entry<String, OutputFileWrapper> wrapperEntry : outWrappers.entrySet()) {
                if (!s.isEmpty()) {
                    return s;
                }

                s = wrapperEntry.getValue().checkValid();
            }
            return s;
        } else {
            return i;
        }
    }

    @Override
    public void gen() {
        for (RarityCategory rarity : RarityCategory.values()) {
            try {
                if (rarity.hasOverlayColor()) {
                    RarityCategory.OverlayColor color = rarity.getOverlayColor();
                    ByteArrayOutputStream overlayedImage = genOverlayedImage(color);

                    File fileOut = outWrappers.get(rarity.getName().toLowerCase()).getContent();
                    FileUtils.touch(fileOut);
                    FileUtilities.writeTo(fileOut, overlayedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ByteArrayOutputStream genOverlayedImage(RarityCategory.OverlayColor color) throws IOException {
        int w = imageWrapper.getImage().getWidth();
        int h = imageWrapper.getImage().getHeight();

        BufferedImage overlay;
        if (color.getOverlay().getWidth() != w || color.getOverlay().getHeight() != h) {
            overlay = resize(color.getOverlay(), w, h);
        } else overlay = color.getOverlay();

        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(overlay, 0, 0, null);
        g.drawImage(imageWrapper.getImage(), 0, 0, null);


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
