package enframer.common;

import enframer.exception.CrashReport;
import enframer.exception.ReportedException;
import enframer.util.FileUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public enum RarityCategory {
    COMMON("Обычный", 0),
    UNCOMMON("Необычный", 1, FileUtilities.getStreamFor("overlay/blue.png")),
    RARE("Редкий", 2, FileUtilities.getStreamFor("overlay/green.png")),
    EPIC("Эпический", 3, FileUtilities.getStreamFor("overlay/purple.png")),
    LEGENDARY("Легендарный", 4, FileUtilities.getStreamFor("overlay/orange.png"));

    private String name;
    private int rarity;
    private OverlayColor overlayColor;

    RarityCategory(String name, int rarity) {
        this(name, rarity, null);
    }

    RarityCategory(String name, int rarity, @Nullable InputStream overlayImageStream) {
        this.name = name;
        this.rarity = rarity;

        if (overlayImageStream != null) {
            this.overlayColor = new OverlayColor(overlayImageStream);
        }
    }

    public int getRarityFactor() {
        return rarity;
    }

    public String getName() {
        return name;
    }

    public OverlayColor getOverlayColor() {
        return overlayColor;
    }

    public boolean hasOverlayColor() {
        return getOverlayColor() != null;
    }

    public class OverlayColor {
        private BufferedImage overlay;

        private OverlayColor(@NotNull InputStream stream) {
            try {
                this.overlay = ImageIO.read(stream);
            } catch (IOException e) {
                throw new ReportedException(CrashReport.makeCrashReport(e, "Невозможно создать цветной оверлей в RarityCategory."));
            }
        }

        public BufferedImage getOverlay() {
            return overlay;
        }
    }
}
