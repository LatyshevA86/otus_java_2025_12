package ru.otus.atm.strategy;

import java.util.EnumMap;
import java.util.Map;
import ru.otus.atm.Denomination;
import ru.otus.atm.exception.ATMException;
import ru.otus.atm.storage.CashStorage;

/**
 * Стратегия выдачи минимальным количеством банкнот.
 */
public class MinBanknotesWithdrawStrategy implements WithdrawStrategy {

    @Override
    public Map<Denomination, Integer> calculateWithdrawal(long amount, CashStorage storage) {
        if (amount <= 0) {
            throw new ATMException("Сумма выдачи должна быть положительной: " + amount);
        }

        if (amount > storage.getTotalBalance()) {
            throw new ATMException("Недостаточно средств в банкомате. Запрошено: " + amount + ", доступно: "
                    + storage.getTotalBalance());
        }

        Map<Denomination, Integer> result = new EnumMap<>(Denomination.class);
        long remaining = amount;

        for (Denomination denomination : Denomination.values()) {
            if (remaining <= 0) {
                break;
            }

            int denominationValue = denomination.getValue();
            int availableCount = storage.getBanknoteCount(denomination);

            if (availableCount > 0 && denominationValue <= remaining) {
                int neededCount = (int) (remaining / denominationValue);
                int toWithdraw = Math.min(neededCount, availableCount);

                if (toWithdraw > 0) {
                    result.put(denomination, toWithdraw);
                    remaining -= (long) toWithdraw * denominationValue;
                }
            }
        }

        if (remaining > 0) {
            throw new ATMException("Невозможно выдать запрошенную сумму: " + amount
                    + ". Не удается подобрать точную сумму. Остаток: " + remaining);
        }

        return result;
    }
}
