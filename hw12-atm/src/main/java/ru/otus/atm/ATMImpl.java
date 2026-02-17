package ru.otus.atm;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.storage.CashStorage;
import ru.otus.atm.strategy.WithdrawStrategy;

/**
 * Реализация банкомата.
 */
public class ATMImpl implements ATM {

    private static final Logger logger = LoggerFactory.getLogger(ATMImpl.class);

    private final CashStorage storage;
    private final WithdrawStrategy withdrawStrategy;

    public ATMImpl(CashStorage storage, WithdrawStrategy withdrawStrategy) {
        this.storage = storage;
        this.withdrawStrategy = withdrawStrategy;
    }

    @Override
    public void deposit(Denomination denomination, int count) {
        logger.info("Внесение {} банкнот номиналом {}", count, denomination.getValue());
        storage.addBanknotes(denomination, count);
        logger.info("Внесение успешно. Новый баланс: {}", storage.getTotalBalance());
    }

    @Override
    public void deposit(Map<Denomination, Integer> banknotes) {
        logger.info("Внесение нескольких банкнот: {}", banknotes);
        banknotes.forEach((denomination, count) -> {
            if (count > 0) {
                storage.addBanknotes(denomination, count);
            }
        });
        logger.info("Внесение успешно. Новый баланс: {}", storage.getTotalBalance());
    }

    @Override
    public Map<Denomination, Integer> withdraw(long amount) {
        logger.info("Запрос на выдачу: {}", amount);

        Map<Denomination, Integer> toWithdraw = withdrawStrategy.calculateWithdrawal(amount, storage);

        toWithdraw.forEach(storage::withdrawBanknotes);

        logger.info("Выдача успешна: {}. Новый баланс: {}", toWithdraw, storage.getTotalBalance());
        return toWithdraw;
    }

    @Override
    public long getBalance() {
        return storage.getTotalBalance();
    }

    @Override
    public String toString() {
        return "Банкомат{баланс=" + getBalance() + ", хранилище=" + storage + "}";
    }
}
