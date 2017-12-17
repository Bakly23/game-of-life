package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.sberbank.bit.concurrency.kolpakov.TestUtils.writerOutputToCharArray;


public class CellInputReaderTest {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    private final CellInputReader cellInputReader = new CellInputReader();

    @Test
    public void testShort() {
        long[][] result = cellInputReader.readFile("src/main/resources/input.txt");
        assertEquals(1, cellInputReader.getNumberOfTries());
        assertEquals(9, cellInputReader.getFieldSize());
        assertEquals(1, result.length);
        assertEquals(11, result[0].length);
        assertEquals(0, result[0][0]);
        assertEquals(0, result[0][1]);
        assertEquals(7L << 57, result[0][5]);
    }

    @Test
    public void testUsual() {
        long[][] result = cellInputReader.readFile("src/main/resources/input100.txt");
        assertEquals(10, cellInputReader.getNumberOfTries());
        assertEquals(100, cellInputReader.getFieldSize());
        assertEquals(2, result.length);
        assertEquals(102, result[0].length);
        long firstLongColFirstRow = 0b1001001100000000000001100011100100001101100001110000010000000000L;
        assertEquals(firstLongColFirstRow, result[0][1]);
        assertEquals(firstLongColFirstRow, result[0][101]);
        long firstLongColSecondRow = 0b0100001100001000101001001101110001110000010100100100010000111101L;
        assertEquals(firstLongColSecondRow, result[0][2]);
        long lastLongColLastRow = 0b1100100000000000011001000100010010000010L << 24;
        assertEquals(lastLongColLastRow, result[1][100]);
        assertEquals(lastLongColLastRow, result[1][0]);
        assertEquals(0b0100110010010101000001010000000100000001L << 24, result[1][98]);
    }
}
