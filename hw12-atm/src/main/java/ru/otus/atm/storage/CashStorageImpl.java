package ru.otus.atm.storage;

import java.util.EnumMap;
import java.util.Map;
import ru.otus.atm.Denomination;
import ru.otus.atm.cell.Cell;
import ru.otus.atm.cell.CellImpl;

/**
 * Реализация хранилища денежных средств банкомата.
 */
public class CashStorageImpl implements CashStorage {

    private final Map<Denomination, Cell> cells;

    public CashStorageImpl() {
        this.cells = new EnumMap<>(Denomination.class);
        for (Denomination denomination : Denomination.values()) {
            cells.put(denomination, new CellImpl(denomination));
        }
    }

    @Override
    public void addBanknotes(Denomination denomination, int count) {
        cells.get(denomination).add(count);
    }

    @Override
    public void withdrawBanknotes(Denomination denomination, int count) {
        cells.get(denomination).withdraw(count);
    }

    @Override
    public long getTotalBalance() {
        return cells.values().stream().mapToLong(Cell::getTotal).sum();
    }

    @Override
    public int getBanknoteCount(Denomination denomination) {
        return cells.get(denomination).getCount();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Хранилище{\n");
        for (Denomination denomination : Denomination.values()) {
            Cell cell = cells.get(denomination);
            if (cell.getCount() > 0) {
                sb.append("  ").append(cell).append("\n");
            }
        }
        sb.append("  Общий баланс: ").append(getTotalBalance()).append("\n}");
        return sb.toString();
    }
}
