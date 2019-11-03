package enframer.display.util;

import javafx.scene.control.TextFormatter;

import java.util.regex.Pattern;

/**
 * Форматтер, который по шаблону на входе проверяет, разрешается ли вводить тот или иной символ в привязанное текстовое поле.
 */
public class PatternTextFormatter<T> extends TextFormatter<T> {
    public PatternTextFormatter(Pattern pattern) {
        super(change -> pattern.matcher(change.getControlNewText()).matches() ? change : null);
    }
}
