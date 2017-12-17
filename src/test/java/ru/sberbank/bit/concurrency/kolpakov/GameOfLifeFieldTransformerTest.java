package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class GameOfLifeFieldTransformerTest {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    @Test
    public void transformOnceMediumTable() {
        GameOfLifeField field = new GameOfLifeField(new CellInputReader().readFile("src/main/resources/input100.txt"), 100);
        GameOfLifeFieldTransformer transformer = new GameOfLifeFieldTransformer(4);
        GameOfLifeField result = transformer.generateNextField(field);

        assertEquals(0, result.getValueInCell(0, 0));
        assertEquals(1, result.getValueInCell(99, 99));
        assertEquals(0, result.getValueInCell(29, 69));
        assertEquals(0, result.getValueInCell(29, 62));
        assertEquals(1, result.getValueInCell(28, 99));
        assertEquals(1, result.getValueInCell(79, 21));
    }
}
