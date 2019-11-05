package enframer.common.gen;

import enframer.common.RarityCategory;
import enframer.display.controller.MainGui;
import enframer.util.wrapper.OutputFileWrapper;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AttributeFileGenerator implements IFileGenerator {
    private List<MainGui.Attribute> attributes;
    private int level;
    private int rarityIn;
    private OutputFileWrapper fileOutWrapper;

    public AttributeFileGenerator(List<MainGui.Attribute> attributes, int level, int rarityIn, File dirOut) {
        this.attributes = attributes;
        this.level = level;
        this.rarityIn = rarityIn;
        this.fileOutWrapper = new OutputFileWrapper(new File(dirOut + "/Атрибуты.txt"));
    }

    @Override
    public @NotNull String initWithMessage() {
        return fileOutWrapper.checkValid();
    }

    @Override
    public void gen() {
        String out = calcAttributeForAllCategories();
        try {
            FileUtils.touch(fileOutWrapper.getContent());
            FileUtils.writeStringToFile(fileOutWrapper.getContent(), out, StandardCharsets.UTF_8, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Высчитывает атрибуты для каждого из "цвета" редкости.
     *
     * @return форматированную строку с высчитанными атрибутами, пригодную для чтения в консоли или записи в файл.
     */
    private String calcAttributeForAllCategories() {
        StringBuilder b = new StringBuilder();
        for (RarityCategory value : RarityCategory.values()) {
            AttributeCategory cat = new AttributeCategory(value);
            b.append(cat.writeCategoryAttributes());
        }
        return b.toString();
    }

    private class AttributeCategory {
        private RarityCategory rarityCategory;

        private AttributeCategory(RarityCategory rarityCategory) {
            this.rarityCategory = rarityCategory;
        }

        private String writeCategoryAttributes() {
            StringBuilder b = new StringBuilder("======= " + rarityCategory.getName() + " =======\n");
            for (MainGui.Attribute attribute : attributes) {
                int initialValue = attribute.valueProperty().get();


                int valueOut=initialValue;
                for(int i = 0; i < rarityCategory.getRarityFactor() ; i++){

                    valueOut += Math.ceil(initialValue / 100F * rarityIn) * level;

              }


                b.append("   ").append(attribute.nameProperty().get()).append(": +").append(valueOut).append("\n");
            }

            return b.toString();
        }
    }
}
