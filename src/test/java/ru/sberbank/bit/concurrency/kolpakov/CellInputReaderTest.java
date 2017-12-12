package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static ru.sberbank.bit.concurrency.kolpakov.TestUtils.writerOutputToCharArray;


public class CellInputReaderTest {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    private final CellInputReader cellInputReader = new CellInputReader(4);

    @Test
    public void testShort() {
        char[] testData = "100110001".toCharArray();
        long[] result = cellInputReader.read(testData);
        char[] writtenData = writerOutputToCharArray(new GameOfLifeFieldWriter()
                .write(new GameOfLifeField(result, 3)));
        assertTrue(Arrays.equals(testData, writtenData));
    }
}
