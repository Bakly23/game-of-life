package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ONE;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ZERO;
import static ru.sberbank.bit.concurrency.kolpakov.TestUtils.getHugeTestData;
import static ru.sberbank.bit.concurrency.kolpakov.TestUtils.getTestData;

public class GameOfLifeFieldTest {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    @Test
    public void testGet() {
        GameOfLifeField field = new GameOfLifeField(getTestData(), 5);
        assertEquals(1, field.getValue(0, 0));
        assertEquals(0, field.getValue(0, 1));
        assertEquals(1, field.getValue(1, 1));
        assertEquals(1, field.getValue(3, 1));
        assertEquals(0, field.getValue(3, 3));
    }

    @Test
    public void testCalculate() {
        GameOfLifeField field = new GameOfLifeField(getTestData(), 5);
        //survive
        assertEquals(1, field.calc(0, 0));
        assertEquals(1, field.calc(3, 2));
        //stay dead
        assertEquals(0, field.calc(0, 3));
        //die of exposure
        assertEquals(0, field.calc(1, 1));
        //die of overcrowding
        assertEquals(0, field.calc(3, 1));
    }

    @Test
    public void testGetForHugeTestData() {
        int size = 400_000_000;
        int fieldSize = Double.valueOf(Math.sqrt(size)).intValue();
        char[] testData = getHugeTestData(size);
        log.info("test data generated");
        long[] readData = new CellInputReader(4).read(testData);
        log.info("converted from char to long");
        GameOfLifeField field = new GameOfLifeField(readData, fieldSize);
        log.info("loaded into field");
        char[] writtenData = new GameOfLifeFieldWriter().write(field);
        log.info("convertted from char to long");
        assertTrue(Arrays.equals(writtenData, testData));
    }
}
