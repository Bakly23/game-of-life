package ru.sberbank.bit.concurrency.kolpakov;

import java.util.Random;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ONE;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ZERO;

public class TestUtils {
    private static final Random rand = new Random();

    public static long[] getTestData() {
        /**
         * 1 0 0 0 0
         * 0 1 0 0 0
         * 0 0 0 1 0
         * 1 1 1 0 0
         * 1 0 1 0 0
         */
        return new long[]{1L << 63, 1L << 62, 1L << 60, 1L << 63 | 1L << 62 | 1L << 61, 1L << 63 | 1L << 61};
    }

    public static char[] getHugeTestData(int size) {
        char[] testData = new char[size];
        for (int i = 0; i < size; i++) {
            int randInt = rand.nextInt(10);
            if (randInt > 6) {
                testData[i] = ONE;
            } else {
                testData[i] = ZERO;
            }
        }
        return testData;
    }
}
