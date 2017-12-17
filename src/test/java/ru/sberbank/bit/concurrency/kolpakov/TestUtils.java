package ru.sberbank.bit.concurrency.kolpakov;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ONE;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ZERO;

public class TestUtils {
    private static final Random rand = new Random();

    public static long[][] getTestData() {
        /**
         * 0 1 0 1 0 0 1
         *  __________
         * 1|1 0 0 0 1|1
         * 0|0 1 0 0 0|0
         * 0|0 0 0 1 0|0
         * 0|1 1 1 0 0|1
         * 0|1 0 1 0 0|1
         *  __________
         * 1 1 0 0 0 1 1
         */

        //after a turn

        /**
         * 0 0 0 1 1 0 0
         *  __________
         * 1|1 0 1 0 1|1
         * 1|1 0 0 0 1|1
         * 0|1 0 0 0 0|1
         * 1|1 0 1 1 1|1
         * 0|0 0 1 1 0|0
         *  __________
         * 1 1 0 1 0 1 1
         */
        long[][] testData = new long[1][7];
        testData[0][1] = 0b1100011L << 57;
        testData[0][2] = 1L << 61;
        testData[0][3] = 1L << 59;
        testData[0][4] = 0b111001L << 57;
        testData[0][5] = 0b101001L << 57;

        testData[0][0] = testData[0][5];
        testData[0][6] = testData[0][1];
        return testData;
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

    public static char[] writerOutputToCharArray(List<String> writerOutput) {
        return writerOutput.stream()
                .collect(Collectors.joining(""))
                .toCharArray();
    }
}
