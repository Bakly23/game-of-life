package ru.sberbank.bit.concurrency.kolpakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.WIDTH_OF_COLUMN;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.calcLineSizeInLongs;

public class GameOfLifeField {
    private static final long[] masks = new long[64];
    private static final long[] currMasks = new long[64];

    static {
        for (int i = 1; i < 63; i++) {
            int shift = WIDTH_OF_COLUMN - i;
            masks[i] = 0b111L << shift;
            currMasks[i] = 0b101L << shift;
        }
    }

    private final long[][] oldField;
    private final long[][] field;
    private final int size;
    private final int lastLongBitToRead;

    public GameOfLifeField(long[][] field, int size) {
        this(new long[calcLineSizeInLongs(size)][size + 2], size, field);
    }

    public GameOfLifeField(long[][] field, int size, long[][] oldField) {
        this.oldField = oldField;
        this.field = field;
        this.size = size;
        this.lastLongBitToRead = Math.min(size, WIDTH_OF_COLUMN);
    }

    public long[][] getOldField() {
        return oldField;
    }

    public long[][] getField() {
        return field;
    }

    public int getSize() {
        return size;
    }

    public void calcColumn(int i) {
        for (int j = 1; j < size + 1; j++) {
            field[i][j] = calcLong(oldField[i][j - 1], oldField[i][j], oldField[i][j + 1]);
        }
        field[i][0] = field[i][size];
        field[i][size + 1] = field[i][1];
    }

    private long calcLong(long prev, long curr, long next) {
        long result = 0L;
        for (int i = 1; i <= lastLongBitToRead; i++) {
            long cellValue = getValue(curr, i);
            long aroundCellValue = countBits(prev, i) + countCurrBits(curr, i) + countBits(next, i);
            long isAlive = (isBirthOfCell(cellValue, aroundCellValue) || isCellSurvived(cellValue, aroundCellValue))
                    ? 1L
                    : 0L;
            result |= isAlive << (Long.SIZE - i - 1);
        }
        return result;
    }

    public void finalize(int i) {
        if (i == field.length - 1) {
            if (i == 0) {
                finalizeFirst();
            } else {
                finalizeLeft(i);
            }
            finalizeLast();
        } else if (i == 0) {
            finalizeRight(i);
            finalizeFirst();
        } else {
            finalizeLeft(i);
            finalizeRight(i);
        }
    }

    private void finalizeLast() {
        int indexOfLastBitInLastLong = getIndexOfLastBitInLastLong();
        finalize(field.length - 1, 0, neighbourLong -> (neighbourLong & (1L << 62)) >>> indexOfLastBitInLastLong);
    }

    private void finalizeFirst() {
        int indexOfLastBitInLastLong = getIndexOfLastBitInLastLong();
        int shiftToGetLastBit = Long.SIZE - indexOfLastBitInLastLong - 1;
        finalize(0, field.length - 1, neighbourLong -> (neighbourLong & (1L << shiftToGetLastBit)) << indexOfLastBitInLastLong);
    }

    private int getIndexOfLastBitInLastLong() {
        return ((size - 1) % WIDTH_OF_COLUMN) + 1;
    }


    private void finalizeLeft(int i) {
        finalize(i, i - 1, neighbourLong -> (neighbourLong & 2L) << 62);
    }

    private void finalizeRight(int i) {
        finalize(i, i + 1, neighbourLong -> (neighbourLong >>> 62) & 1L);
    }

    private void finalize(int i, int neighbor, LongUnaryOperator finalizedBitLongFunc) {
        for (int j = 1; j < size + 1; j++) {
            field[i][j] |= finalizedBitLongFunc.applyAsLong(field[neighbor][j]);
        }
        field[i][0] = field[i][size];
        field[i][size + 1] = field[i][1];
    }

    private boolean isCellSurvived(long cellValue, long aroundCellValue) {
        return cellValue == 1 && (aroundCellValue == 2 || aroundCellValue == 3);
    }

    private boolean isBirthOfCell(long cellValue, long aroundCellValue) {
        return cellValue == 0 && aroundCellValue == 3;
    }

    private int countCurrBits(long curr, int i) {
        return Long.bitCount(curr & currMasks[i]);
    }


    private int countBits(long lineInLong, int j) {
        return Long.bitCount(lineInLong & masks[j]);
    }

    private long getValue(long field, int shift) {
        return (field >>> (Long.SIZE - shift - 1)) & 1;
    }

    public long getValueInCell(int i, int j) {
        int longColumn = j / WIDTH_OF_COLUMN;
        int positionOfBitInLong = 1 + (j % WIDTH_OF_COLUMN);
        int shift = Long.SIZE - positionOfBitInLong - 1;
        return (oldField[longColumn][i + 1] >>> shift) & 1L;
    }
}
