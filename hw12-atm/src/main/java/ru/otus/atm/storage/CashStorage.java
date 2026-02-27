package ru.otus.atm.storage;

import ru.otus.atm.Denomination;

/**
 * Интерфейс хранилища денежных средств банкомата.
 */
public interface CashStorage {

    /**
     * Добавить банкноты указанного номинала.
     *
     * @param denomination номинал
     * @param count количество банкнот
     */
    void addBanknotes(Denomination denomination, int count);

    /**
     * Изъять банкноты указанного номинала.
     *
     * @param denomination номинал
     * @param count количество банкнот
     */
    void withdrawBanknotes(Denomination denomination, int count);

    /**
     * Получить общий остаток денежных средств.
     */
    long getTotalBalance();

    /**
     * Получить количество банкнот указанного номинала.
     */
    int getBanknoteCount(Denomination denomination);
}
