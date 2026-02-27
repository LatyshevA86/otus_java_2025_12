package ru.otus;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.ATM;
import ru.otus.atm.ATMImpl;
import ru.otus.atm.Denomination;
import ru.otus.atm.exception.ATMException;
import ru.otus.atm.storage.CashStorage;
import ru.otus.atm.storage.CashStorageImpl;
import ru.otus.atm.strategy.MinBanknotesWithdrawStrategy;
import ru.otus.atm.strategy.WithdrawStrategy;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CashStorage storage = new CashStorageImpl();
        WithdrawStrategy strategy = new MinBanknotesWithdrawStrategy();
        ATM atm = new ATMImpl(storage, strategy);

        logger.info("=== Демонстрация работы банкомата ===\n");

        logger.info("1. Внесение банкнот разных номиналов");
        atm.deposit(Denomination.FIVE_THOUSAND, 10); // 50 000
        atm.deposit(Denomination.TWO_THOUSAND, 10); // 20 000
        atm.deposit(Denomination.ONE_THOUSAND, 20); // 20 000
        atm.deposit(Denomination.FIVE_HUNDRED, 30); // 15 000
        atm.deposit(Denomination.TWO_HUNDRED, 50); // 10 000
        atm.deposit(Denomination.ONE_HUNDRED, 100); // 10 000
        atm.deposit(Denomination.FIFTY, 100); // 5 000

        logger.info("Текущий баланс банкомата: {} руб.\n", atm.getBalance());

        logger.info("2. Выдача 8750 руб.");
        try {
            Map<Denomination, Integer> withdrawn = atm.withdraw(8750);
            printWithdrawal(withdrawn);
            logger.info("Баланс после выдачи: {} руб.\n", atm.getBalance());
        } catch (ATMException e) {
            logger.error("Ошибка: {}\n", e.getMessage());
        }

        logger.info("3. Выдача 27500 руб.");
        try {
            Map<Denomination, Integer> withdrawn = atm.withdraw(27500);
            printWithdrawal(withdrawn);
            logger.info("Баланс после выдачи: {} руб.\n", atm.getBalance());
        } catch (ATMException e) {
            logger.error("Ошибка: {}\n", e.getMessage());
        }

        logger.info("4. Попытка выдать 123 руб. (невозможно набрать)");
        try {
            atm.withdraw(123);
        } catch (ATMException e) {
            logger.error("Ожидаемая ошибка: {}\n", e.getMessage());
        }

        logger.info("5. Попытка выдать 1 000 000 руб.");
        try {
            atm.withdraw(1_000_000);
        } catch (ATMException e) {
            logger.error("Ожидаемая ошибка: {}\n", e.getMessage());
        }

        logger.info("6. Внесение нескольких номиналов одновременно");
        atm.deposit(Map.of(
                Denomination.FIVE_THOUSAND, 5,
                Denomination.ONE_THOUSAND, 10,
                Denomination.ONE_HUNDRED, 50));
        logger.info("Итоговый баланс банкомата: {} руб.", atm.getBalance());
    }

    private static void printWithdrawal(Map<Denomination, Integer> withdrawn) {
        logger.info("Выдано:");
        withdrawn.forEach((denomination, count) ->
                logger.info("  {} x {} = {} руб.", denomination.getValue(), count, denomination.getValue() * count));

        int totalBanknotes =
                withdrawn.values().stream().mapToInt(Integer::intValue).sum();
        long totalAmount = withdrawn.entrySet().stream()
                .mapToLong(e -> (long) e.getKey().getValue() * e.getValue())
                .sum();
        logger.info("  Итого: {} банкнот на сумму {} руб.", totalBanknotes, totalAmount);
    }
}
