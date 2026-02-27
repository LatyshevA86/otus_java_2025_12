package ru.otus.atm.strategy;

import java.util.Map;
import ru.otus.atm.Denomination;
import ru.otus.atm.storage.CashStorage;

/**
 * Интерфейс стратегии выдачи денежных средств.
 */
public interface WithdrawStrategy {

    /**
     * Рассчитать набор банкнот для выдачи запрошенной суммы.
     *
     * @param amount сумма для выдачи
     * @param storage хранилище с банкнотами
     * @return Map количество банкнот для выдачи
     */
    Map<Denomination, Integer> calculateWithdrawal(long amount, CashStorage storage);
}
