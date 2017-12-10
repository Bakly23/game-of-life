package ru.sberbank.bit.concurrency.kolpakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameOfLifeField {
    public static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    private final long[] field;
    private final int size;

    public GameOfLifeField(long[] field, int size) {
        this.field = field;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public long calc(int i, int j) {
        if (i >= getSize()) {
            throw new IllegalArgumentException("first index is greater or equal than width of the field");
        }
        if (j >= getSize()) {
            throw new IllegalArgumentException("second index is greater or equal than height of the field");
        }
        long cellValue = getValue(i, j);
        long aroundCellValue = getValue(i - 1, j - 1)
                + getValue(i - 1, j)
                + getValue(i - 1, j + 1)
                + getValue(i, j - 1)
                + getValue(i, j + 1)
                + getValue(i + 1, j - 1)
                + getValue(i + 1, j)
                + getValue(i + 1, j + 1);
        if (isBirthOfCell(cellValue, aroundCellValue) || isCellSurvived(cellValue, aroundCellValue)) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isCellSurvived(long cellValue, long aroundCellValue) {
        return cellValue == 1 && (aroundCellValue == 2 || aroundCellValue == 3);
    }

    private boolean isBirthOfCell(long cellValue, long aroundCellValue) {
        return cellValue == 0 && aroundCellValue == 3;
    }

    public long getValue(int i, int j) {
        int size = getSize();
        int jMod = Math.floorMod(j, size);
        int longIndex = (jMod / Long.SIZE) * size + Math.floorMod(i, size);
        int bitIndex = jMod % Long.SIZE;
        return (field[longIndex] >> (Long.SIZE - bitIndex - 1)) & 1;
    }
}
