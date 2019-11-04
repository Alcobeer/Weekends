package enframer.util.wrapper;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractWrapper<T> implements IWrapper {
    private T content;
    private String validResult = null;

    public AbstractWrapper(T content) {
        this.content = content;
    }

    /**
     * Оптимизированным образом проверяет валидность объекта внутри враппера.
     * Метод не должен быть переопределен.
     */
    @Override
    public @NotNull String checkValid() {
        if (!isChecked()) {
            validResult = checkValidImpl();
        }

        return validResult;
    }

    /**
     * Вернет пустую строку, если объект на входе удовлетворяет требованиям враппера.
     */
    abstract @NotNull String checkValidImpl();

    /**
     * Возвращает объект, соедржащийся в враппере.
     */
    public T getContent() {
        return content;
    }

    private boolean isChecked() {
        return validResult != null;
    }
}
