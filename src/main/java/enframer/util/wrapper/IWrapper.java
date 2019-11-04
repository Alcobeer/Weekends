package enframer.util.wrapper;

import org.jetbrains.annotations.NotNull;

public interface IWrapper {
    /**
     * Вернет пустую строку, если объект на входе удовлетворяет требованиям враппера.
     */
    @NotNull String checkValid();
}
