package ru.otus.atm;

import java.util.Map;

/**
 * Интерфейс банкомата.
 */
public interface ATM {

    /**
     * Внести банкноты указанного номинала.
     *
     * @param denomination номинал банкноты
     * @param count количество банкнот
     */
    void deposit(Denomination denomination, int count);

    /**
     * Внести несколько банкнот разных номиналов.
     *
     * @param banknotes Map количество банкнот
     */
    void deposit(Map<Denomination, Integer> banknotes);

    /**
     * Выдать запрошенную сумму минимальным количеством банкнот.
     *
     * @param amount запрошенная сумма
     * @return Map количество выданных банкнот
     */
    Map<Denomination, Integer> withdraw(long amount);

    /**
     * Получить текущий остаток денежных средств в банкомате.
     *
     * @return сумма всех денежных средств
     */
    long getBalance();
}
