package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static ru.sberbank.bit.concurrency.kolpakov.TestUtils.getHugeTestData;
import static ru.sberbank.bit.concurrency.kolpakov.TestUtils.getTestData;

public class GameOfLifeFieldTransformerTest {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    @Test
    public void testTransform() {
        GameOfLifeField field = new GameOfLifeField(getTestData(), 5);
        GameOfLifeFieldTransformer transformer = new GameOfLifeFieldTransformer(4);
        GameOfLifeField transformedField = transformer.generateNextField(field);
        log.info("transformed values: {}", new GameOfLifeFieldWriter().write(transformedField));
        //survived
        assertEquals(1, transformedField.getValue(0, 0));
        assertEquals(1, transformedField.getValue(3, 2));
        //stayed dead
        assertEquals(0, transformedField.getValue(0, 3));
        //died of exposure
        assertEquals(0, transformedField.getValue(1, 1));
        //died of overcrowding
        assertEquals(0, transformedField.getValue(3, 1));
    }

    @Test
    public void runHugeTransform() {
        int size = 10_000;
        int T = 1_0;
        int fieldSize = Double.valueOf(Math.sqrt(size)).intValue();
        char[] testData = getHugeTestData(size);
        long[] readData = new CellInputReader(4).read(testData);
        GameOfLifeField field = new GameOfLifeField(readData, fieldSize);
        GameOfLifeFieldTransformer transformer = new GameOfLifeFieldTransformer(4);
        for (int i = 0; i < T; i++) {
            if ((10 * i) % T == 0) {
                log.info("{}/{} transofrmations has been passed", i, T);
            }
            field = transformer.generateNextField(field);
        }
        log.info("all {} transofrmations has been passed", T);
    }
}
