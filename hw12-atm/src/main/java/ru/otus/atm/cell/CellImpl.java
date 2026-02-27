package ru.otus.atm.cell;

import ru.otus.atm.Denomination;
import ru.otus.atm.exception.ATMException;

/**
 * Реализация ячейки банкомата.
 */
public class CellImpl implements Cell {

    private final Denomination denomination;
    private int count;

    public CellImpl(Denomination denomination) {
        this(denomination, 0);
    }

    public CellImpl(Denomination denomination, int initialCount) {
        this.denomination = denomination;
        this.count = initialCount;
    }

    @Override
    public Denomination getDenomination() {
        return denomination;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void add(int count) {
        if (count <= 0) {
            throw new ATMException("Количество должно быть положительным");
        }
        this.count += count;
    }

    @Override
    public void withdraw(int count) {
        if (count <= 0) {
            throw new ATMException("Количество должно быть положительным");
        }
        if (count > this.count) {
            throw new ATMException("Недостаточно банкнот в ячейке. Запрошено: " + count + ", доступно: " + this.count);
        }
        this.count -= count;
    }

    @Override
    public String toString() {
        return "Ячейка{" + denomination.getValue() + ": " + count + " банкнот, всего: " + getTotal() + "}";
    }
}
