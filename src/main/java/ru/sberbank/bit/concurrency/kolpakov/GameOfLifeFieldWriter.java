package ru.sberbank.bit.concurrency.kolpakov;

import java.util.stream.IntStream;

import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ONE;
import static ru.sberbank.bit.concurrency.kolpakov.GameOfLifeUtils.ZERO;

public class GameOfLifeFieldWriter {

    public char[] write(GameOfLifeField field) {
        char[] result = new char[field.getSize() * field.getSize()];
        IntStream.range(0, field.getSize() * field.getSize())
                .parallel()
                .forEach(i -> {
                    long value = field.getValue(i / field.getSize(), i % field.getSize());
                    if (value == 1L) {
                        result[i] = ONE;
                    } else if (value == 0L) {
                        result[i] = ZERO;
                    } else {
                        throw new RuntimeException("Unexpected value");
                    }
                });
        return result;
    }
}
