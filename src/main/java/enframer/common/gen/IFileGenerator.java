package enframer.common.gen;

import org.jetbrains.annotations.NotNull;

public interface IFileGenerator {
    /**
     * Инициализация объекта. Здесь можно проверять все входные данные, поступающие в объект.
     * Если входные данные верны, необходимо вернуть пустую строку.
     * Если входные данные неверны, необходимо вернуть сообщение, описывающее проблему.
     */
    @NotNull String initWithMessage();

    /**
     * Метод для запуска генерации, должен вызываться после метода {@link #initWithMessage()}
     */
    void gen();
}
