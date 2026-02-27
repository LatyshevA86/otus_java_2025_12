package ru.otus.atm.cell;

import ru.otus.atm.Denomination;

/**
 * Интерфейс ячейки банкомата для хранения банкнот одного номинала.
 */
public interface Cell {

    /**
     * Получить номинал банкнот в ячейке.
     */
    Denomination getDenomination();

    /**
     * Получить количество банкнот в ячейке.
     */
    int getCount();

    /**
     * Добавить банкноты в ячейку.
     *
     * @param count количество банкнот для добавления
     */
    void add(int count);

    /**
     * Изъять банкноты из ячейки.
     *
     * @param count количество банкнот для изъятия
     */
    void withdraw(int count);

    /**
     * Получить общую сумму в ячейке.
     */
    default long getTotal() {
        return (long) getDenomination().getValue() * getCount();
    }
}
