package enframer.gen;

import enframer.display.controller.MainGui;
import enframer.util.RarityCategory;

import java.util.List;

public class AttributeCalculator {
    private List<MainGui.Attribute> attributes;
    private int level;
    private int rarityIn;

    public AttributeCalculator(List<MainGui.Attribute> attributes, int level, int rarityIn) {
        this.attributes = attributes;
        this.level = level;
        this.rarityIn = rarityIn;
    }

    /**
     * Высчитывает атрибуты для каждого из "цвета" редкости.
     *
     * @return форматированную строку с высчитанными атрибутами, пригодную для чтения в консоли или записи в файл.
     */
    public String calcAttributeForAllCategories() {
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
                double rarityFactor = Math.pow(rarityCategory.getRarityFactor(), rarityIn == 0 ? 1 : Math.exp(rarityIn / 10D));
                int valueOut = (int) (initialValue + rarityFactor * Math.max(1, level / 5D));//TODO проверить

                b.append("   ").append(attribute.nameProperty().get()).append(": +").append(valueOut).append("\n");
            }

            return b.toString();
        }
    }
}
