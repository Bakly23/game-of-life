package ru.sberbank.bit.concurrency.kolpakov;

public final class GameOfLifeUtils {
    public static final char ONE = '1';
    public static final char ZERO = '0';

    private GameOfLifeUtils() {
    }

    public static int calcLineSizeInLongs(int lineSize) {
        return lineSize / Long.SIZE + 1;
    }

    public static long getOneBitAtPosition(int i) {
        return 1L << (Long.SIZE - i - 1);
    }

    public static long convertToLong(char[] cellInput, int from, int to) {
        long result = 0;
        for (int j = from; j < to; j++) {
            if (cellInput[j] == ONE) {
                result |= getOneBitAtPosition(j - from);
            } else if (cellInput[j] == ZERO) {
                //do nothing
            } else {
                throw new IllegalArgumentException("char input array can't contain elements apart from '0' and '1'");
            }
        }
        return result;
    }
}
