package enframer.display;

import javafx.util.converter.IntegerStringConverter;

/**
 * Конвертер из {@link Integer} в {@link String} и обратно.
 * В случае, если при попытке конвертации строки в число мы получим null, то вернётся значение, заданное по умолчанию {@link #startDigit}.
 */
public class IntConverterWithDefault extends IntegerStringConverter {
    /**
     * Значение, которое будет возвращать конвертер в случае получения null при попытке конвертации строки в число.
     */
    private int startDigit;

    public IntConverterWithDefault(int startDigit) {
        this.startDigit = startDigit;
    }

    @Override
    public Integer fromString(String value) {
        Integer i = super.fromString(value);

        return i == null ? startDigit : i;
    }
}
