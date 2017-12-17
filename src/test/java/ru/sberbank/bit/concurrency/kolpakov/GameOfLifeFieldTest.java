package ru.sberbank.bit.concurrency.kolpakov;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static ru.sberbank.bit.concurrency.kolpakov.TestUtils.getTestData;

public class GameOfLifeFieldTest {
    private static final Logger log = LoggerFactory.getLogger(GameOfLifeField.class);

    @Test
    public void testGet() {
        GameOfLifeField field = new GameOfLifeField(getTestData(), 5);
        assertEquals(1, field.getValueInCell(0, 0));
        assertEquals(0, field.getValueInCell(0, 1));
        assertEquals(1, field.getValueInCell(1, 1));
        assertEquals(1, field.getValueInCell(3, 1));
        assertEquals(0, field.getValueInCell(3, 3));
    }

    @Test
    public void testCalculate() {
        GameOfLifeField field = new GameOfLifeField(getTestData(), 5);
        field.calcColumn(0);
        field.finalize(0);
        //survive + stay dead + die of exposure
        assertEquals(0b1100011L << 57, field.getField()[0][1]);
        assertEquals(0b1100011L << 57, field.getField()[0][6]);
        //survive + die of overcrowding + bore
        assertEquals(0b1101111L << 57, field.getField()[0][4]);

        assertEquals(0b0001100L << 57, field.getField()[0][5]);
        assertEquals(0b0001100L << 57, field.getField()[0][0]);
        //to be born
        assertEquals(0b1100011L << 57, field.getField()[0][2]);
        //die of exposure
        assertEquals(0b0100001L << 57, field.getField()[0][3]);
    }

    @Test
    public void testGetInMediumTable() {
        GameOfLifeField field = new GameOfLifeField(new CellInputReader().readFile("src/main/resources/input100.txt"), 100);
        assertEquals(0, field.getValueInCell(0, 0));
        assertEquals(1, field.getValueInCell(99, 99));
        assertEquals(1, field.getValueInCell(29, 69));
        assertEquals(0, field.getValueInCell(29, 62));
        assertEquals(1, field.getValueInCell(79, 22));
    }
}
