package enframer.display.util;

import javafx.scene.control.TextFormatter;

import java.util.regex.Pattern;

public class PatternTextFormatter<T> extends TextFormatter<T> {
    public PatternTextFormatter(Pattern pattern) {
        super(change -> pattern.matcher(change.getControlNewText()).matches() ? change : null);
    }
}
