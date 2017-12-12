package ru.sberbank.bit.concurrency.kolpakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.calcLineSizeInLongs;

public class GameOfLifeField {
    public static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);
    private static final long[] masks = new long[63];
    static {
        for (int i = 0; i < 63; i++) {
            masks[i] = 7L << (Long.SIZE - i - 3);
        }
    }

    private final long[] oldField;
    private final long[] field;
    private final int size;
    private final int border;

    public GameOfLifeField(long[] field, int size) {
        this(field, size, new long[calcLineSizeInLongs(size) * size]);
    }

    public GameOfLifeField(long[] field, int size, long[] oldField) {
        this.oldField = oldField;
        this.field = field;
        this.size = size;
        this.border = size - 1;
    }

    public long[] getOldField() {
        return oldField;
    }

    public long[] getField() {
        return field;
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
        long aroundCellValue;
        if(isSafeCalc(i, j)) {
            int jToCountsBitsFor = j - 1;
            aroundCellValue = countBits(i - 1, jToCountsBitsFor)
                    + countBits(i, jToCountsBitsFor)
                    + countBits(i + 1, jToCountsBitsFor)
                    - cellValue;
        } else {
            aroundCellValue = getSafeValue(i - 1, j - 1)
                    + getSafeValue(i - 1, j)
                    + getSafeValue(i - 1, j + 1)
                    + getSafeValue(i, j - 1)
                    + getSafeValue(i, j + 1)
                    + getSafeValue(i + 1, j - 1)
                    + getSafeValue(i + 1, j)
                    + getSafeValue(i + 1, j + 1);

        }
        if (isBirthOfCell(cellValue, aroundCellValue) || isCellSurvived(cellValue, aroundCellValue)) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isSafeCalc(int i, int j) {
        return j % Long.SIZE != 0 && j % Long.SIZE != 63 && i != 0 && i != border && j != 0 && j != border;
    }

    private boolean isCellSurvived(long cellValue, long aroundCellValue) {
        return cellValue == 1 && (aroundCellValue == 2 || aroundCellValue == 3);
    }

    private boolean isBirthOfCell(long cellValue, long aroundCellValue) {
        return cellValue == 0 && aroundCellValue == 3;
    }

    public int countBits(int i, int j) {
        return Long.bitCount(field[(j / Long.SIZE) * size + i] & masks[j % Long.SIZE]);
    }

    public long getSafeValue(int i, int j) {
        return getValue(Math.floorMod(i, size), Math.floorMod(j, size));
    }

    public long getValue(int i, int j) {
        int size = getSize();
        int longIndex = (j / Long.SIZE) * size + i;
        int bitIndex = j % Long.SIZE;
        return (field[longIndex] >> (Long.SIZE - bitIndex - 1)) & 1;
    }
}
