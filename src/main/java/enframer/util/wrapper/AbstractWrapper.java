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
     */
    @Override
    public final @NotNull String checkValid() {
        if (!isChecked()) {
            validResult = checkValidImpl();
        }

        return validResult;
    }

    /**
     * Вернет пустую строку, если объект на входе удовлетворяет требованиям враппера.
     */
    protected abstract @NotNull String checkValidImpl();

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
